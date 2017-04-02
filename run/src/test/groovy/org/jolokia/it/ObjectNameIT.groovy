package org.jolokia.it

import com.consol.citrus.annotations.CitrusTest
import groovy.json.JsonOutput
import org.hamcrest.Matcher
import org.junit.Test

import java.util.regex.Pattern

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.*

/**
 * @author roland
 * @since 02/04/2017
 */
class ObjectNameIT extends BaseJolokiaTest {

    def names = [
            "simple",
            "/slash-simple/",
            "/--/",
            "with%3acolon",
            "//server/client",
            "service%3ajmx%3armi%3a///jndi/rmi%3a//bhut%3a9999/jmxrmi",
            "name with space",
            "n!a!m!e with !/!"
    ]

    @Test
    @CitrusTest
    void namesGet() {
        names.forEach({
            // URL ending with a slash don't work with the citrus client which squeezes slashes
            // (which happens for trailing slashes which are combined with the
            // See https://github.com/christophd/citrus/issues/231 for a fix requested
            if (!it.endsWith("/")) {
                def reqUrl = "/read/" + escape("jolokia.it:type=naming/,name=" + it) + "/Ok";
                jolokiaClient().send().get(reqUrl);
                jolokiaResponse("read")
                        .validate('$.value', equalTo("OK"))
            }
        })
    }

    @Test
    @CitrusTest
    void namesPost() {
        names.forEach({
            jolokiaClient().send().post().payload(toJson([
                    type: "read",
                    mbean: "jolokia.it:type=naming/,name=" + it,
                    attribute: "Ok"
            ]))
            jolokiaResponse("read")
                    .validate('$.value', equalTo("OK"))
        })
    }

    @Test
    @CitrusTest
    void search() {
        jolokiaClient().send().get("/search/" + escape("*:name=//server/client,*"))
        jolokiaResponse("search")
                .validate('$.value', hasSize(2))

        jolokiaClient().send().post().payload(toJson([
                type: "search",
                mbean: "*:name=//server/client,*"
        ]))
        jolokiaResponse("search")
                .validate('$.value', hasSize(2))
    }

    private static String escape(String s) {
        String ret = Pattern.compile("!").matcher(s).replaceAll("!!")
        return Pattern.compile("/").matcher(ret).replaceAll("!/");
    }
}
