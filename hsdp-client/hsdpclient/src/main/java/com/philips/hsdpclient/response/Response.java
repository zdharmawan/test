/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.philips.hsdpclient.Formattable;
import com.philips.hsdpclient.util.MapUtils;
import com.philips.hsdpclient.util.StringFormatter;

public class Response implements Formattable {
    public static final Response SUCCESS = new Response(ImmutableMap.of("responseCode", "200", "responseMessage", "Success"));

    public final Map<String, Object> rawBody;
    public final String code;
    public final String message;

    public Response(Map<String, Object> rawBody) {
        this.rawBody = rawBody;
        this.code = MapUtils.extract(rawBody, "responseCode");
        this.message = MapUtils.extract(rawBody, "responseMessage");
    }

    public Response(String code, Map<String, Object> rawBody) {
        this.rawBody = rawBody;
        this.code = code;
        this.message = MapUtils.extract(rawBody, "responseMessage");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Response response = (Response) o;
        return Objects.equals(rawBody, response.rawBody) &&
               Objects.equals(code, response.code) &&
               Objects.equals(message, response.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawBody, code, message);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rawResponse", rawBody)
                .toString();
    }

    public String toString(StringFormatter stringFormatter) {
        return stringFormatter.format(this);
    }
}
