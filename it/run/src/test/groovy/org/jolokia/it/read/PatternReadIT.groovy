package org.jolokia.it.read

import com.jayway.restassured.RestAssured
import com.jayway.restassured.parsing.Parser
import com.jayway.restassured.path.json.JsonPath
import org.jolokia.it.BaseJolokiaTest
import org.junit.Test

import static org.hamcrest.CoreMatchers.*

/**
 *
 * @author roland
 * @since 11.06.14
 */
class PatternReadIT extends BaseJolokiaTest {

  @Test
  void simple() {
    def versionExpected = System.getProperty("jolokia.version");
    def jolokiaUrl = System.getProperty("jolokia.url");

    RestAssured.baseURI = jolokiaUrl;
    RestAssured.defaultParser = Parser.JSON;
    System.out.println("Checking URL: " + jolokiaUrl);

    // Need to do it that way since Jolokia doesnt return application/json as mimetype by default
    JsonPath json = with(get("/version").asString());
    json.prettyPrint();
    assertEquals(versionExpected, json.get("value.agent"));

    // Alternatively, set the mime type before, then Rest-assured's fluent API can be used
    given()
            .param("mimeType", "application/json")
            .get("/version")
            .then().assertThat()
            .header("content-type", containsString("application/json"))
            .body("value.agent", equalTo(versionExpected))
            .body("timestamp", lessThan((int) (System.currentTimeMillis() / 1000)))
            .body("status", equalTo(200))
            .body("value.protocol", equalTo("7.1"))
            .body("value.config",notNullValue());
  }
}
