package software.amazon.ec2.transitgatewayroutetable;

import java.util.Map;
import java.util.Optional;
import java.util.Collections;
import java.util.stream.Collectors;
class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-ec2-transitgatewayroutetable.json");
    }

    @Override
    public Map<String, String> resourceDefinedTags(ResourceModel resourceModel) {
        return Optional.ofNullable(resourceModel.getTags()).orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(Tag::getKey, Tag::getValue, (value1, value2) -> value2));
    }
}
