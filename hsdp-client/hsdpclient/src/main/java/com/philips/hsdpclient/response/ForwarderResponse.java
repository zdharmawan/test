/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class ForwarderResponse {
    public HttpHeaders headers;
    public String body;
    public HttpStatus status;

    public ForwarderResponse(HttpHeaders headers, String body, HttpStatus status) {
        this.headers = headers;
        this.body = body;
        this.status = status;
    }
}
