package org.jolokia.it

import io.restassured.builder.ResponseSpecBuilder
import io.restassured.parsing.Parser
import org.junit.BeforeClass

import static io.restassured.RestAssured.*
import io.restassured.specification.*;
import static io.restassured.http.ContentType.*;
import static org.hamcrest.Matchers.*

/**
 *
 * @author roland
 * @since 11.06.14
 */
class BaseJolokiaTest {

  protected ResponseSpecification jolokiaBaseSpec(String type) {
    return new ResponseSpecBuilder().
            expectStatusCode(200).
            expectHeader("content-type", containsString(TEXT.toString())).
            expectBody("request.type", equalTo(type)).
            expectBody("timestamp", lessThanOrEqualTo((Integer) (System.currentTimeMillis() / 1000))).
            expectBody("status", equalTo(200)).
            expectBody("keySet()", hasItems("request","value","timestamp","status")).

            build();
  }

  @BeforeClass
  public static void setupParser() {
    registerParser("text/plain", Parser.JSON);
  }

  protected RequestSpecification jolokiaGiven() {
    return jolokiaGiven(null);
  }

  protected RequestSpecification jolokiaGiven(RequestSpecification spec) {
    return (spec != null ? given(spec) : given()).
            baseUri(System.getProperty("jolokia.url"));
  }
}
