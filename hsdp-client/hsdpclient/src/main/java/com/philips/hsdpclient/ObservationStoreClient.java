/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.philips.hsdpclient.datamodel.observation.Observation;
import com.philips.hsdpclient.exception.ErrorStoringObservation;
import com.philips.hsdpclient.request.ObservationStoreRequest;
import com.philips.hsdpclient.response.Response;

public class ObservationStoreClient extends ApiClient {

    private final String prefix;

    enum RequestMode {
        Async("async"), Sync("sync");
        private final String value;
        RequestMode(String value) {
            this.value = value;
        }
    }

    public ObservationStoreClient(ApiClientConfiguration clientConfiguration, String prefix) {
        super(clientConfiguration);
        this.prefix = prefix;
    }

    public void storeAsync(String userId, String subscriptionId, Observation observation) {
        storeObservations(userId, subscriptionId, observation, RequestMode.Async);
    }

    public void storeSync(String userId, String subscriptionId, Observation observation) {
        storeObservations(userId, subscriptionId, observation, RequestMode.Sync);
    }

    private void storeObservations(String userId, String subscriptionId, Observation observation, RequestMode requestMode) {
        String endpoint = "/observationStorage/applications/" + applicationName + "/propositions/" + propositionName + "/users/" + userId + "/observationSubscription/"
                + subscriptionId + "/observations";
        HttpHeaders headers = new HttpHeaders();
        String params = "mode=" + requestMode.value;

        ObservationStoreRequest body = new ObservationStoreRequest(observation, prefix);

        sign(headers, endpoint, params, HttpMethod.POST, body);
        Response response = sendRequest(HttpMethod.POST, endpoint, params, headers, body);
        if (!response.code.equals(RESPONSE_OK)) {
            throw new ErrorStoringObservation("Error storing observations");
        }
    }
}
