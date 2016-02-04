package org.jolokia.it.read

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
import org.junit.Test

import static org.hamcrest.Matchers.*

/**
 * @author roland
 * @since 04/02/16
 */
public class ReadGetIT extends BaseJolokiaTest {

    def attributeMBean = new AttributeChecking("jolokia.it");

    @Test
    void simple() {
        jolokiaGiven()
        .when()
                .get("/read/jolokia.it:type=attribute/LongSeconds")
        .then()
                .spec(jolokiaBaseSpec("read"))
                .body("value",equalTo((int) attributeMBean.getLongSeconds()))
    }

    @Test
    void withMultipleAttributes() {
        reset()

        jolokiaGiven().
               when()
                .get("/read/jolokia.it:type=attribute/Bytes,State")
              .then()
                .spec(jolokiaBaseSpec("read"))
                .body("value.Bytes",equalTo((int) attributeMBean.getBytes()))
                .body("value.State",equalTo(attributeMBean.getState()))
                .body("value.keySet().size()",equalTo(2))
    }



    void reset() {
        jolokiaGiven().get("/exec/jolokia.it:type=attribute/reset")
    }
}
