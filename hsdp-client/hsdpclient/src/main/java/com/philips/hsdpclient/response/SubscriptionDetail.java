/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

public class SubscriptionDetail {
    public String standardObservationName;
    public String userSubscriptionUUID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubscriptionDetail that = (SubscriptionDetail) o;

        if (standardObservationName != null ? !standardObservationName.equals(that.standardObservationName) : that.standardObservationName != null)
            return false;
        return userSubscriptionUUID != null ? userSubscriptionUUID.equals(that.userSubscriptionUUID) : that.userSubscriptionUUID == null;

    }

    @Override
    public int hashCode() {
        int result = standardObservationName != null ? standardObservationName.hashCode() : 0;
        result = 31 * result + (userSubscriptionUUID != null ? userSubscriptionUUID.hashCode() : 0);
        return result;
    }
}
