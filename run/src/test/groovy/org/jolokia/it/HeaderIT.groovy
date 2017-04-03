package org.jolokia.it

import com.consol.citrus.annotations.CitrusTest
import org.jolokia.it.BaseJolokiaTest
import org.junit.Test
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
      .extractFromHeader('Date', 'date')
      .header('Expires', '@lowerThan(${date})@');
  }

  @Test
  @CitrusTest
  void versionWithScript() {
    jolokiaClient().send().get("/version");
    
    jolokiaResponse("version")
      .validator("defaultGroovyTextMessageValidator")
      .validateScript('''
        import org.junit.Assert
        import java.time.*;
        import java.time.format.DateTimeFormatter;

        def date = headers.get('Date')
        def expires = headers.get('Expires')
        
        Assert.assertTrue('<Expires> header value not less than <Date> header', expires < date)
        Assert.assertTrue('<Expires> header does not match RFC-1123 format', OffsetDateTime.of(LocalDateTime.ofInstant(Instant.ofEpochMilli(1L), ZoneOffset.UTC), ZoneOffset.UTC)
                                                                                            .format(DateTimeFormatter.RFC_1123_DATE_TIME).toString()
                                                                                            .matches("\\\\w{3}, \\\\d{1,2} \\\\w{3} \\\\d{4} \\\\d{2}:\\\\d{2}:\\\\d{2} GMT"))
      ''');
  }
}
