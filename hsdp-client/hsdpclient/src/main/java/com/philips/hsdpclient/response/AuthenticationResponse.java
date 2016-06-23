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

public final class AuthenticationResponse extends Response {
    public final String accessToken;
    public final String refreshToken;
    public final Integer expiresIn;
    public final String userId;

    public AuthenticationResponse(Map<String, Object> rawResponse) {
        this(null, null, null, null, rawResponse);
    }

    public AuthenticationResponse(String accessToken, String refreshToken, Integer expiresIn, String userId, Map<String, Object> rawResponse) {
        super(rawResponse);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        AuthenticationResponse that = (AuthenticationResponse) o;
        return Objects.equals(accessToken, that.accessToken) &&
               Objects.equals(refreshToken, that.refreshToken) &&
               Objects.equals(expiresIn, that.expiresIn) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accessToken, refreshToken, expiresIn, userId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accessToken", accessToken)
                .add("refreshToken", refreshToken)
                .add("expiresIn", expiresIn)
                .add("userId", userId)
                .add("rawResponse", rawBody)
                .toString();
    }
}
