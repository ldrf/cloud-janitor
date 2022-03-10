package tasktree.aws.cleanup;

import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import tasktree.Configuration;
import tasktree.spi.Task;

import java.util.List;
import java.util.stream.Stream;

public class FilterSecurityGroups extends AWSFilter<SecurityGroup> {
    private String vpcId;

    public FilterSecurityGroups(String vpcId) {
        this.vpcId = vpcId;
    }

    private boolean match(SecurityGroup resource) {
        var notDefault = ! resource.groupName().equals("default");
        var targetVPC = resource.vpcId().equals(vpcId);
        var match = notDefault && targetVPC;
        log().debug("Found Security Group {} {}", mark(match), resource);
        return match;
    }

    @Override
    protected List<SecurityGroup> filterResources() {
        var client = newEC2Client();
        var resources = client.describeSecurityGroups().securityGroups();
        var matches = resources.stream().filter(this::match).toList();
        return matches;
    }

    @Override
    protected Stream<Task> mapSubtasks(SecurityGroup resource) {
        return Stream.of(new DeleteSecurityGroup(resource));
    }

    @Override
    protected String getResourceType() {
        return "Security Group";
    }

    @Override
    protected String toString(SecurityGroup securityGroup) {
        return securityGroup.groupId();
    }
}

