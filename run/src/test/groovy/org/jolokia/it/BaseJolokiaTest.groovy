package org.jolokia.it

import com.jayway.restassured.RestAssured
import com.jayway.restassured.builder.RequestSpecBuilder
import com.jayway.restassured.builder.ResponseSpecBuilder
import com.jayway.restassured.parsing.Parser
import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.specification.ResponseSpecification
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.junit.Before
import org.junit.BeforeClass

import static com.jayway.restassured.RestAssured.*
import static com.jayway.restassured.http.ContentType.JSON
import static com.jayway.restassured.http.ContentType.TEXT
import static com.jayway.restassured.matcher.RestAssuredMatchers.*
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
