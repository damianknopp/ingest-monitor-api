package dmk.aws.ingest.monitor.web.model.lambda;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UpdateLambdaConcurrency {

    public int concurrency;
    public String lambdaName;

    public UpdateLambdaConcurrency() {
        super();
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public String getLambdaName() {
        return lambdaName;
    }

    public void setLambdaName(String lambdaName) {
        this.lambdaName = lambdaName;
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }
}
