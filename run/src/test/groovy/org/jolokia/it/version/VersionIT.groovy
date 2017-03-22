package org.jolokia.it.version

import io.restassured.response.Response
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.jolokia.it.BaseJolokiaTest
import org.junit.BeforeClass
import org.junit.Test

import static org.fusesource.jansi.Ansi.Color.*
import static org.fusesource.jansi.Ansi.ansi
import static org.hamcrest.Matchers.*

/**
 *
 * @author roland
 * @since 11.06.14
 */
class VersionIT extends BaseJolokiaTest {


  private String SEP = c("====================================================",BLUE)
  private String P = c("===",BLUE)

  @BeforeClass
  static void initColor() {
    if (System.console() != null) {
      AnsiConsole.systemInstall()
      Ansi.setEnabled(true)
    }
  }

  @Test
  void version() {
    Response resp =
    jolokiaGiven().
    when().
            get("/version").
    then().
            //log().all().
            spec(jolokiaBaseSpec("version")).
            root("value").
            body("agent", equalTo(System.getProperty("jolokia.version").replaceAll("-SNAPSHOT", ""))).
            body("protocol", notNullValue()).
            body("info.keySet()",hasItems("product", "vendor", "version")).
            body("config",notNullValue()).
    extract().
            response();

    printInfo(resp)

  }

  private printInfo(Response resp) {
    println """
$SEP
$P ${c("Jolokia", CYAN)} ${c(resp, "agent", CYAN)}
$P
$P Server:   ${c(resp, "info.product", YELLOW)} ${c(resp, "info.version", YELLOW)} (${resp.path("value.info.vendor")})
$P Protocol: ${resp.path("value.protocol")}
$P Type:     ${resp.path("value.config.agentType")}
$SEP
"""
  }

  private static String c(String msg, Ansi.Color color) {
    Ansi ansi = ansi().fg(color)
    return ansi.a(msg).reset().toString()
  }

  private static String c(Response response, String key, Ansi.Color color) {
    return c(response.path("value." + key).toString(),color);
  }
}
