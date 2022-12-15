package cj.aws.repeat;
import cj.BaseTask;
import cj.CJInput;
import cj.aws.AWSClientsManager;
import cj.aws.AWSIdentity;
import cj.aws.AWSOutput;
import cj.aws.filter.FilterRegions;
import software.amazon.awssdk.regions.Region;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static cj.aws.AWSInput.identity;
import static cj.aws.AWSInput.regions;

@Dependent
public class RepeatPerRegionTask extends BaseTask {
    @Inject
    AWSClientsManager aws;

    @Inject
    FilterRegions filterRegions;


    @Override
    public void apply() {
        var query = inputList(CJInput.query, String.class);
        var ids = aws.identities();
        var regions = submit(filterRegions)
                .outputList(AWSOutput.RegionMatches, Region.class);
        for (var id: ids){
            for (var region: regions){
                debug("Repeating [{}] as [{}]@[{}]", query, id, region);
                submitQuery(query, List.of(region), id);
            }
        }
    }

    private void submitQuery(List<String> query, List<Region> regionList, AWSIdentity id) {
        tasks().submitQuery(query, Map.of(
                regions, regionList,
                identity, id
        ));
    }
}
