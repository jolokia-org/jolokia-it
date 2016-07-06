package org.jolokia.it.version

import com.consol.citrus.actions.AbstractTestAction
import com.consol.citrus.annotations.CitrusTest
import com.consol.citrus.context.TestContext
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.jolokia.it.BaseJolokiaTest
import org.junit.BeforeClass
import org.junit.Test

import static org.fusesource.jansi.Ansi.Color.*
import static org.fusesource.jansi.Ansi.ansi
import static org.hamcrest.Matchers.*

/**
 * @author roland
 * @since 11.06.14
 */
public class VersionIT extends BaseJolokiaTest {

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
    @CitrusTest
    void version() {
        jolokiaClient().get("/version");
        jolokiaResponse("version")
                .validate("\$.value.agent", System.getProperty("jolokia.version"))
                .validate("\$.value.protocol", notNullValue())
                .validate("\$.value.info.keySet()", hasItems("product", "vendor", "version"))
                .validate("\$.value.config", notNullValue())
                .extractFromPayload("\$.value.agent", "agent")
                .extractFromPayload("\$.value.protocol", "protocol")
                .extractFromPayload("\$.value.info.product", "info.product")
                .extractFromPayload("\$.value.info.version", "info.version")
                .extractFromPayload("\$.value.info.vendor", "info.vendor")
                .extractFromPayload("\$.value.config.agentType", "config.agentType");

        printInfo()
    }

    private printInfo() {
        action(new AbstractTestAction() {
            @Override
            void doExecute(TestContext context) {
                println """
$SEP
$P ${c("Jolokia", CYAN)} ${c(context.getVariable("agent"), CYAN)}
$P
$P Server:   ${c(context.getVariable("info.product"), YELLOW)} ${c(context.getVariable("info.version"), YELLOW)} (${context.getVariable("info.vendor")})
$P Protocol: ${context.getVariable("protocol")}
$P Type:     ${context.getVariable("config.agentType")}
$SEP
"""
            }
        });
    }

    private static String c(String msg, Ansi.Color color) {
        Ansi ansi = ansi().fg(color)
        return ansi.a(msg).reset().toString()
    }
}
