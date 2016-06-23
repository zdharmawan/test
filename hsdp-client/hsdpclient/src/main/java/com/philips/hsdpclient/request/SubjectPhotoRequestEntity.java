/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import org.joda.time.DateTime;

import com.philips.hsdpclient.util.DateTimeUtils;

public class SubjectPhotoRequestEntity {

    public Photo photo;
    public String lastUpdated;

    public SubjectPhotoRequestEntity(com.philips.hsdpclient.datamodel.Photo photo, DateTime lastUpdated) {
        if (photo != null) {
            this.photo = new Photo(photo.type, photo.base64Value);
        } else {
            this.photo = new Photo("", "");
        }
        this.lastUpdated = lastUpdated != null ? DateTimeUtils.asString(lastUpdated) : null;
    }
}
