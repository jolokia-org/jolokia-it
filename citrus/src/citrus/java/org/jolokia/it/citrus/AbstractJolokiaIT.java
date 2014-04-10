package org.jolokia.it.citrus;

import com.consol.citrus.dsl.TestNGCitrusTestBuilder;
import com.consol.citrus.dsl.definition.SendHttpMessageActionDefinition;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.validation.MessageValidator;
import com.consol.citrus.validation.json.JsonTextMessageValidator;
import org.json.simple.JSONObject;
import org.springframework.http.HttpMethod;

/**
 * @author roland
 * @since 01.04.14
 */
abstract public class AbstractJolokiaIT extends TestNGCitrusTestBuilder {

    private String httpClient;

    protected AbstractJolokiaIT() {
        httpClient = "jolokiaHttpClient";
    }

    protected void receiveAndValidateValue(Object ... pValues) {
        receiveAndValidateValue(new JsonTextMessageValidator().strict(false), pValues);
    }

    protected void receiveAndValidateValue(MessageValidator pValidator, Object ... pValues) {
        receive(httpClient)
                .messageType(MessageType.JSON)
                .validator(pValidator)
                .payload(createPayload(pValues));
    }

    protected SendHttpMessageActionDefinition send() {
        return send(httpClient).http();
    }

    protected SendHttpMessageActionDefinition sendGet(String pPath) {
        return send().method(HttpMethod.GET).path(pPath);
    }

    protected String createPayload(Object ... pKeyValues) {
        JSONObject value = new JSONObject();
        for (int i = 0; i < pKeyValues.length; i += 2) {
            value.put(pKeyValues[i].toString(),pKeyValues[i+1]);
        }
        JSONObject ret = new JSONObject();
        ret.put("value",value);
        return ret.toJSONString();
    }

}
