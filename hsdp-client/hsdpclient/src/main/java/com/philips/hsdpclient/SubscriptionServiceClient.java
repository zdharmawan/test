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

import com.google.common.collect.ImmutableMap;
import com.philips.hsdpclient.exception.*;
import com.philips.hsdpclient.logging.LogMessageBuilder;
import com.philips.hsdpclient.request.StoreTermsAndConditionsRequest;
import com.philips.hsdpclient.request.SubscribeToStandardObservation;
import com.philips.hsdpclient.response.*;
import com.philips.hsdpclient.util.MapUtils;

public class SubscriptionServiceClient extends ApiClient {
    private static final String RESPONSE_NOT_FOUND = "1151";
    private Logger LOGGER = Logger.getLogger(SubscriptionServiceClient.class);

    private static final String TERMS_AND_CONDITIONS_DOCUMENT_ID = "1";
    private static final String INVALID_STANDARD_OBSERVATION_NAME = "1305";

    public SubscriptionServiceClient(ApiClientConfiguration apiClientConfiguration) {
        super(apiClientConfiguration);
    }

    public Response subscribeToApplication(String userId) {
        LOGGER.info(createLogMessage("Subscribing to application", userId, applicationName, propositionName));
        String apiEndpoint = "/subscription/applications/" + applicationName + "/users/" + userId + "/subscribe";
        HttpHeaders headers = new HttpHeaders();
        sign(headers, apiEndpoint, "", HttpMethod.POST, "");
        Response response = sendRequest(HttpMethod.POST, apiEndpoint, "", headers, "");
        if (!response.code.equals(RESPONSE_OK)) {
            LOGGER.error(createLogMessage("Error subscribing to application:", userId, applicationName, propositionName, response));
        }
        LOGGER.info(createLogMessage("Subscribed to application", userId, applicationName, propositionName, response));
        return response;
    }

    public Response subscribeToProposition(String userId) {
        LOGGER.info(createLogMessage("Subscribing to proposition", userId, applicationName, propositionName));
        String apiEndpoint = "/subscription/applications/" + applicationName + "/propositions/" + propositionName + "/users/" + userId + "/subscribe";
        HttpHeaders headers = new HttpHeaders();
        sign(headers, apiEndpoint, "", HttpMethod.POST, "");
        Response response = sendRequest(HttpMethod.POST, apiEndpoint, "", headers, "");
        if (!response.code.equals(RESPONSE_OK)) {
            LOGGER.error(createLogMessage("Error subscribing to proposition:", userId, applicationName, propositionName, response));
        }
        LOGGER.info(createLogMessage("Subscribed to proposition", userId, applicationName, propositionName, response));
        return response;
    }

    public Response closeAccount(String userId, boolean wipeData, String accessToken) {
        String apiEndpoint = "/subscription/applications/" + applicationName + "/users/" + userId + "/close";
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);
        Map<String, String> requestBody = ImmutableMap.of("deleteDataFlag", wipeData ? "Yes" : "No");

        return sendRequest(HttpMethod.PUT, apiEndpoint, "", headers, requestBody);
    }

    public Response storeTermsAndConditionsAccepted(String userId, String standardObservationName, String consentStatus, String consentCode, String countryCode, String documentVersion, String deviceIdentificationNumber) {
        LOGGER.info(createLogMessage("Storing terms and conditions to HSDP", userId, standardObservationName, consentStatus, consentCode, countryCode, documentVersion));
        String apiEndpoint = "/subscription/users/" + userId + "/termsAndConditions";
        StoreTermsAndConditionsRequest request = new StoreTermsAndConditionsRequest(applicationName, TERMS_AND_CONDITIONS_DOCUMENT_ID, documentVersion,
                countryCode,
                consentCode, standardObservationName, propositionName, consentStatus, deviceIdentificationNumber);
        HttpHeaders headers = new HttpHeaders();
        sign(headers, apiEndpoint, "", HttpMethod.PUT, request);
        Response response = sendRequest(HttpMethod.PUT, apiEndpoint, "", headers, request);
        if (!response.code.equals(RESPONSE_OK)) {
            LOGGER.error(
                    createLogMessage("Error storing terms and conditions", userId, response, standardObservationName, consentStatus, consentCode, countryCode, documentVersion));
            throw new TermsAndConditionRequest(response.message);
        }
        LOGGER.info(createLogMessage("Stored terms and conditions", userId, response, standardObservationName, consentStatus, consentCode, countryCode, documentVersion));
        return response;
    }

    public TermsAndConditionResponse retrieveLatestTermsAndConditions(String userId, String standardObservationName, String deviceIdentificationNumber, String documentVersion, String consentCode) {
        String apiEndpoint = "/subscription/applications/" + applicationName + "/users/" + userId + "/termsAndConditions";
        String queryParams = String.format("consentCode=%s&standardObservationName=%s&propositionName=%s", consentCode, standardObservationName, propositionName);

        HttpHeaders headers = new HttpHeaders();
        sign(headers, apiEndpoint, queryParams, HttpMethod.GET, null);
        Response response = sendRequest(HttpMethod.GET, apiEndpoint, queryParams, headers, null);
        if (response.code.equals(INVALID_STANDARD_OBSERVATION_NAME)) {
            throw new InvalidStandardObservationName("Unknown observation name " + standardObservationName);
        } else if (!response.code.equals(RESPONSE_OK) && !response.code.equals(RESPONSE_NOT_FOUND)) {
            throw new TermsAndConditionRequest(response.message);
        }
        return createTermsAndConditionsResponse(response, standardObservationName, deviceIdentificationNumber, documentVersion);
    }

    public List<SubscriptionDetail> subscribeToStandardObservations(String feedVendor, String userId, List<String> standardObservationNames) {
        String apiEndpoint = "/subscription/applications/" + applicationName + "/propositions/" + propositionName + "/feedVendors/" + feedVendor + "/users/" + userId
                             + "/standardObservations/subscribe";

        HttpHeaders headers = new HttpHeaders();
        SubscribeToStandardObservation request = new SubscribeToStandardObservation(standardObservationNames);
        sign(headers, apiEndpoint, "", HttpMethod.POST, request);
        Response response = sendRequest(HttpMethod.POST, apiEndpoint, "", headers, request);

        if (!response.code.equals(RESPONSE_OK)) {
            LOGGER.error(createLogMessage("Error subscribing to standard observation", userId, response, standardObservationNames));
            throw new StandardObservationSubscriptionRequest(response.message);
        }

        List<SubscriptionDetail> subscriptionDetails = createSubscribeToStandardObservationsResponse(response);

        LOGGER.info(createLogMessage("Subscribed to standard observations", userId, subscriptionDetails));

        return subscriptionDetails;
    }

    public List<SubscriptionDetail> retrieveSubscriptions(String feedVendor, String userId) {
        String apiEndpoint = "/subscription/applications/" + applicationName + "/propositions/" + propositionName + "/feedVendors/" + feedVendor + "/users/" + userId
                             + "/standardObservations";

        HttpHeaders headers = new HttpHeaders();

        sign(headers, apiEndpoint, "", HttpMethod.GET, null);
        Response response = sendRequest(HttpMethod.GET, apiEndpoint, "", headers, null);

        if (!response.code.equals(RESPONSE_OK)) {
            LOGGER.error("Error retrieve standard observation subscriptions");
            throw new StandardObservationSubscriptionRequest(response.message);
        }

        List<SubscriptionDetail> subscriptionDetails = createRetrieveStandardObservationsResponse(response);

        LOGGER.info(createLogMessage("Retrieved standard observations subscriptions", userId, subscriptionDetails));

        return subscriptionDetails;
    }

    private TermsAndConditionResponse createTermsAndConditionsResponse(Response response, String requestedStandardObservationName, String deviceIdentificationNumber, String documentVersion) {
        List<Map<String, Object>> userDocumentsList = MapUtils.extract(response.rawBody, "exchange.userTermsAndConditionsCollection.userTermsAndConditionsList");
        TermsAndConditionResponse latestTermsAndCondition = null;
        if (userDocumentsList == null)
            return null;
        for (Map<String, Object> document : userDocumentsList) {
            DateTime termsAndConditionDate = DateTime.parse((String) document.get("startDate"));
            String standardObservationName = (String) document.get("standardObservationName");
            String receivedDeviceIdentificationNumber = (String) document.get("deviceIdentificationNumber");
            String receivedDocumentVersion = (String) document.get("documentVersion");
            if ((latestTermsAndCondition == null || termsAndConditionDate.isAfter(latestTermsAndCondition.startDate))
                && standardObservationName.equals(requestedStandardObservationName)
                && stringEqualsOrEmpty(deviceIdentificationNumber, receivedDeviceIdentificationNumber)
                && stringEqualsOrEmpty(documentVersion, receivedDocumentVersion)) {
                latestTermsAndCondition = new TermsAndConditionResponse();
                latestTermsAndCondition.startDate = termsAndConditionDate;
                latestTermsAndCondition.applicationName = (String) document.get("applicationName");
                latestTermsAndCondition.consentStatus = (String) document.get("consentStatus");
                latestTermsAndCondition.documentVersion = receivedDocumentVersion;
                latestTermsAndCondition.standardObservationName = standardObservationName;
                latestTermsAndCondition.deviceIdentificationNumber = receivedDeviceIdentificationNumber;
            }
        }
        return latestTermsAndCondition;
    }

    private boolean stringEqualsOrEmpty(String expectedString, String foundString) {
        return (expectedString.equals(foundString) || expectedString.equals(""));
    }

    private List<SubscriptionDetail> createSubscribeToStandardObservationsResponse(Response response) {
        List<Map<String, Object>> subscriptionDetailsList = MapUtils.extract(response.rawBody, "exchange.subscriptionDetails");

        if (subscriptionDetailsList == null)
            return null;

        List<SubscriptionDetail> result = new ArrayList<>();

        for (Map<String, Object> subscriptionDetail : subscriptionDetailsList) {
            SubscriptionDetail detail = new SubscriptionDetail();
            detail.userSubscriptionUUID = (String) subscriptionDetail.get("userSubscriptionUUID");
            detail.standardObservationName = (String) subscriptionDetail.get("standardObservationName");
            result.add(detail);
        }

        return result;
    }

    private List<SubscriptionDetail> createRetrieveStandardObservationsResponse(Response response) {
        List<Map<String, Object>> subscriptionDetailsList = MapUtils.extract(response.rawBody, "exchange.subcribedObservations");

        if (subscriptionDetailsList == null)
            return null;

        List<SubscriptionDetail> result = new ArrayList<>();

        for (Map<String, Object> subscriptionDetail : subscriptionDetailsList) {
            SubscriptionDetail detail = new SubscriptionDetail();
            detail.userSubscriptionUUID = (String) subscriptionDetail.get("subscriptionUUID");
            detail.standardObservationName = (String) subscriptionDetail.get("standardObservationName");
            result.add(detail);
        }

        return result;
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String applicationName, String propositionName) {
        return createLogMessageBuilder(message)
                .appendUserId(userId)
                .appendApplicationName(applicationName)
                .appendPropositionName(propositionName);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String applicationName, String propositionName, Response response) {
        return createLogMessage(message, userId, applicationName, propositionName).append(response);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String standardObservationName, String consentStatus, String consentCode, String countryCode, String documentVersion) {
        return createLogMessageBuilder(message)
                .appendUserId(userId)
                .appendStandardObservationName(standardObservationName)
                .appendConsentStatus(consentStatus)
                .appendConsentCode(consentCode)
                .appendCountryCode(countryCode)
                .appendDocumentVersion(documentVersion);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, Response response, String standardObservationName, String consentStatus, String consentCode, String countryCode, String documentVersion) {
        return createLogMessageBuilder(message)
                .appendUserId(userId)
                .append(response)
                .appendStandardObservationName(standardObservationName)
                .appendConsentStatus(consentStatus)
                .appendConsentCode(consentCode)
                .appendCountryCode(countryCode)
                .appendDocumentVersion(documentVersion);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, List<SubscriptionDetail> subscriptionDetails) {
        LogMessageBuilder builder = createLogMessageBuilder(message);
        builder.appendUserId(userId);
        for (SubscriptionDetail detail : subscriptionDetails) {
            builder.appendStandardObservationName(detail.standardObservationName);
            builder.appendSubscriptionId(detail.userSubscriptionUUID);
        }
        return builder;
    }

    private LogMessageBuilder createLogMessage(String message, String userId, Response response, List<String> standardObservationNames) {
        LogMessageBuilder builder = createLogMessageBuilder(message);
        builder.appendUserId(userId);
        builder.append(response);
        for (String standardObservationName : standardObservationNames) {
            builder.appendStandardObservationName(standardObservationName);
        }

        return builder;
    }
}
