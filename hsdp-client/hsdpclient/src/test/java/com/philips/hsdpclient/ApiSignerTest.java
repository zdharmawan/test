/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class ApiSignerTest {
    @Test(expected = RuntimeException.class)
    public void throwsFatalException_missingKeys() {
        new ApiSigner(null, null);
    }

    @Test
    public void getAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(com.google.common.net.HttpHeaders.CONTENT_TYPE, " application/json;Content-Length: 10");
        headers.add("SignedDate", "2015-07-02T07:52:03.100+0000");
        String result = new ApiSigner("sharedKey", "secretKey").buildAuthorizationHeaderValue(
                HttpMethod.POST,
                "foo=bar&bar=foo",
                headers,
                "http://user-registration-service.cloud.pcftest.com",
                "BLAA");

        assertEquals("HmacSHA256;Credential:sharedKey;SignedHeaders:Content-Type,Content-Length,SignedDate;Signature:F539WeAGXNJJ/p9N51vfpzS2elZ6ebRh14pHO/9ETa0=", result);
    }
}