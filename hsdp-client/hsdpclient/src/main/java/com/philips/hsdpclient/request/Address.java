/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import com.google.common.base.MoreObjects;

public class Address {

    public String country;

    public Address(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Address address = (Address) o;
        return !(country != null ? !country.equals(address.country) : address.country != null);
    }

    @Override
    public int hashCode() {
        return country != null ? country.hashCode() : 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("country", country)
                .toString();
    }
}