package org.jolokia.it.version

import org.jolokia.it.BaseJolokiaDocumentation
import com.consol.citrus.annotations.CitrusTest
import com.consol.citrus.message.MessageType
import org.junit.Test
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.snippet.Snippet

import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields

/*
 * 
 * Copyright 2015 Roland Huss
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
 * @since 03/02/16
 */
class VersionDoc extends BaseJolokiaDocumentation {

  @Test
  @CitrusTest
  public void get() {
    jolokiaClient(resp()).get("/version")
  }

  @Test
  @CitrusTest
  public void post() {
    jolokiaClient(resp(), req()).post()
            .messageType(MessageType.JSON)
            .payload('{ "type": "version" }')
  }

  static Snippet req() {
    return requestFields(commonRequestFields("version") as FieldDescriptor[]);
  }

  static Snippet resp() {
    return responseFields([
             *commonResponseFields(),
             f("value","Meta and configuration information about this agent"),
             f("value.protocol","Protocol version"),
             f("value.agent","Version of Jolokia agent"),
             f("value.info","Information about the server the agent is attached to"),
             f("value.config","Agent configuration in use")
            ] as FieldDescriptor[])
  }
}
