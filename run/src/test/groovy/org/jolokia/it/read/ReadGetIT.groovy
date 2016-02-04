package org.jolokia.it.read

import com.jayway.restassured.response.ValidatableResponse
import org.jolokia.it.AttributeChecking

/*
 * 
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

import org.jolokia.it.BaseJolokiaTest
import org.jolokia.it.Chili
import org.junit.Test

import static org.hamcrest.Matchers.*

/**
 * Read test, more or less equivalent to
 * https://github.com/rhuss/jmx4perl/blob/master/it/t/80_read.t
 *
 * @author roland
 * @since 04/02/16
 */
public class ReadGetIT extends BaseJolokiaTest {

    def attributeMBean = new AttributeChecking("jolokia.it");

    @Test
    void simple() {
        prepareReadThen("jolokia.it:type=attribute/LongSeconds")
                .body("value",equalTo((int) attributeMBean.getLongSeconds()))
    }

    @Test
    void withMultipleAttributes() {
        reset()
        prepareReadThen("jolokia.it:type=attribute/Bytes,State")
                .body("value.Bytes",equalTo((int) attributeMBean.getBytes()))
                .body("value.State",is(true))
                .body("value.size()",equalTo(2))
    }

    @Test
    void withAllAttributes() {
        reset()
        prepareReadThen("jolokia.it:type=attribute")
                .body("value.Bytes",equalTo((int) attributeMBean.getBytes()))
                .body("value.State",is(true))
                .body("value.LongSeconds",equalTo((int) attributeMBean.getLongSeconds()))
                .body("value.size()",greaterThan(3))
    }

    @Test
    void wildcardPatternWithAllAttributes() {
        prepareReadThen("jolokia.it:*")
                .body("value['jolokia.it:type=attribute'].Bytes",equalTo((int) attributeMBean.getBytes()))
                .body("value.size()",greaterThan(1))
    }

    @Test
    void wildcardPatternWithSomeAttributes() {
        reset()
        prepareReadThen("jolokia.it:*/Bytes,State")
                .body("value['jolokia.it:type=attribute'].Bytes",equalTo((int) attributeMBean.getBytes()))
                .body("value['jolokia.it:type=attribute'].State",is(true))
                .body("value['jolokia.it:type=attribute'].keySet().size()",equalTo(2))
                .body("value.size()",equalTo(1))
    }

    @Test
    void wildcardPatternWithSingleAttribute() {
        prepareReadThen("jolokia.it:*/SmallDouble")
                .body("value['jolokia.it:type=attribute'].SmallDouble",equalTo((float) attributeMBean.getSmallDouble()))
                .body("value['jolokia.it:type=attribute'].keySet().size()",equalTo(1))
                .body("value.size()",equalTo(1))
    }

    @Test
    void objectNameSerialization() {
        prepareReadThen("jolokia.it:type=attribute/ObjectName")
                .body("value.objectName",equalTo(attributeMBean.getObjectName().getCanonicalName()))
                .body("value.size()",equalTo(1))
    }

    @Test
    void setAsList() {
        prepareReadThen("jolokia.it:type=attribute/Set")
                .body("value",isA(List.class))
                .body("value",contains("habanero","jolokia"))
    }

    @Test
    void utf8() {
        prepareReadThen("jolokia.it:type=attribute/Utf8Content")
                .body("value",equalTo("☯"))
    }

    @Test
    void enumSerialization() {
        prepareReadThen("jolokia.it:type=attribute/Chili")
                .body("value",equalTo(Chili.AJI.name()))
    }

    @Test
    void jsonMbeanAll() {
        prepareReadThen("jolokia.it.jsonmbean:type=plain")
                .body("value.Data.set",contains(12,14))
                .body("value.keySet().size()",equalTo(1))
                .body("value.Data.size()",greaterThan(1))

    }

    @Test
    void tabularData() {
        prepareReadThen("jolokia.it:type=tabularData/Table2/Value0.0/Value0.1")
                .body("value.Column1",equalTo("Value0.0"))
                .body("value.Column2",equalTo("Value0.1"))
                .body("value.Column3",equalTo("Value0.2"))
                .body("value.size()",equalTo(3))
    }

    @Test
    void mxBeanWithComplexKey() {
        prepareReadThen("jolokia.it:type=mxbean/MapWithComplexKey")
                .body("value.size()",equalTo(2))
                .body("value.indexNames[0]",equalTo("key"))
                .body("value.values.size()",equalTo(2))
                .body("value.values[0].key.number",anyOf(equalTo(1),equalTo(2)))
    }

    // =============================================================================

    private ValidatableResponse prepareReadThen(String request) {
        return jolokiaGiven()
                .when()
                   .get("/read/" + request)
                .then()
                   .spec(jolokiaBaseSpec("read"))
    }

    private void reset() {
        // needed for any test accessing 'State'
        jolokiaGiven().get("/exec/jolokia.it:type=attribute/reset")
    }
}
