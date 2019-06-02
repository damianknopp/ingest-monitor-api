package dmk.aws.ingest.monitor.web.model.lambda;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

public class UpdateLambdaEventSourceMapping {

    public UUID eventSourceMappingUuid;
    public String lambdaArn;

    public UpdateLambdaEventSourceMapping() {
        super();
    }

    public UUID getEventSourceMappingUuid() {
        return eventSourceMappingUuid;
    }

    public void setEventSourceMappingUuid(UUID eventSourceMappingUuid) {
        this.eventSourceMappingUuid = eventSourceMappingUuid;
    }

    public String getLambdaArn() {
        return lambdaArn;
    }

    public void setLambdaArn(String lambdaArn) {
        this.lambdaArn = lambdaArn;
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
