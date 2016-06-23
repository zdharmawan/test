/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static com.philips.hsdpclient.RestTestUtils.jsonBody;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.philips.hsdpclient.exception.*;
import com.philips.hsdpclient.response.*;

public class SubscriptionServiceClientTest extends BaseClientTest {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String FEED_VENDOR = "FEED_VENDOR";
    private static final String STANDARD_OBSERVATION_NAME = "STANDARD_OBSERVATION_NAME";
    private static final String USER_SUBSCRIPTION_UUID = "USER_SUBSCRIPTION_UUID";
    private static final String USER_ID = "e11c8064-abb7-4de0-974c-cfc40e4d3f51";
    private static final String ACCESS_TOKEN = "2sherg8x3nqw3atd";
    private static final String DEVICE_ID = "ExampleDeviceId";
    private static final String DOCUMENT_VERSION = "SomeVersion";

    private static final SubscriptionDetail SUBSCRIPTION = buildSubscriptionDetail(STANDARD_OBSERVATION_NAME, USER_SUBSCRIPTION_UUID);
    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(REST_TEMPLATE);

    private SubscriptionServiceClient serviceClient;

    private TermsAndConditionResponse foundTermAndCondition;

    @Before
    public void before() {
        serviceClient = new SubscriptionServiceClient(
                new ApiClientConfiguration(BASE_URL, HSDP_APPLICATION_NAME, HSDP_PROPOSITION_NAME, "key", "secret", ""));
        serviceClient.setRestTemplate(REST_TEMPLATE);
    }

    private List<SubscriptionDetail> returnedSubscriptions;

    @Test
    public void sendsTermsAndConditionsRequestToHsdp_returnsResponse() {
        mockServer.expect(requestTo(BASE_URL + "/subscription/users/" + USER_ID + "/termsAndConditions"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonBody("{\n" +
                                    "    \"applicationName\": \"" + HSDP_APPLICATION_NAME + "\",\n" +
                                    "    \"consentCode\": \"1\",\n" +
                                    "    \"countryCode\": \"NL\",\n" +
                                    "    \"documentVersion\": \"2.1\",\n" +
                                    "    \"deviceIdentificationNumber\": \"DI123\",\n" +
                                    "    \"documentId\": \"1\",\n" +
                                    "    \"propositionName\": \"uGrowProp\",\n" +
                                    "    \"standardObservationName\": \"Weight\",\n" +
                                    "    \"consentStatus\": \"A\"\n" +
                                    "}"))
                .andRespond(withSuccess(SUCCESSFUL_TERMS_AND_CONDITIONS_STORE_RESPONSE, MediaType.APPLICATION_JSON));
        Response response = serviceClient.storeTermsAndConditionsAccepted(USER_ID, "Weight", "A", "1", "NL", "2.1", "DI123");
        assertEquals("200", response.code);

        mockServer.verify();
    }

    @Test(expected = TermsAndConditionRequest.class)
    public void throwsException_WhenErrorOccursWhenStoringTermsAndConditions() {
        mockServer.expect(requestTo(BASE_URL + "/subscription/users/" + USER_ID + "/termsAndConditions"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(FAILED_STORE_TERMS_AND_CONDITIONS_RESPONSE, MediaType.APPLICATION_JSON));
        serviceClient.storeTermsAndConditionsAccepted(USER_ID, "Weight", "A", "1", "NL", "2.1", "DI123");
    }

    @Test
    public void retrieveLatestTermsAndConditionForTheCorrectStandardObservation() {
        givenHSDPReturnsConsents(SUCCESSFUL_TERMS_AND_CONDITIONS_RESPONSE);
        whenRetrievingTermsAndConditions("Weight", DEVICE_ID, DOCUMENT_VERSION);
        thenConsentIsReturned(DOCUMENT_VERSION, DEVICE_ID, "Weight", "A", "2017-03-11T18:45:47Z");
    }

    @Test
    public void retrieveLatestTermsAndConditionForTheCorrectStandardObservation_WhenDeviceIdNotProvided() {
        givenHSDPReturnsConsents(SUCCESSFUL_TERMS_AND_CONDITIONS_RESPONSE);
        whenRetrievingTermsAndConditions("Weight", "", DOCUMENT_VERSION);
        thenConsentIsReturned(DOCUMENT_VERSION, "SomeOtherDeviceId2", "Weight", "A", "2018-03-11T18:45:47Z");
    }

    @Test
    public void retrieveLatestTermsAndConditionForTheCorrectStandardObservation_WhenDocumentVersionNotProvided() {
        givenHSDPReturnsConsents(SUCCESSFUL_TERMS_AND_CONDITIONS_RESPONSE);
        whenRetrievingTermsAndConditions("Weight", DEVICE_ID, "");
        thenConsentIsReturned("1.0", DEVICE_ID, "Weight", "A", "2019-03-11T18:45:47Z");
    }

    @Test
    public void retrieveLatestTermsAndConditionForTheCorrectStandardObservation_WhenDocumentVersionAndDeviceNotProvided() {
        givenHSDPReturnsConsents(SUCCESSFUL_TERMS_AND_CONDITIONS_RESPONSE);
        whenRetrievingTermsAndConditions("Weight", "", "");
        thenConsentIsReturned("1.0", DEVICE_ID, "Weight", "A", "2019-03-11T18:45:47Z");
    }

    @Test
    public void returnsNullWhenNoMatchingRecordIsFound() {
        givenHSDPReturnsConsents(SUCCESSFUL_TERMS_AND_CONDITIONS_RESPONSE);
        whenRetrievingTermsAndConditions("Weight", "nonExisting", "nonExisting");
        thenNoTermsAndConditionsAreReturned();
    }

    @Test
    public void returnsNullWhenNoRecordFound() {
        givenNoRecordFoundInHSDP();
        whenRetrievingTermsAndConditions("SomeNonExextingStandardObservation", "", "");
        thenNoTermsAndConditionsAreReturned();
    }

    @Test(expected = TermsAndConditionRequest.class)
    public void throwsHsdpException_WhenTheRequestIsInvalid() {
        givenHSDPReturnsConsents(FAILED_TERMS_AND_CONDITIONS_RESPONSE);
        whenRetrievingTermsAndConditions("Weight", "", "");
    }

    @Test(expected = InvalidStandardObservationName.class)
    public void throwsException_WhenAnUnknownStandardObservationNameIsProvided() {
        givenHSDPReturnsConsents(UNKNOWN_STANDARD_OBSERVATION_NAME_RESPONSE);
        whenRetrievingTermsAndConditions("Weight", "", "");
    }

    @Test
    public void sendsCloseAccount_returnsSuccessWithDeleteData() {
        mockServer.expect(requestTo(BASE_URL + "/subscription/applications/" + HSDP_APPLICATION_NAME + "/users/" + USER_ID + "/close"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonBody("{\n" +
                                    "    \"deleteDataFlag\": \"Yes\"\n" +
                                    "}"))
                .andRespond(withSuccess(SUCCESSFUL_CLOSE_ACCOUNT_RESPONSE, MediaType.APPLICATION_JSON));

        serviceClient.closeAccount(USER_ID, true, ACCESS_TOKEN);

        mockServer.verify();
    }

    @Test
    public void sendsCloseAccount_returnsSuccessWithoutDeletingData() {
        mockServer.expect(requestTo(BASE_URL + "/subscription/applications/" + HSDP_APPLICATION_NAME + "/users/" + USER_ID + "/close"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonBody("{\n" +
                                    "    \"deleteDataFlag\": \"No\"\n" +
                                    "}"))
                .andRespond(withSuccess(SUCCESSFUL_CLOSE_ACCOUNT_RESPONSE, MediaType.APPLICATION_JSON));

        serviceClient.closeAccount(USER_ID, false, ACCESS_TOKEN);

        mockServer.verify();
    }

    @Test
    public void retrieveSubscriptions() {
        givenHsdpRespondsWith(
                BASE_URL + "/subscription/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/feedVendors/" + FEED_VENDOR + "/users/"
                              + USER_ID + "/standardObservations",
                SUBSCRIPTION_RESPONSE);
        whenRetrivingSubscriptions();
        thenSubscriptionsAreReturned(Arrays.asList(SUBSCRIPTION));
    }

    @Test(expected = StandardObservationSubscriptionRequest.class)
    public void throwsExceptionWhenRetrievingSubscriptionFails() {
        givenHsdpRespondsWith(
                BASE_URL + "/subscription/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/feedVendors/" + FEED_VENDOR + "/users/"
                              + USER_ID + "/standardObservations",
                FAILED_RETRIEVING_SUBSCRIPTION_RESPONSE);
        whenRetrivingSubscriptions();
    }

    @Test
    public void sendsApplicationSubscription_returnsSuccess() {
        mockServer.expect(requestTo(BASE_URL + "/subscription/applications/" + HSDP_APPLICATION_NAME + "/users/" + USER_ID + "/subscribe"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(SUCCESSFUL_APPLICATION_SUBSCRIPTION_RESPONSE, MediaType.APPLICATION_JSON));

        serviceClient.subscribeToApplication(USER_ID);

        mockServer.verify();
    }

    @Test
    public void sendsPropositionSubscription_returnsSuccess() {
        mockServer.expect(
                requestTo(BASE_URL + "/subscription/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/users/" + USER_ID + "/subscribe"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(SUCCESSFUL_PROPOSITION_SUBSCRIPTION_RESPONSE, MediaType.APPLICATION_JSON));

        serviceClient.subscribeToProposition(USER_ID);

        mockServer.verify();
    }

    private static final String SUCCESSFUL_TERMS_AND_CONDITIONS_STORE_RESPONSE = "{\n" +
                                                                                 "    \"exchange\": {\n" +
                                                                                 "        \"applicationName\": \"" + HSDP_APPLICATION_NAME + "\",\n" +
                                                                                 "        \"classCode\": \"S300\",\n" +
                                                                                 "        \"deviceIdentificationNumber\": \"DI123\",\n" +
                                                                                 "        \"documentId\": \"1\",\n" +
                                                                                 "        \"startDate\": \"2015-01-13 06:42:10\",\n" +
                                                                                 "        \"countryCode\": \"IN\",\n" +
                                                                                 "        \"documentVersion\": \"1.0\"\n" +
                                                                                 "    },\n" +
                                                                                 "    \"responseCode\": \"200\",\n" +
                                                                                 "    \"responseMessage\": \"Success\"\n" +
                                                                                 "}";

    private static final String SUBSCRIPTION_RESPONSE = "{\n" +
                                                        "  \"exchange\": {\n" +
                                                        "    \"userUUID\": \"6093d435-81e5-46d5-aada-39a375761e6e\",\n" +
                                                        "    \"applicationName\": \"uGrowApp\",\n" +
                                                        "    \"propositionName\": \"uGrowProp\",\n" +
                                                        "    \"vendorName\": \"uGrow\",\n" +
                                                        "    \"subcribedObservations\": [\n" +
                                                        "      {\n" +
                                                        "        \"subscriptionUUID\": \"" + USER_SUBSCRIPTION_UUID + "\",\n" +
                                                        "        \"standardObservationName\": \"" + STANDARD_OBSERVATION_NAME + "\"\n" +
                                                        "      }\n" +
                                                        "    ]\n" +
                                                        "  },\n" +
                                                        "  \"responseCode\": \"200\",\n" +
                                                        "  \"responseMessage\": \"Success\"\n" +
                                                        "}";

    private static final String SUCCESSFUL_TERMS_AND_CONDITIONS_RESPONSE = "{\n" +
                                                                           "    \"exchange\": {\n" +
                                                                           "        \"userTermsAndConditionsCollection\": {\n" +
                                                                           "            \"userTermsAndConditionsList\": [{\n" +
                                                                           "                    \"applicationName\": \"" + HSDP_APPLICATION_NAME + "\",\n" +
                                                                           "                    \"documentVersion\": \"1.0\",\n" +
                                                                           "                    \"documentId\": \"1\",\n" +
                                                                           "                    \"countryCode\": \"AL\",\n" +
                                                                           "                    \"classCode\": \"S300\",\n" +
                                                                           "                    \"startDate\": \"2015-03-11T18:45:47Z\",\n" +
                                                                           "                    \"deviceIdentificationNumber\": \"SomeOtherDeviceId1\",\n" +
                                                                           "                    \"standardObservationName\": \"Weight\",\n" +
                                                                           "                    \"consentStatus\": \"R\",\n" +
                                                                           "                    \"consentMode\": \"V\",\n" +
                                                                           "                    \"consentDisclosure\": \"F\",\n" +
                                                                           "                    \"languageCode\": \"NL\",\n" +
                                                                           "                    \"consentCode\": \"1\"\n" +
                                                                           "                },{" +
                                                                           "                \"applicationName\": \"" + HSDP_APPLICATION_NAME + "\"," +
                                                                           "                    \"documentVersion\": \"" + DOCUMENT_VERSION + "\",\n" +
                                                                           "                    \"documentId\": \"1\",\n" +
                                                                           "                    \"countryCode\": \"AL\",\n" +
                                                                           "                    \"classCode\": \"S300\",\n" +
                                                                           "                    \"startDate\": \"2018-03-11T18:45:47Z\",\n" +
                                                                           "                    \"deviceIdentificationNumber\": \"SomeOtherDeviceId2\",\n" +
                                                                           "                    \"standardObservationName\": \"Weight\",\n" +
                                                                           "                    \"consentStatus\": \"A\",\n" +
                                                                           "                    \"consentMode\": \"V\",\n" +
                                                                           "                    \"consentDisclosure\": \"F\",\n" +
                                                                           "                    \"languageCode\": \"NL\",\n" +
                                                                           "                    \"consentCode\": \"1\"\n" +
                                                                           "               },{" +
                                                                           "                \"applicationName\": \"" + HSDP_APPLICATION_NAME + "\"," +
                                                                           "                    \"documentVersion\": \"1.0\",\n" +
                                                                           "                    \"documentId\": \"1\",\n" +
                                                                           "                    \"countryCode\": \"AL\",\n" +
                                                                           "                    \"classCode\": \"S300\",\n" +
                                                                           "                    \"startDate\": \"2019-03-11T18:45:47Z\",\n" +
                                                                           "                    \"deviceIdentificationNumber\": \"" + DEVICE_ID + "\",\n" +
                                                                           "                    \"standardObservationName\": \"Weight\",\n" +
                                                                           "                    \"consentStatus\": \"A\",\n" +
                                                                           "                    \"consentMode\": \"V\",\n" +
                                                                           "                    \"consentDisclosure\": \"F\",\n" +
                                                                           "                    \"languageCode\": \"NL\",\n" +
                                                                           "                    \"consentCode\": \"1\"\n" +
                                                                           "               },{" +
                                                                           "                \"applicationName\": \"" + HSDP_APPLICATION_NAME + "\"," +
                                                                           "                    \"documentVersion\": \"" + DOCUMENT_VERSION + "\",\n" +
                                                                           "                    \"documentId\": \"1\",\n" +
                                                                           "                    \"countryCode\": \"AL\",\n" +
                                                                           "                    \"classCode\": \"S300\",\n" +
                                                                           "                    \"startDate\": \"2017-03-11T18:45:47Z\",\n" +
                                                                           "                    \"deviceIdentificationNumber\": \"" + DEVICE_ID + "\",\n" +
                                                                           "                    \"standardObservationName\": \"Weight\",\n" +
                                                                           "                    \"consentStatus\": \"A\",\n" +
                                                                           "                    \"consentMode\": \"V\",\n" +
                                                                           "                    \"consentDisclosure\": \"F\",\n" +
                                                                           "                    \"languageCode\": \"NL\",\n" +
                                                                           "                    \"consentCode\": \"1\"\n" +
                                                                           "               }" +
                                                                           "            ]\n" +
                                                                           "        }\n" +
                                                                           "    },\n" +
                                                                           "    \"responseCode\": \"200\",\n" +
                                                                           "    \"responseMessage\": \"Success\"\n" +
                                                                           "}";

    private static final String FAILED_STORE_TERMS_AND_CONDITIONS_RESPONSE = "{\n" +
                                                                             "    \"responseCode\": \"1151\",\n" +
                                                                             "    \"responseMessage\": \"Error.\"\n" +
                                                                             "}";

    private static final String TERMS_NO_RECORDS_FOUND = "{\n" +
                                                         "    \"responseCode\": \"1151\",\n" +
                                                         "    \"responseMessage\": \"No records found.\"\n" +
                                                         "}";

    private static final String FAILED_TERMS_AND_CONDITIONS_RESPONSE = "{\n" +
                                                                       "    \"responseCode\": \"1150\",\n" +
                                                                       "    \"responseMessage\": \"Fail\"\n" +
                                                                       "}";
    private static final String FAILED_RETRIEVING_SUBSCRIPTION_RESPONSE = "{\n" +
                                                                          "  \"responseCode\": \"1111\",\n" +
                                                                          "  \"responseMessage\": \"Invalid Internal User id\"\n" +
                                                                          "}";

    private static final String UNKNOWN_STANDARD_OBSERVATION_NAME_RESPONSE = "{\n" +
                                                                             "    \"responseCode\": \"1305\",\n" +
                                                                             "    \"responseMessage\": \"Fail\"\n" +
                                                                             "}";

    private static final String SUCCESSFUL_CLOSE_ACCOUNT_RESPONSE = "{\n" +
                                                                    "    \"exchange\": {},\n" +
                                                                    "    \"responseCode\": \"200\",\n" +
                                                                    "    \"responseMessage\": \"Success\"\n" +
                                                                    "}";

    private static final String SUCCESSFUL_APPLICATION_SUBSCRIPTION_RESPONSE = "{\n" +
                                                                               "    \"exchange\": { \n" +
                                                                               "        \"userApplicationSubscription\": {} \n" +
                                                                               "    },\n" +
                                                                               "    \"responseCode\": \"200\",\n" +
                                                                               "    \"responseMessage\": \"Success\"\n" +
                                                                               "}";

    private static final String SUCCESSFUL_PROPOSITION_SUBSCRIPTION_RESPONSE = "{\n" +
                                                                               "    \"exchange\": {\n" +
                                                                               "       \"userPropositionSubscription\": {\n" +
                                                                               "            \"userSubscriptionUUID\": \"6ce15fa8-b179-4a78-a1a1-432186a5b968\",\n" +
                                                                               "            \"userUUID\": \"e11c8064-abb7-4de0-974c-cfc40e4d3f51\",\n" +
                                                                               "            \"subscriptionType\": \"Propositions\"\n" +
                                                                               "        }\n" +
                                                                               "    },\n" +
                                                                               "    \"responseCode\": \"200\",\n" +
                                                                               "    \"responseMessage\": \"Success\"\n" +
                                                                               "}\n";

    private void givenHSDPReturnsConsents(String successfulTermsAndConditionsResponse) {
        mockServer
                .expect(requestTo(BASE_URL + "/subscription/applications/" + HSDP_APPLICATION_NAME + "/users/" + USER_ID
                                  + "/termsAndConditions?consentCode=3&standardObservationName=Weight&propositionName=uGrowProp"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(successfulTermsAndConditionsResponse, MediaType.APPLICATION_JSON));
    }

    private void givenNoRecordFoundInHSDP() {
        mockServer
                .expect(anything())
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(TERMS_NO_RECORDS_FOUND, MediaType.APPLICATION_JSON));
    }

    private void givenHsdpRespondsWith(String url, String subscriptionResponse) {
        mockServer
                .expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(subscriptionResponse, MediaType.APPLICATION_JSON));
    }

    private void whenRetrievingTermsAndConditions(String standardObservationName, String deviceId, String documentVersion) {
        foundTermAndCondition = serviceClient.retrieveLatestTermsAndConditions(USER_ID, standardObservationName, deviceId, documentVersion, "3");
    }

    private void whenRetrivingSubscriptions() {
        returnedSubscriptions = serviceClient.retrieveSubscriptions(FEED_VENDOR, USER_ID);
    }

    private void thenNoTermsAndConditionsAreReturned() {
        assertNull(foundTermAndCondition);
    }

    private void thenSubscriptionsAreReturned(List<SubscriptionDetail> expectedSubscriptionDetails) {
        assertEquals(expectedSubscriptionDetails, returnedSubscriptions);
    }

    private void thenConsentIsReturned(String documentVersion, String deviceId, String observationName, String status, String startDate) {
        TermsAndConditionResponse expectedTermAndCondition = new TermsAndConditionResponse(documentVersion, deviceId, observationName, status, HSDP_APPLICATION_NAME,
                new DateTime(startDate, DateTimeZone.UTC));
        assertEquals(expectedTermAndCondition, foundTermAndCondition);
        mockServer.verify();
    }

    private static SubscriptionDetail buildSubscriptionDetail(String standardObservationName, String userSubscriptionUUID) {
        SubscriptionDetail subscriptionDetail = new SubscriptionDetail();
        subscriptionDetail.standardObservationName = standardObservationName;
        subscriptionDetail.userSubscriptionUUID = userSubscriptionUUID;
        return subscriptionDetail;
    }

}