/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import com.philips.hsdpclient.datamodel.observation.Value;
import org.joda.time.DateTime;

public class ValueBuilder {
    private Value value;

    public ValueBuilder(Value value) {
        this.value = value;
    }
    public ValueBuilder withValue(String value) {
        this.value.v = value;
        return this;
    }
    public ValueBuilder withTimeStamp(DateTime timeStamp) {
        this.value.ts = timeStamp;
        return this;
    }

    public ValueBuilder withSubjectId(String subjectId) {
        this.value.subjectId = subjectId;
        return this;
    }

    public ValueBuilder withDevice(String device) {
        this.value.device = device;
        return this;
    }

    public ValueBuilder withLastModifed(DateTime lastModifed) {
        this.value.lastModTS = lastModifed;
        return this;
    }

    public ValueBuilder withVisible(String visible) {
        this.value.visible = visible;
        return this;
    }

    public ValueBuilder withResourceId(String resourceId) {
        this.value.resourceId = resourceId;
        return this;
    }

}
