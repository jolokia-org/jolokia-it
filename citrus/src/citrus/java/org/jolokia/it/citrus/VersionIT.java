package org.jolokia.it.citrus;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

/**
 * This is a sample Citrus integration test for loading XML syntax test case.
 *
 * @author Citrus
 */
@Test
public class VersionIT extends AbstractJolokiaIT {

    @CitrusTest
    public void versionCheck() {
        variable("jolokia.version", System.getProperty("jolokia.version"));
        sendGet("/version");
        receiveAndValidateValue("config", "@ignore@",
                                 "agent", "${jolokia.version}");
    }

}
