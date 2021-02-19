package com.aws.ec2.transitgateway.workflow;

import com.aws.ec2.transitgateway.AbstractTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TagUtilsTest extends AbstractTestBase {

    @Test
    public void instance() {
        assertThat(new TagUtils().toString().contains("TagUtils")).isTrue();
    }

}
