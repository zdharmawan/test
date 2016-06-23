/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.datamodel.observation.Observation;
import com.philips.hsdpclient.util.*;

public class ObservationStoreClientTest extends BaseClientTest {

    public static final String PREFIX = "uGrow";

    @Before
    public void before() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        observationStoreClient = new ObservationStoreClient(clientConfiguration, PREFIX);
        observationStoreClient.setRestTemplate(serverSpy);
    }

    @Test
    public void storeObservationAsync() {
        givenTheServerRespondsWith(BASE_URL + "/observationStorage/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/users/" + USER_ID
                                   + "/observationSubscription/" + USER_SUBSCRIPTION_UUID + "/observations?mode=async",
                successResponse);
        whenStoringObservationsAsync(USER_ID, USER_SUBSCRIPTION_UUID, OBSERVATION1);
        storeObservation();
    }

    @Test
    public void storeObservationSync() {
        givenTheServerRespondsWith(BASE_URL + "/observationStorage/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/users/" + USER_ID
                        + "/observationSubscription/" + USER_SUBSCRIPTION_UUID + "/observations?mode=sync",
                successResponse);
        whenStoringObservationsSync(USER_ID, USER_SUBSCRIPTION_UUID, OBSERVATION1);
        storeObservation();
    }

    private void storeObservation() {
        thenTheSentBodyIs(storeRequest);
        andTheUsedMethodIs(HttpMethod.POST);
        andHeaderIsSent("Authorization");
    }

    private void whenStoringObservationsAsync(String userId, String subscriptionId, Observation observation) {
        observationStoreClient.storeAsync(userId, subscriptionId, observation);
    }

    private void whenStoringObservationsSync(String userId, String subscriptionId, Observation observation) {
        observationStoreClient.storeSync(userId, subscriptionId, observation);
    }

    private static final String LAST_MODIFIED_1 = "2016-02-11T12:14:24.00Z";

    private static final String RESOURCE_ID_1 = "resourceId";

    private static final String TIMESTAMP_1 = "2016-01-29T10:27:00.00Z";
    private static final String SUBJECT_ID = "SUBJECT_ID";
    private static final Observation OBSERVATION1 = createObservation();
    private static final String TEMPERATURE = "uGrowTemperature";
    private static final String USER_SUBSCRIPTION_UUID = "subscriptionUuid";

    private static Observation createObservation() {
        ObservationBuilder builder = new ObservationBuilder("Temperature");
        builder.addSource()
                .withSourceName("uGrow")
                .addValue().withValue("40.5").withTimeStamp(DateTimeUtils.toObservationDateTime(TIMESTAMP_1))
                .withSubjectId(SUBJECT_ID).withDevice(COMPASS).withLastModifed(DateTimeUtils.toObservationDateTime(LAST_MODIFIED_1))
                .withVisible("0");

        return builder.build();
    }

    private static final String COMPASS = "Compass";

    private ObservationStoreClient observationStoreClient;

    private String storeRequest = "{\"dataType\":\"Philips Data Feed|Manual Input|Third Party|Other\"," +
                                  "\"collectedTS\":\"" + TIMESTAMP_1 + "\"," +
                                  "\"obsType\":\"" + TEMPERATURE + "\"," +
                                  "\"data\":[{" +
                                  "\"v\":\"40.5\"," +
                                  "\"ts\":\"" + TIMESTAMP_1 + "\"," +
                                  "\"subjectId\":\"" + SUBJECT_ID + "\"," +
                                  "\"device\":\"" + COMPASS + "\"," +
                                  "\"visible\":\"0\"" +
                                  "}]" +
                                  "}";

    private String successResponse = "{\n" +
                                     "  \"exchange\": {\n" +
                                     "    \"resourceId\": \"8cfa76e7-78b1-45ec-b5fa-bb2d7d5954e3\",\n" +
                                     "    \"transactionStatus\": \"STATE_RECEIVED\",\n" +
                                     "    \"lastModTS\": \"2016-02-18T14:49:37:37Z\"\n" +
                                     "  },\n" +
                                     "  \"responseCode\": \"200\",\n" +
                                     "  \"responseMessage\": \"Success\"\n" +
                                     "}";
}