/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import com.google.common.base.MoreObjects;

public class UserProfile {

    public String uuid;
    public String loginId;
    public String password;
    public Profile profile;

    public UserProfile(String loginId, String password, Profile profile) {
        this(null, loginId, password, profile);
    }

    public UserProfile(String uuid, String loginId, String password, Profile profile) {
        this.uuid = uuid;
        this.loginId = loginId;
        this.password = password;
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UserProfile that = (UserProfile) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null)
            return false;
        if (loginId != null ? !loginId.equals(that.loginId) : that.loginId != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null)
            return false;
        return !(profile != null ? !profile.equals(that.profile) : that.profile != null);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (loginId != null ? loginId.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("loginId", loginId)
                .add("password", password)
                .add("profile", profile)
                .toString();
    }
}
