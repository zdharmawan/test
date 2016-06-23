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
import com.philips.hsdpclient.response.AuthenticationResponse;
import com.philips.hsdpclient.response.Response;
import com.philips.hsdpclient.util.MapUtils;

public class AuthenticationManagementClient extends ApiClient {
    private Logger LOGGER = Logger.getLogger(AuthenticationManagementClient.class);

    static class AuthenticationRequestJson {
        public String loginId;
        public String password;

        public AuthenticationRequestJson(String loginId, String password) {
            this.loginId = loginId;
            this.password = password;
        }
    }

    static class RefreshTokenRequest {
        public String refreshToken;

        public RefreshTokenRequest(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    public AuthenticationManagementClient(ApiClientConfiguration apiClientConfiguration) {
        super(apiClientConfiguration);
    }

    public AuthenticationResponse authenticate(String username, String password) {
        String apiEndpoint = "/authentication/login";
        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();
        AuthenticationRequestJson request = new AuthenticationRequestJson(username, password);

        sign(headers, apiEndpoint, queryParams, HttpMethod.POST, request);
        Response response = sendRequest(HttpMethod.POST, apiEndpoint, queryParams, headers, request);

        if (!"200".equals(response.code))
            return new AuthenticationResponse(response.rawBody);

        String accessToken = MapUtils.extract(response.rawBody, "exchange.accessCredential.accessToken");
        String refreshToken = MapUtils.extract(response.rawBody, "exchange.accessCredential.refreshToken");
        String expiresIn = MapUtils.extract(response.rawBody, "exchange.accessCredential.expiresIn");
        String userId = MapUtils.extract(response.rawBody, "exchange.user.userUUID");

        return new AuthenticationResponse(accessToken, refreshToken, Integer.parseInt(expiresIn), userId, response.rawBody);
    }

    public AuthenticationResponse refresh(String userId, String refreshToken) {
        LOGGER.info(createLogMessage("Refreshing access token", userId, refreshToken));
        String apiEndpoint = "/authentication/users/" + userId + "/refreshToken";
        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();

        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        sign(headers, apiEndpoint, queryParams, HttpMethod.PUT, request);
        Response response = sendRequest(HttpMethod.PUT, apiEndpoint, queryParams, headers, request);

        if (!response.code.equals(HttpStatus.OK.toString())) {
            LOGGER.error(createLogMessage("Error refreshing token", userId, refreshToken, response));
            return new AuthenticationResponse(response.rawBody);
        }

        String newAccessToken = MapUtils.extract(response.rawBody, "exchange.accessToken");
        String newRefreshToken = MapUtils.extract(response.rawBody, "exchange.refreshToken");
        String expiresIn = MapUtils.extract(response.rawBody, "exchange.expiresIn");

        LOGGER.info(createLogMessage("Refreshed access token", userId, newAccessToken, response));
        return new AuthenticationResponse(newAccessToken, newRefreshToken, Integer.parseInt(expiresIn), userId, response.rawBody);
    }

    public Response validateToken(String userId, String accessToken) {
        String apiEndpoint = "/authentication/users/" + userId + "/tokenStatus";
        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);

        return sendRequest(HttpMethod.GET, apiEndpoint, queryParams, headers, null);
    }

    public Response logout(String userId, String accessToken) {
        String apiEndpoint = "/authentication/users/" + userId + "/logout";
        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);

        return sendRequest(HttpMethod.PUT, apiEndpoint, queryParams, headers, null);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String refreshToken) {
        return createLogMessageBuilder(message)
                .appendUserId(userId)
                .appendAccessToken(refreshToken);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String refreshToken, Response response) {
        return createLogMessage(message, userId, refreshToken).append(response);
    }

}
