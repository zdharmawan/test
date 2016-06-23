/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import java.util.Map;

import com.philips.hsdpclient.datamodel.SubjectProfile;
import com.philips.hsdpclient.util.DateTimeUtils;

public class SubjectProfileRequestEntity {

    public SubjectProfileJsonObject profile;
    public String lastUpdated;

    public SubjectProfileRequestEntity(SubjectProfile subjectProfile) {
        profile = new SubjectProfileJsonObject();
        profile.name = subjectProfile.name;
        profile.birthday = subjectProfile.birthday != null ? subjectProfile.birthday.toString("yyyy-MM-dd") : "";
        profile.gender = subjectProfile.gender;
        profile.metadata = subjectProfile.metadata;

        this.lastUpdated = subjectProfile.lastUpdated != null ? DateTimeUtils.asString(subjectProfile.lastUpdated) : null;
    }

    class SubjectProfileJsonObject {
        public String name;
        public String birthday;
        public String gender;
        public Map<String, String> metadata;
    }
}
