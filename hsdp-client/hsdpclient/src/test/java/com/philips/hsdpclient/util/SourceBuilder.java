/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import java.util.ArrayList;

import com.philips.hsdpclient.datamodel.observation.Source;
import com.philips.hsdpclient.datamodel.observation.Value;

public class SourceBuilder {
    private Source source;

    public SourceBuilder(Source source) {
        this.source = source;
    }

    public SourceBuilder withSourceName(String sourceName) {
        this.source.sourceName = sourceName;
        return this;
    }

    public ValueBuilder addValue() {
        if (this.source.values == null) {
            this.source.values = new ArrayList<>();
        }
        Value value = new Value();
        this.source.values.add(value);
        return new ValueBuilder(value);
    }
}
