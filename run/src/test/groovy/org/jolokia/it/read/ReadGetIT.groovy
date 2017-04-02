package org.jolokia.it.read

import com.consol.citrus.annotations.CitrusTest

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
import com.consol.citrus.dsl.builder.ReceiveMessageBuilder
import org.jolokia.it.AttributeChecking
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
    @CitrusTest
    void simple() {
        prepareReadThen("jolokia.it:type=attribute/LongSeconds")
                .validate('$.value', attributeMBean.getLongSeconds());
    }

    @Test
    @CitrusTest
    void withMultipleAttributes() {
        reset()
        prepareReadThen("jolokia.it:type=attribute/Bytes,State")
                .validate('$.value.Bytes', attributeMBean.getBytes())
                .validate('$.value.State', true)
                .validate('$.value.size()', 2)
    }

    @Test
    @CitrusTest
    void withAllAttributes() {
        reset()
        prepareReadThen("jolokia.it:type=attribute")
                .validate('$.value.Bytes', attributeMBean.getBytes())
                .validate('$.value.State', true)
                .validate('$.value.LongSeconds', attributeMBean.getLongSeconds())
                .validate('$.value.size()', greaterThan(3))
    }

    @Test
    @CitrusTest
    void wildcardPatternWithAllAttributes() {
        prepareReadThen("jolokia.it:*")
                .validate('$.value["jolokia.it:type=attribute"].Bytes', attributeMBean.getBytes())
                .validate('$.value.size()', greaterThan(1))
    }

    @Test
    @CitrusTest
    void wildcardPatternWithSomeAttributes() {
        reset()
        prepareReadThen("jolokia.it:*/Bytes,State")
                .validate('$.value["jolokia.it:type=attribute"].Bytes', attributeMBean.getBytes())
                .validate('$.value["jolokia.it:type=attribute"].State', true)
                .validate('$.value["jolokia.it:type=attribute"].keySet()', hasSize(2))
                .validate('$.value.size()', 1)
    }

    @Test
    @CitrusTest
    void wildcardPatternWithSingleAttribute() {
        prepareReadThen("jolokia.it:*/SmallDouble")
                .validate('$.value["jolokia.it:type=attribute"].SmallDouble', attributeMBean.getSmallDouble())
                .validate('$.value["jolokia.it:type=attribute"].keySet()', hasSize(1))
                .validate('$.value.size()', 1)
    }

    @Test
    @CitrusTest
    void objectNameSerialization() {
        prepareReadThen("jolokia.it:type=attribute/ObjectName")
                .validate('$.value.objectName', attributeMBean.getObjectName().getCanonicalName())
                .validate('$.value.size()', 1)
    }

    @Test
    @CitrusTest
    void setAsList() {
        prepareReadThen("jolokia.it:type=attribute/Set")
                .validate('$.value', contains("habanero", "jolokia"))
    }

    @Test
    @CitrusTest
    void utf8() {
        prepareReadThen("jolokia.it:type=attribute/Utf8Content")
                .validate('$.value', "☯")
    }

    @Test
    @CitrusTest
    void enumSerialization() {
        prepareReadThen("jolokia.it:type=attribute/Chili")
                .validate('$.value', Chili.AJI.name())
    }

    @Test
    @CitrusTest
    void jsonMbeanAll() {
        prepareReadThen("jolokia.it.jsonmbean:type=plain")
                .validate('$.value.Data.set', contains(12L, 14L))
                .validate('$.value.keySet()', hasSize(1))
                .validate('$.value.Data.size()', greaterThan(1))
    }

    @Test
    @CitrusTest
    void tabularData() {
        prepareReadThen("jolokia.it:type=tabularData/Table2/Value0.0/Value0.1")
                .validate('$.value.Column1', "Value0.0")
                .validate('$.value.Column2', "Value0.1")
                .validate('$.value.Column3', "Value0.2")
                .validate('$.value.size()', 3)
    }

    @Test
    @CitrusTest
    void mxBeanWithComplexKey() {
        prepareReadThen("jolokia.it:type=mxbean/MapWithComplexKey")
                .validate('$.value.size()', 2)
                .validate('$.value.indexNames[0]', "key")
                .validate('$.value.values.size()', 2)
                .validate('$.value.values[0].key.number', anyOf(equalTo(1L),equalTo(2L)));
    }

    // =============================================================================

    private ReceiveMessageBuilder prepareReadThen(String request) {
        jolokiaClient().send().get("/read/" + request);

        return jolokiaResponse("read");
    }

    private void reset() {
        // needed for any test accessing 'State'
        jolokiaClient().send().get("/exec/jolokia.it:type=attribute/reset")
    }
}
