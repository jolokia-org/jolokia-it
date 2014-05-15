package org.jolokia.it.citrus;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

/**
 * @author roland
 * @since 18.03.14
 */
@Test
public class GetIT extends AbstractJolokiaIT {

    @CitrusTest
    public void simpleGet() {
        sendGet("/read/java.lang:type=Memory");
        receiveAndValidateValue("HeapMemoryUsage", "@ignore@");
    }

    @CitrusTest
    public void simpleAttribute() {
        sendGet("/read/jolokia.it:type=attribute");
        receiveAndValidateValue("LongSeconds",(float) 60*60*24*2,
                                "Bytes",3 * 1024 * 1024 +  1024 * 512,
                                "Null",null);

    }
}

