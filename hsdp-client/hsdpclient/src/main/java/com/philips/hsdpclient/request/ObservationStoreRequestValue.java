/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import com.philips.hsdpclient.datamodel.observation.Value;
import com.philips.hsdpclient.util.DateTimeUtils;

public class ObservationStoreRequestValue {
    public String v;
    public String ts;
    public String subjectId;
    public String device;
    public String visible;

    public ObservationStoreRequestValue(Value value) {
        this.v = value.v;
        this.ts = DateTimeUtils.asObservationString(value.ts);
        this.subjectId = value.subjectId;
        this.device = value.device;
        this.visible = value.visible;
    }
}
