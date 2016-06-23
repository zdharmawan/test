/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.datamodel.observation;

import org.joda.time.DateTime;

public class Value {
    public String v;
    public DateTime ts;
    public String subjectId;
    public String device;
    public DateTime lastModTS;
    public String visible;
    public String resourceId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Value value = (Value) o;

        if (v != null ? !v.equals(value.v) : value.v != null)
            return false;
        if (ts != null ? !ts.equals(value.ts) : value.ts != null)
            return false;
        if (subjectId != null ? !subjectId.equals(value.subjectId) : value.subjectId != null)
            return false;
        if (device != null ? !device.equals(value.device) : value.device != null)
            return false;
        if (lastModTS != null ? !lastModTS.equals(value.lastModTS) : value.lastModTS != null)
            return false;
        if (visible != null ? !visible.equals(value.visible) : value.visible != null)
            return false;
        return resourceId != null ? resourceId.equals(value.resourceId) : value.resourceId == null;

    }

    @Override
    public int hashCode() {
        int result = v != null ? v.hashCode() : 0;
        result = 31 * result + (ts != null ? ts.hashCode() : 0);
        result = 31 * result + (subjectId != null ? subjectId.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + (lastModTS != null ? lastModTS.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        return result;
    }
}
