package dmk.aws.ingest.monitor.web.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @deprecated no longer used
 * @param <V>
 */
public class CompletablePromise<V> extends CompletableFuture<V> {
    private Future<V> future;
    private CompletablePromiseContext context;

    private CompletablePromise(Future<V> future, CompletablePromiseContext context) {
        this.future = future;
        this.context = context;
        context.schedule(this::tryToComplete);
    }

    public static <V> CompletablePromise<V> create(Future<V> future, CompletablePromiseContext context) {
        return new CompletablePromise(future, context);
    }

    private void tryToComplete() {
        if (future.isDone()) {
            try {
                complete(future.get());
            } catch (InterruptedException e) {
                completeExceptionally(e);
            } catch (ExecutionException e) {
                completeExceptionally(e.getCause());
            }
            return;
        }

        if (future.isCancelled()) {
            cancel(true);
            return;
        }

        context.schedule(this::tryToComplete);
    }
}
