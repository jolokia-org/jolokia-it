package org.jolokia.it.misc

import org.junit.Test
import com.consol.citrus.annotations.CitrusTest
import org.jolokia.it.BaseJolokiaTest

import static org.hamcrest.Matchers.hasItems
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.startsWith

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

/**
 * @author roland
 * @since 02/10/16
 */
class HeaderIT extends BaseJolokiaTest {

  @Test
  @CitrusTest
  void version() {
    jolokiaClient().send().get("/version");
    jolokiaResponse("version")
            .validate("\$.value.agent", startsWith("1.3"))
            .validate("\$.value.protocol", notNullValue())
            .validate("\$.value.info.keySet()", hasItems("product", "vendor", "version"))
            .validate("\$.value.config", notNullValue())
            .extractFromPayload("\$.value.agent", "agent")
            .extractFromPayload("\$.value.protocol", "protocol")
            .extractFromPayload("\$.value.info.product", "info.product")
            .extractFromPayload("\$.value.info.version", "info.version")
            .extractFromPayload("\$.value.info.vendor", "info.vendor")
            .extractFromPayload("\$.value.config.agentType", "config.agentType");
  }
}
