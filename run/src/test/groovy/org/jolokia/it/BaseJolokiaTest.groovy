package org.jolokia.it

import com.consol.citrus.dsl.builder.HttpClientActionBuilder
import com.consol.citrus.dsl.builder.ReceiveMessageBuilder
import com.consol.citrus.dsl.endpoint.CitrusEndpoints
import com.consol.citrus.dsl.junit.JUnit4CitrusTestDesigner
import com.consol.citrus.http.client.HttpClient
import com.consol.citrus.message.MessageType
import org.junit.BeforeClass
import org.springframework.http.HttpStatus

import static org.hamcrest.Matchers.*
/**
 * @author roland
 * @since 11.06.14
 */
class BaseJolokiaTest extends JUnit4CitrusTestDesigner {

    private static HttpClient jolokiaClient;

    @BeforeClass
    static void setup() {
        jolokiaClient = CitrusEndpoints.http()
                                .client()
                                .requestUrl(System.getProperty("jolokia.url"))
                                .build();
    }

    protected ReceiveMessageBuilder jolokiaResponse(String type) {
        return jolokiaClient()
                 .response(HttpStatus.OK)
                 .contentType("@contains('text')@")
                 .messageType(MessageType.JSON)
                 .validate("\$.request.type", type)
                 .validate("\$.timestamp", lessThanOrEqualTo((Long) (System.currentTimeMillis() / 1000) + 1))
                 .validate("\$.status", 200)
                 .validate("\$.keySet()", hasItems("request","value","timestamp","status"));
    }

    protected HttpClientActionBuilder jolokiaClient() {
        return http().client(jolokiaClient);
    }
}
