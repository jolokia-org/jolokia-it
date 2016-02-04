package org.jolokia.it

import com.jayway.restassured.builder.RequestSpecBuilder
import com.jayway.restassured.specification.RequestSpecification
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
import org.springframework.restdocs.restassured.RestDocumentationFilter
import org.springframework.restdocs.snippet.Snippet

import static com.jayway.restassured.RestAssured.given
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration

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
class BaseJolokiaDocumentation {

  @Rule
  public RestDocumentation restDocumentation = new RestDocumentation("target/generated-snippets");
  private RequestSpecification spec;

  RestDocumentationFilter doc;

  @Before
  public void setUp() {
    this.spec = new RequestSpecBuilder().
            addFilter(documentationConfiguration(this.restDocumentation)).
            build();
    this.doc = document "{class-name}/{method-name}",
               preprocessRequest(prettyPrint(), manageHeaders()),
               preprocessResponse(prettyPrint(), manageHeaders())
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

  RequestSpecification jolokiaGiven(Snippet ... docSnippets) {
    if (docSnippets.length > 0) {
      doc.snippets(docSnippets)
    }
    spec.filter(doc)
    return given(spec).baseUri(System.getProperty("jolokia.url"))
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
