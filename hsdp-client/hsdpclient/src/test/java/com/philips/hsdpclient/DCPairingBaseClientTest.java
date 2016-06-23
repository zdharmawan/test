/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.util.ServerSpy;

public class DCPairingBaseClientTest {
    protected ServerSpy serverSpy;
    protected MockRestServiceServer server;
    protected ApiClientConfigurationBasicAuthentication clientConfiguration =
                                                                              new ApiClientConfigurationBasicAuthentication(BASE_URL, USERNAME, "", API_VERSION);

    protected static final String USER_ID = "6093d435-81e5-46d5-aada-39a375761e99";
    protected static final String DEVICE_ID = "testDeviceId";
    protected static final String USERNAME = "SomeUser";
    protected static final String API_VERSION = "1";

    protected static final String ERROR_CREATING_ALREADY_EXISTS_JSON = "{\"issue\":[{\"Severity\":\"information\",\"Code\":{\"coding\":[{\"system\":\"Pairing\",\"code\":\"200\"}]},\"Details\":\"The resource is already existing. Post is ignored.\"}]}";
    protected static final String ERROR_CREATING_INVALID_REQUEST_BODY_JSON = "{\"issue\":[{\"Severity\":\"error\",\"Code\":{\"coding\":[{\"system\":\"Pairing\",\"code\":\"23138\"}]},\"Details\":\"The ResourceType is not valid\"}]}";
    protected static final String ERROR_DELETING_NO_RELATION_FOUND_JSON = "{\"issue\":[{\"Severity\":\"information\",\"Code\":{\"coding\":[{\"system\":\"Pairing\",\"code\":\"404\"}]},\"Details\":\"No relation resource could be found to delete.\"}]}";
    protected static final String ERROR_UPDATING_FORBIDDEN_JSON = "{\"issue\":[{\"Severity\":\"error\",\"Code\":{\"coding\":[{\"system\":\"Pairing\",\"code\":\"403\"}]},\"Details\":\"Forbidden. The provided search parameters do not allow the update of the resource\"}]}";
    protected static final String ERROR_UPSERTING_INVALID_TRUSTEE_JSON = "{\"issue\":[{\"Severity\":\"error\",\"Code\":{\"coding\":[{\"system\":\"Pairing\",\"code\":\"22369\"}]},\"Details\":\"Trustee Id not valid\"}]}";

    protected static final String BASE_URL = "http://example.org/api";

    protected void givenTheServerRespondsWithHeader(String url, String responseBody, String locationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", locationHeader);
        server.expect(requestTo(url)).andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON).headers(headers));
    }

    protected void givenTheServerRespondsWith(String url, String responseBody) {
        server.expect(requestTo(url)).andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }

    protected void givenTheServerRespondsWithError(String url) {
        server.expect(requestTo(url)).andRespond(withServerError());
    }

    protected void givenTheServerRespondsWith404Status(String url) {
        server.expect(requestTo(url)).andRespond(withStatus(HttpStatus.NOT_FOUND));
    }

    protected void thenTheSentBodyIs(String expectedBody) {
        assertEquals(expectedBody, serverSpy.sentBody);
    }

    protected void andTheSentBodyIs(String expectedBody) {
        thenTheSentBodyIs(expectedBody);
    }

    protected void andHeaderIsSent(String name) {
        assertNotNull(serverSpy.getSentHeader(name));
    }

    protected void andHeaderIsSent(String name, String value) {
        andHeaderIsSent(name);
        assertEquals(value, serverSpy.getSentHeader(name));
    }

    protected void andTheUsedMethodIs(HttpMethod expectedMethod) {
        assertEquals(expectedMethod, serverSpy.httpMethod);
    }
}
