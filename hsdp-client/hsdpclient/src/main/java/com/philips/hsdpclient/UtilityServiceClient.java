/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static com.philips.hsdpclient.logging.LogMessageBuilder.createLogMessageBuilder;

import org.apache.log4j.Logger;
import org.springframework.http.*;

import com.philips.hsdpclient.logging.LogMessageBuilder;
import com.philips.hsdpclient.response.Response;

public class UtilityServiceClient extends ApiClient {

    private Logger LOGGER = Logger.getLogger(UtilityServiceClient.class);

    public UtilityServiceClient(ApiClientConfiguration clientConfiguration) {
        super(clientConfiguration);
    }

    public void deleteTermsAndConditions(String userId) {
        LOGGER.info(createLogMessage("Deleting terms and conditions", userId));
        String endpoint = "/subscription/applications/" + applicationName + "/users/" + userId + "/termsAndConditions";
        HttpHeaders headers = new HttpHeaders();
        sign(headers, endpoint, "", HttpMethod.DELETE, null);
        headers.add("applicationName", applicationName);
        headers.add("userUUID", userId);
        try {
            Response response = sendRequest(HttpMethod.DELETE, endpoint, "", headers, null);
            if (response.code.equals(HttpStatus.OK.toString())) {
                LOGGER.info(createLogMessage("Deleted terms and conditions", userId, response));
            }else{
                LOGGER.error(createLogMessage("Error deleting terms and conditions", userId, response));
            }
        } catch (Exception e) {
            LOGGER.error(createLogMessage("Error deleting terms and conditions", userId, e.getMessage()));
        }
    }

    private LogMessageBuilder createLogMessage(String message, String userId) {
        return createLogMessageBuilder(message).appendUserId(userId);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, Response response) {
        return createLogMessage(message, userId).append(response);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String errorMessage) {
        return createLogMessage(message, userId).appendErrorMessage(errorMessage);
    }
}
