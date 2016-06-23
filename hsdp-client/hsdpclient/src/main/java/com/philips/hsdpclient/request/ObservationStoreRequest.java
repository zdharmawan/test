/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import com.philips.hsdpclient.datamodel.observation.*;
import com.philips.hsdpclient.util.DateTimeUtils;

import java.util.Collections;
import java.util.List;

public class ObservationStoreRequest {

    public String dataType;
    public String collectedTS;
    public String obsType;
    public List<Object> data;

    public ObservationStoreRequest(Observation observation, String prefix) {
        assert (!(observation.sources == null || observation.sources.size() != 1));
        Source source = observation.sources.get(0);
        assert (!(source.values == null || source.values.size() != 1));
        Value value = source.values.get(0);

        dataType = "Philips Data Feed|Manual Input|Third Party|Other";
        collectedTS = DateTimeUtils.asObservationString(value.ts);
        if ("RoomTemperature".equals(observation.observationType) || "RelativeHumidity".equals(observation.observationType)) {
            obsType = observation.observationType;
        } else {
            obsType = prefix + observation.observationType;
        }
        data = Collections.singletonList(getRequestValue(value));
    }

    private ObservationStoreRequestValue getRequestValue(Value value) {
        return new ObservationStoreRequestValue(value);
    }
}
