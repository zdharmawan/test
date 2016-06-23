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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.util.ServerSpy;

public class BaseClientTest {

    protected ServerSpy serverSpy;
    protected MockRestServiceServer server;
    protected ApiClientConfiguration clientConfiguration = new ApiClientConfiguration(BASE_URL, HSDP_APPLICATION_NAME, HSDP_PROPOSITION_NAME, "", "", HSDP_API_VERSION);

    protected static final String HSDP_API_VERSION = "1.0";
    protected static final String HSDP_APPLICATION_NAME = "uGrowApp";
    protected static final String HSDP_PROPOSITION_NAME = "uGrowProp";
    protected static final String HSDP_VENDOR_NAME = "uGrow";
    protected static final String ERROR_JSON = "{\"responseCode\": \"1151\", \"responseMessage\":\"Error.\"}";
    protected static final String ERROR_NOT_SUBSCRIBED_TO_APPLICATION_OR_PROPOSITION_JSON = "{\"responseCode\": \"3756\", \"responseMessage\":\"Error.\"}";
    protected static final String PHOTO_NOT_FOUND_JSON = "{\"responseCode\": \"3597\", \"responseMessage\":\"Error.\"}";
    protected static final String TOKEN_EXPIRED_JSON = "{\"responseCode\": \"1008\", \"responseMessage\":\"Error.\"}";
    protected static final String ERROR_PHOTO_NOT_FOUND_JSON = "{\"responseCode\": \"3597\", \"responseMessage\":\"Error.\"}";
    protected static final String USER_ID = "testUserId";
    protected static final String SUBJECT_PROFILE_ID = "testSubjectProfileId";
    protected static final String BASE_URL = "http://example.org/api";

    protected void givenTheServerRespondsWith(String url, String responseBody) {
        server.expect(requestTo(url)).andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
    }

    protected void givenTheServerRespondsWith(String url, HttpMethod httpMethod, String responseBody) {
        server.expect(requestTo(url)).andExpect(method(httpMethod))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
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
