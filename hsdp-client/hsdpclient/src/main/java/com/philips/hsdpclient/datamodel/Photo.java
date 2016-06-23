/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.datamodel;

import org.joda.time.DateTime;

public class Photo {

    public String type;
    public String base64Value;
    public DateTime lastUpdated;

    public Photo(String type, String base64Value) {
        this(type, base64Value, null);
    }

    public Photo(String type, String base64Value, DateTime lastUpdated) {
        this.type = type;
        this.base64Value = base64Value;
        this.lastUpdated = lastUpdated;
    }
}
