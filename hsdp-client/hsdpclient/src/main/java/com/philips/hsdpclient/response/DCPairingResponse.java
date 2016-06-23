/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

import java.util.*;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.philips.hsdpclient.Formattable;
import com.philips.hsdpclient.util.MapUtils;
import com.philips.hsdpclient.util.StringFormatter;

public class DCPairingResponse implements Formattable {

    public final Map<String, Object> rawBody;
    public final String code;
    public final String message;
    public final Map<String, String> headers;

    @SuppressWarnings("unchecked")
    public DCPairingResponse(RestResponse response) {
        this.rawBody = response.body;
        this.code = extractResponseCode();
        this.message = extractResponseMessage();
        headers = response.headers;
    }

    private String extractResponseCode() {
        List<Map<String, Object>> issueList = MapUtils.extract(rawBody, "issue");
        List<Map<String, Object>> codingList = MapUtils.extract(issueList != null ? issueList.get(0) : null, "Code.coding");
        return MapUtils.extract(codingList != null ? codingList.get(0) : null, "code");
    }

    private String extractResponseMessage() {
        List<Map<String, Object>> issueList = MapUtils.extract(rawBody, "issue");
        return MapUtils.extract(issueList != null ? issueList.get(0) : null, "Details");
    }


    @Override
    public String toString(StringFormatter stringFormatter) {
        return stringFormatter.format(this);

    }
}
