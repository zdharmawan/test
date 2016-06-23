/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.exception.StandardObservationSubscriptionRequest;
import com.philips.hsdpclient.response.SubscriptionDetail;
import com.philips.hsdpclient.util.ServerSpy;

public class SubscriptionClientStandardObservationTests extends BaseClientTest {

    private List<SubscriptionDetail> createdSubscriptions;

    @Before
    public void before() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        subscriptionServiceClient = new SubscriptionServiceClient(clientConfiguration);
        subscriptionServiceClient.setRestTemplate(serverSpy);
    }

    @Test
    public void subscribeToSingleObservation() {
        givenTheServerRespondsWith(BASE_URL + postPathForSubscribe(), SUCCESFULL_SUBSCRIPTION_RESPONSE);
        whenSubscribingToObservations(Arrays.asList(STANDARD_OBSERVATION_NAME));
        thenTheSubscriptionIsCreatedWithId(STANDARD_OBSERVATION_NAME, SUBSCRIPTION_UUID);
        thenTheSentBodyIs("{\"standardObservationNames\":[\"" + STANDARD_OBSERVATION_NAME + "\"]}");
    }

    @Test
    public void subscribeToMultipleObservations() {
        givenTheServerRespondsWith(BASE_URL + postPathForSubscribe(), SUCCESFULL_MULTI_SUBSCRIPTION_RESPONSE);
        whenSubscribingToObservations(Arrays.asList(STANDARD_OBSERVATION_NAME, ANOTHER_STANDARD_OBSERVATION_TYPE));
        thenTheSubscriptionIsCreatedWithId(STANDARD_OBSERVATION_NAME, SUBSCRIPTION_UUID);
        thenTheSubscriptionIsCreatedWithId(ANOTHER_STANDARD_OBSERVATION_TYPE, ANOTHER_SUBSCRIPTION_UUID);
        thenTheSentBodyIs("{\"standardObservationNames\":[\"" + STANDARD_OBSERVATION_NAME + "\","
                          + "\"" + ANOTHER_STANDARD_OBSERVATION_TYPE + "\"]}");
    }

    @Test(expected = StandardObservationSubscriptionRequest.class)
    public void subscribeToObservationWithWrongObservationName() {
        givenTheServerRespondsWith(BASE_URL + postPathForSubscribe(), FAILED_SUBSCRIPTION_RESPONSE);
        whenSubscribingToObservations(Arrays.asList(INVALID_OBSERVATION_NAME));
    }

    @Test(expected = StandardObservationSubscriptionRequest.class)
    public void subscribeToObservationWithMissingConsent() {
        givenTheServerRespondsWith(BASE_URL + postPathForSubscribe(), FAILED_MISSING_CONSENT);
        whenSubscribingToObservations(Arrays.asList(STANDARD_OBSERVATION_NAME));
    }

    private void thenTheSubscriptionIsCreatedWithId(String standardObservationType, String expectedSubscriptionUuid) {
        for (SubscriptionDetail detail : createdSubscriptions) {
            if (detail.standardObservationName.equals(standardObservationType) && detail.userSubscriptionUUID.equals(expectedSubscriptionUuid))
                return;
        }
        fail("Could not find observation... " + standardObservationType);
    }

    private void whenSubscribingToObservations(List<String> standardObservationNames) {
        createdSubscriptions = subscriptionServiceClient.subscribeToStandardObservations(HSDP_VENDOR_NAME, USER_ID, standardObservationNames);
    }

    private String postPathForSubscribe() {
        return "/subscription/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/feedVendors/" + HSDP_VENDOR_NAME + "/users/" + USER_ID
               + "/standardObservations/subscribe";
    }

    private SubscriptionServiceClient subscriptionServiceClient;

    public static final String SUBSCRIPTION_UUID = "subscriptionUuid";
    public static final String ANOTHER_SUBSCRIPTION_UUID = "anotherSubscriptionUuid";

    public static final String STANDARD_OBSERVATION_NAME = "RoomTemperature";
    public static final String ANOTHER_STANDARD_OBSERVATION_TYPE = "RelativeHumidity";

    public static final String INVALID_OBSERVATION_NAME = "WrongTemperature";

    public static final String SUCCESFULL_SUBSCRIPTION_RESPONSE = "{\n" +
                                                                  "  \"exchange\": {\n" +
                                                                  "    \"subscriptionDetails\": [\n" +
                                                                  "      {\n" +
                                                                  "        \"userSubscriptionUUID\": \"" + SUBSCRIPTION_UUID + "\",\n" +
                                                                  "        \"userUUID\": \"" + USER_ID + "\",\n" +
                                                                  "        \"subscriptionType\": \"HSDP Observations\",\n" +
                                                                  "        \"standardObservationName\": \"" + STANDARD_OBSERVATION_NAME + "\",\n" +
                                                                  "        \"vendorName\": \"" + HSDP_VENDOR_NAME + "\"\n" +
                                                                  "      }\n" +
                                                                  "    ]\n" +
                                                                  "  },\n" +
                                                                  "  \"responseCode\": \"200\",\n" +
                                                                  "  \"responseMessage\": \"Success\"\n" +
                                                                  "}";

    public static final String SUCCESFULL_MULTI_SUBSCRIPTION_RESPONSE = "{\n" +
                                                                        "  \"exchange\": {\n" +
                                                                        "    \"subscriptionDetails\": [\n" +
                                                                        "      {\n" +
                                                                        "        \"userSubscriptionUUID\": \"" + SUBSCRIPTION_UUID + "\",\n" +
                                                                        "        \"userUUID\": \"" + USER_ID + "\",\n" +
                                                                        "        \"subscriptionType\": \"HSDP Observations\",\n" +
                                                                        "        \"standardObservationName\": \"" + STANDARD_OBSERVATION_NAME + "\",\n" +
                                                                        "        \"vendorName\": \"" + HSDP_VENDOR_NAME + "\"\n" +
                                                                        "      },\n" +
                                                                        "      {\n" +
                                                                        "        \"userSubscriptionUUID\": \"" + ANOTHER_SUBSCRIPTION_UUID + "\",\n" +
                                                                        "        \"userUUID\": \"" + USER_ID + "\",\n" +
                                                                        "        \"subscriptionType\": \"HSDP Observations\",\n" +
                                                                        "        \"standardObservationName\": \"" + ANOTHER_STANDARD_OBSERVATION_TYPE + "\",\n" +
                                                                        "        \"vendorName\": \"" + HSDP_VENDOR_NAME + "\"\n" +
                                                                        "      }\n" +
                                                                        "    ]\n" +
                                                                        "  },\n" +
                                                                        "  \"responseCode\": \"200\",\n" +
                                                                        "  \"responseMessage\": \"Success\"\n" +
                                                                        "}";

    public static final String FAILED_SUBSCRIPTION_RESPONSE = "{\n" +
                                                              "  \"responseCode\": \"1308\",\n" +
                                                              "  \"responseMessage\": \"Invalid Standard Observation\"\n" +
                                                              "}";

    public static final String FAILED_MISSING_CONSENT = "{\n" +
                                                        "  \"responseCode\": \"1138\",\n" +
                                                        "  \"responseMessage\": \"Consent is required for RoomTemperature\"\n" +
                                                        "}";
}
