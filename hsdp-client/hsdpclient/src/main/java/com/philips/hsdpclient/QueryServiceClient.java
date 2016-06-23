/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static com.philips.hsdpclient.logging.LogMessageBuilder.createLogMessageBuilder;

import java.util.*;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.philips.hsdpclient.datamodel.observation.*;
import com.philips.hsdpclient.exception.ErrorGettingObservations;
import com.philips.hsdpclient.response.Response;
import com.philips.hsdpclient.util.DateTimeUtils;
import com.philips.hsdpclient.util.MapUtils;

public class QueryServiceClient extends ApiClient {

    protected static final String NO_RECORDS_FOUND = "1410";

    private Logger LOGGER = Logger.getLogger(QueryServiceClient.class);

    public QueryServiceClient(ApiClientConfiguration clientConfiguration) {
        super(clientConfiguration);
    }

    public List<Observation> getObservations(String userId, String subjectId, String observationIds, DateTime startTimeUtc) {
        String endpoint = "/queryservice/applications/" + applicationName + "/propositions/" + propositionName + "/users/" + userId + "/hourly";
        HttpHeaders headers = new HttpHeaders();
        String params = "observationList=" + observationIds + "&startDate=" + DateTimeUtils.asString(startTimeUtc) + "&subjectUUID=" + subjectId;
        sign(headers, endpoint, params, HttpMethod.GET, null);
        Response response = sendRequest(HttpMethod.GET, endpoint, params, headers, null);
        if (response.code.equals(NO_RECORDS_FOUND)) {
            return Collections.emptyList();
        }
        if (!response.code.equals(RESPONSE_OK)) {
            LOGGER.error(createLogMessageBuilder("Error getting observations").appendUserId(userId).appendSubjectId(subjectId).appendObservationIds(observationIds)
                    .appendStartTimeUtc(DateTimeUtils.asString(startTimeUtc)).append(response));

            throw new ErrorGettingObservations("Error getting observations");
        }
        return extractObservationsFrom(response);
    }

    private List<Observation> extractObservationsFrom(Response response) {
        List<Observation> observations = new ArrayList<>();
        List<Map<String, Object>> observationsList = MapUtils.extract(response.rawBody, "exchange.responseObject.observations");
        if (observationsList == null)
            return observations;

        for (Map<String, Object> observation : observationsList) {
            observations.add(extractObservations(observation));
        }

        return observations;
    }

    private Observation extractObservations(Map<String, Object> responseObservation) {
        Observation observation = new Observation();
        observation.observationType = MapUtils.extract(responseObservation, "observationType");
        observation.sources = new ArrayList<>();

        List<Map<String, Object>> sourceList = MapUtils.extract(responseObservation, "sources");
        if (sourceList == null) {
            return observation;
        }

        for (Map<String, Object> source : sourceList) {
            observation.sources.add(extractSource(source));
        }
        return observation;
    }

    private Source extractSource(Map<String, Object> responseSource) {
        Source source = new Source();
        source.sourceName = MapUtils.extract(responseSource, "sourceName");
        source.values = new ArrayList<>();

        List<Map<String, Object>> valuesList = MapUtils.extract(responseSource, "values");
        if (valuesList == null) {
            return source;
        }

        for (Map<String, Object> value : valuesList) {
            source.values.add(extractValue(value));
        }
        return source;
    }

    private Value extractValue(Map<String, Object> responseValue) {
        Value value = new Value();
        value.v = MapUtils.extract(responseValue, "v");

        /* Very ugly hack, for Mood there is no collection timestamp (ts) */
        value.lastModTS = DateTimeUtils.toObservationDateTime(MapUtils.extract(responseValue, "lastModTS"));
        String ts = MapUtils.extract(responseValue, "ts");
        value.ts = (ts == null ? value.lastModTS : DateTimeUtils.toObservationDateTime(ts));

        value.subjectId = MapUtils.extract(responseValue, "subjectId");
        value.device = MapUtils.extract(responseValue, "device");
        value.visible = MapUtils.extract(responseValue, "visible");
        value.resourceId = MapUtils.extract(responseValue, "resourceId");
        return value;
    }
}
