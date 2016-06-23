/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.datamodel.observation;

import java.util.List;

public class Observation {
    public String observationType;
    
    public List<Source> sources;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Observation that = (Observation) o;

        if (observationType != null ? !observationType.equals(that.observationType) : that.observationType != null)
            return false;
        return sources != null ? sources.equals(that.sources) : that.sources == null;

    }

    @Override
    public int hashCode() {
        int result = observationType != null ? observationType.hashCode() : 0;
        result = 31 * result + (sources != null ? sources.hashCode() : 0);
        return result;
    }
}
