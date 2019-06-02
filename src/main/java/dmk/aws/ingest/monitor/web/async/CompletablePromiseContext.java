package dmk.aws.ingest.monitor.web.async;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @deprecated no longer used
 */
public class CompletablePromiseContext {

    private ScheduledExecutorService service;

    public CompletablePromiseContext(ScheduledExecutorService service) {
        this.service = service;
    }

    public void schedule(Runnable r) {
        service.schedule(r, 1, TimeUnit.MILLISECONDS);
    }
}