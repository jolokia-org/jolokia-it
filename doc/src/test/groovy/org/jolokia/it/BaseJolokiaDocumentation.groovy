package org.jolokia.it

import com.consol.citrus.dsl.builder.HttpClientActionBuilder
import com.consol.citrus.dsl.endpoint.CitrusEndpoints
import com.consol.citrus.dsl.junit.JUnit4CitrusTestDesigner
import com.consol.citrus.http.client.HttpClient
import com.consol.citrus.restdocs.RestDocClientInterceptor
import org.junit.Before
import org.junit.Rule
import org.springframework.http.HttpHeaders
import org.springframework.restdocs.RestDocumentation
import org.springframework.restdocs.operation.OperationRequest
import org.springframework.restdocs.operation.OperationRequestFactory
import org.springframework.restdocs.operation.OperationResponse
import org.springframework.restdocs.operation.OperationResponseFactory
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.snippet.Snippet

import static com.consol.citrus.restdocs.CitrusRestDocsSupport.*
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
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
class BaseJolokiaDocumentation extends JUnit4CitrusTestDesigner {

  @Rule
  public RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");

  private static HttpClient jolokiaClient;

  private RestDocClientInterceptor doc;

  @Before
  public void setUp() {
    doc = restDocsInterceptor("{class-name}/{method-name}", preprocessRequest(prettyPrint(), manageHeaders()), preprocessResponse(prettyPrint(), manageHeaders()));

    jolokiaClient = CitrusEndpoints.http()
            .client()
            .interceptors(Arrays.asList(restDocsConfigurer(this.restDocumentation), doc))
            .requestUrl(System.getProperty("jolokia.url"))
            .build();
  }

  // Can be overridden
  OperationPreprocessor manageHeaders() {
    return removeAllHeaders();
  }

  // ==========================================================================

  private OperationPreprocessor removeAllHeaders() {
    return new OperationPreprocessor() {

      @Override
      public OperationResponse preprocess(OperationResponse response) {
        return new OperationResponseFactory().createFrom(response, new HttpHeaders());
      }

      @Override
      public OperationRequest preprocess(OperationRequest request) {
        return new OperationRequestFactory().createFrom(request, new HttpHeaders());
      }
    }
  }

  protected HttpClientActionBuilder jolokiaClient(Snippet ... docSnippets) {
    if (docSnippets.length > 0) {
      doc.snippets(docSnippets)
    }

    return http().client(jolokiaClient);
  }

  static List<FieldDescriptor> commonResponseFields() {
    return [
            f("timestamp","Timestamp when response was created servers side"),
            f("status","Status code for JMX operation (200 : ok)"),
            f("request","Original request repeated")
    ];
  }

  static List<FieldDescriptor> commonRequestFields(String type) {
    return [
            f("type","Request type")
    ]
  }

  static FieldDescriptor f(String path, String description) {
    return fieldWithPath(path).description(description);
  }
}
