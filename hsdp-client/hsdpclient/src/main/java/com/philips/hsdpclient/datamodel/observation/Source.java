/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.datamodel.observation;

import java.util.List;

public class Source {
    public String sourceName;
    public List<Value> values;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Source source = (Source) o;

        if (sourceName != null ? !sourceName.equals(source.sourceName) : source.sourceName != null)
            return false;
        return values != null ? values.equals(source.values) : source.values == null;

    }

    @Override
    public int hashCode() {
        int result = sourceName != null ? sourceName.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
