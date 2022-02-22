package tasktree.aws.cleanup;

import software.amazon.awssdk.regions.Region;
import tasktree.Configuration;
import tasktree.aws.AWSTask;

public class CleanupRegion extends AWSTask {
    public CleanupRegion(Configuration config, Region region) {
        super(config, region);
    }

    @Override
    public void run() {
        push(new FilterInstances());
        push(new FilterNATGateways());
    }
}
