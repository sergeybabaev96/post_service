package faang.school.postservice.util.ci.pipeline.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class FailedTest {

    @Test
    public void testShouldFail () {
        fail("This test is falling");
    }
}
