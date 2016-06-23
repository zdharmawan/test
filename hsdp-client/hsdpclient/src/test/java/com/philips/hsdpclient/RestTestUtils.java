/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import java.io.IOException;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.RequestMatcher;

public abstract class RestTestUtils {
    public static RequestMatcher jsonBody(final String expectedJson) {
        return new RequestMatcher() {
            @Override
            public void match(ClientHttpRequest request) throws IOException, AssertionError {
                MockClientHttpRequest mockRequest = (MockClientHttpRequest) request;
                try {
                    JSONAssert.assertEquals(expectedJson, mockRequest.getBodyAsString(), true);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
