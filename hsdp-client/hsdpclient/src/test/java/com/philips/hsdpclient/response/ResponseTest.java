/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ResponseTest {
    @Test
    public void extractsResponseCodeFromRawResponse() {
        assertEquals("200", new Response(ImmutableMap.of("responseCode", "200")).code);
    }

    @Test
    public void extractsResponseMessageFromRawResponse() {
        assertEquals("Success", new Response(ImmutableMap.of("responseMessage", "Success")).message);
    }

    @Test
    public void providesConstantForDefaultSuccessResponse() {
        assertEquals(new Response(ImmutableMap.of("responseCode", "200", "responseMessage", "Success")), Response.SUCCESS);
    }
}