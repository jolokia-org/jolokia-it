package org.jolokia.it

import com.consol.citrus.annotations.CitrusTest
import groovy.json.JsonOutput

/*
 * Copyright 2016 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.junit.Test

import static org.hamcrest.Matchers.*

/**
 * Read test, more or less equivalent to
 * https://github.com/rhuss/jmx4perl/blob/master/it/t/80_read.t
 *
 * @author roland
 * @since 04/02/16
 */
class ReadIT extends BaseJolokiaTest {

    public static final String D                   = "jolokia.it"
    public static final        ATTR_MBEAN          = "${D}:type=attribute"
    public static final        ATTR_WILDCARD_MBEAN = "${D}:*"

    def attributeMBean = new AttributeChecking(D);

    ReadIT() {
    }

    @Test
    @CitrusTest
    void simple() {
        check("${ATTR_MBEAN}/LongSeconds",
              [
                   mbean: "${ATTR_MBEAN}",
                   attribute: "LongSeconds"
              ],
              '$.value': attributeMBean.getLongSeconds()
        )
    }

    @Test
    @CitrusTest
    void withMultipleAttributes() {
        check("${ATTR_MBEAN}/Bytes,State",
              [
                  mbean: "${ATTR_MBEAN}",
                  attribute: ["Bytes", "State"]
              ],
              '$.value.Bytes': attributeMBean.getBytes(),
              '$.value.State': true,
              '$.value.size()': 2
        )
    }

    @Test
    @CitrusTest
    void withAllAttributes() {
        check("${ATTR_MBEAN}",
              [
                  mbean: "${ATTR_MBEAN}",
              ],
              '$.value.Bytes': attributeMBean.getBytes(),
              '$.value.State': true,
              '$.value.LongSeconds': attributeMBean.getLongSeconds(),
              '$.value.size()': greaterThan(3)
        )
    }

    @Test
    @CitrusTest
    void wildcardPatternWithAllAttributes() {
        check("${ATTR_WILDCARD_MBEAN}",
              [
                  mbean: "${ATTR_WILDCARD_MBEAN}"
              ],
              '$.value["jolokia.it:type=attribute"].Bytes': attributeMBean.getBytes(),
              '$.value.size()': greaterThan(1)
        )
    }

    @Test
    @CitrusTest
    void wildcardPatternWithSomeAttributes() {
        check("${ATTR_WILDCARD_MBEAN}/Bytes,State",
              [
                  mbean: "${ATTR_WILDCARD_MBEAN}",
                  attribute: ["Bytes", "State"]
              ],
              '$.value["jolokia.it:type=attribute"].Bytes': attributeMBean.getBytes(),
              '$.value["jolokia.it:type=attribute"].State': true,
              '$.value["jolokia.it:type=attribute"].keySet()': hasSize(2),
              '$.value.size()': 1
        )
    }

    @Test
    @CitrusTest
    void wildcardPatternWithSingleAttribute() {
        check("${ATTR_WILDCARD_MBEAN}/SmallDouble",
              [
                  mbean: "${ATTR_WILDCARD_MBEAN}",
                  attribute: "SmallDouble"
              ],
              '$.value["jolokia.it:type=attribute"].SmallDouble': attributeMBean.getSmallDouble(),
              '$.value["jolokia.it:type=attribute"].keySet()': hasSize(1),
              '$.value.size()': 1
        )
    }

    @Test
    @CitrusTest
    void objectNameSerialization() {
        check("${ATTR_MBEAN}/ObjectName",
              [
                  mbean: "${ATTR_MBEAN}",
                  attribute: "ObjectName"
              ],
              '$.value.objectName': attributeMBean.getObjectName().getCanonicalName(),
              '$.value.size()': 1
        )
    }

    @Test
    @CitrusTest
    void setAsList() {
        check("${ATTR_MBEAN}/Set",
              [
                  mbean: "${ATTR_MBEAN}",
                  attribute: "Set"
              ],
              '$.value': contains("habanero", "jolokia")
        )
    }

    @Test
    @CitrusTest
    void utf8() {
        check("${ATTR_MBEAN}/Utf8Content",
              [
                  mbean: "${ATTR_MBEAN}",
                  attribute: "Utf8Content"
              ],
              '$.value': "☯"
        )
    }

    @Test
    @CitrusTest
    void enumSerialization() {
        check("${ATTR_MBEAN}/Chili",
              [
                  mbean: "${ATTR_MBEAN}",
                  attribute: "Chili"
              ],
              '$.value': Chili.AJI.name()
        )
    }

    @Test
    @CitrusTest
    void jsonMbeanAll() {
        check("jolokia.it.jsonmbean:type=plain",
              [
                  mbean: "jolokia.it.jsonmbean:type=plain"
              ],
              '$.value.Data.set': contains(12L, 14L),
              '$.value.keySet()': hasSize(1),
              '$.value.Data.size()': greaterThan(1)
        )
    }

    @Test
    @CitrusTest
    void tabularData() {
        check("jolokia.it:type=tabularData/Table2/Value0.0/Value0.1",
              [
                  mbean: "jolokia.it:type=tabularData",
                  attribute: "Table2",
                  path: "Value0.0/Value0.1"
              ],
              '$.value.Column1': "Value0.0",
              '$.value.Column2': "Value0.1",
              '$.value.Column3': "Value0.2",
              '$.value.size()': 3
        )
    }

    @Test
    @CitrusTest
    void mxBeanWithComplexKey() {
        check("jolokia.it:type=mxbean/MapWithComplexKey",
              [
                  mbean: "jolokia.it:type=mxbean",
                  attribute: "MapWithComplexKey"
              ],
              '$.value.size()': 2,
              '$.value.indexNames[0]': "key",
              '$.value.values.size()': 2,
              '$.value.values[0].key.number': anyOf(equalTo(1L), equalTo(2L))
        )
    }

    // =============================================================================

    void check(Map validation, String request, Map postJson) {
        reset();
        jolokiaClient().send().get("/read/" + request);
        def response = jolokiaResponse("read");
        validation.each { path, check -> response.validate(path, check) }

        reset();
        postJson.type = "read";
        jolokiaClient().send().post().payload(JsonOutput.toJson(postJson));
        response = jolokiaResponse("read");
        validation.each { path, check -> response.validate(path, check) }
    }

    private void reset() {
        // needed for any test accessing 'State'
        jolokiaClient().send().get("/exec/jolokia.it:type=attribute/reset")
    }
}
