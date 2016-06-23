/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import java.util.ArrayList;

import com.philips.hsdpclient.datamodel.observation.Observation;
import com.philips.hsdpclient.datamodel.observation.Source;

public class ObservationBuilder {
    private Observation observation;

    public ObservationBuilder(String type) {
        observation = new Observation();
        observation.observationType = type;
    }

    public SourceBuilder addSource() {
        if (observation.sources == null) {
            observation.sources = new ArrayList<>();
        }
        Source source = new Source();
        observation.sources.add(source);
        return new SourceBuilder(source);
    }

    public Observation build() {
        return observation;
    }
}
