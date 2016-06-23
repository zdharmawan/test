/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

import java.util.Map;

public class RestResponse {
    public Map body;

    public Map<String, String> headers;

    public RestResponse(Map body, Map<String, String> headers) {
        this.body = body;
        this.headers = headers;
    }
}
