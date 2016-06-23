/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import com.google.common.base.MoreObjects;
import com.philips.hsdpclient.Formattable;
import com.philips.hsdpclient.util.StringFormatter;

public class Photo implements Formattable {
    public String type;
    public String value;

    public Photo(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Photo photo = (Photo) o;

        if (type != null ? !type.equals(photo.type) : photo.type != null)
            return false;
        return !(value != null ? !value.equals(photo.value) : photo.value != null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("value", value)
                .toString();
    }

    public String toString(StringFormatter stringFormatter) {
        return stringFormatter.format(this);
    }
}