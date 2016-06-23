/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

import java.util.Map;
import java.util.Objects;

public final class UserRegistrationResponse extends Response {
    public final String userId;

    public UserRegistrationResponse(String userId, Map<String, Object> rawResponse) {
        super(rawResponse);
        this.userId = userId;
    }

    public UserRegistrationResponse(String userId, String responseCode, Map<String, Object> rawResponse) {
        super(responseCode, rawResponse);
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
        UserRegistrationResponse that = (UserRegistrationResponse) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId);
    }
}
