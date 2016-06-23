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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.google.common.collect.ImmutableMap;
import com.philips.hsdpclient.exception.*;
import com.philips.hsdpclient.logging.LogMessageBuilder;
import com.philips.hsdpclient.request.*;
import com.philips.hsdpclient.response.Response;
import com.philips.hsdpclient.response.UserRegistrationResponse;
import com.philips.hsdpclient.util.MapUtils;

public class UserManagementClient extends ApiClient {

    private static final Logger LOGGER = Logger.getLogger(UserManagementClient.class);
    private static final String PATH_ACTIVATE_USER = "/usermanagement/users/activate";
    private static final String PATH_RECOVER_PASSWORD = "/authentication/credential/recoverPassword";
    private static final String PATH_CHANGE_FORGOTTEN_PASSWORD = "/authentication/credential/changePasswordWithCode";
    private static final String PATH_CHANGE_PASSWORD = "/authentication/credential/changePassword";
    private static final String PATH_USERS = "/usermanagement/users";
    private static final String ACCESS_TOKEN_EXPIRED = "1008";

    public UserManagementClient(ApiClientConfiguration apiClientConfiguration) {
        super(apiClientConfiguration);
    }

    private void addCountryCodeToHeaders(HttpHeaders headers, String currentLocation) {
        headers.add("Country-Code", currentLocation);
    }

    public UserRegistrationResponse registerUser(UserProfile registerUserRequest) {
        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();

        addCountryCodeToHeaders(headers, registerUserRequest.profile.currentLocation);
        sign(headers, PATH_USERS, queryParams, HttpMethod.POST, registerUserRequest);
        Response response = sendRequest(HttpMethod.POST, PATH_USERS, queryParams, headers, registerUserRequest);

        String userId = MapUtils.extract(response.rawBody, "exchange.user.userUUID");
        return new UserRegistrationResponse(userId, response.rawBody);
    }

    public Response changePassword(String loginId, String currentPassword, String newPassword, String accessToken) {
        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);

        Map<String, String> requestBody = ImmutableMap.of(
                "loginId", loginId,
                "currentPassword", currentPassword,
                "newPassword", newPassword);

        return sendRequest(HttpMethod.POST, PATH_CHANGE_PASSWORD, queryParams, headers, requestBody);
    }

    public Response changeForgottenPassword(String code, String newPassword, String redirectUri) {
        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();

        Map<String, String> requestBody = ImmutableMap.of(
                "newPassword", newPassword,
                "confirmPassword", newPassword,
                "code", code,
                "redirectURI", redirectUri);

        sign(headers, PATH_CHANGE_FORGOTTEN_PASSWORD, queryParams, HttpMethod.POST, requestBody);
        return sendRequest(HttpMethod.POST, PATH_CHANGE_FORGOTTEN_PASSWORD, queryParams, headers, requestBody);
    }

    public Response resetPassword(String loginId) {
        Map<String, String> requestBody = ImmutableMap.of("loginId", loginId);
        return resetPassword(requestBody);
    }

    public Response resetPassword(String email, String redirectUri) {
        Map<String, String> requestBody = ImmutableMap.of("loginId", email, "redirectURI", redirectUri);
        return resetPassword(requestBody);
    }

    public void updateProfile(String userId, Profile userProfile, String accessToken) {
        LOGGER.info(createLogMessage("Updating user profile", userId, accessToken));
        String endPoint = "/usermanagement/users/" + userId + "/profile";
        String queryParams = String.format("applicationName=%s", applicationName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("accessToken", accessToken);
        Response response = sendRequest(HttpMethod.PUT, endPoint, queryParams, headers, userProfile);

        if (response.code.equals(ACCESS_TOKEN_EXPIRED)) {
            LOGGER.error(createLogMessage("Error updating user profile: access token expired", userId, accessToken, response, userProfile));
            throw new TokenExpired();
        } else if (!response.code.equals(RESPONSE_OK)) {
            LOGGER.error(createLogMessage("Error updating user profile", userId, accessToken, response, userProfile));
            throw new ErrorUpdatingUserProfile(response.message);
        }
        LOGGER.info(createLogMessage("Updated user profile", userId, accessToken, response));
    }

    public Response resendConfirmation(String email) {
        Map<String, String> requestBody = ImmutableMap.of("loginId", email);

        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();
        sign(headers, PATH_ACTIVATE_USER, queryParams, HttpMethod.PUT, requestBody);
        return sendRequest(HttpMethod.PUT, PATH_ACTIVATE_USER, queryParams, headers, requestBody);
    }

    public UserProfile getProfile(String userId, String accessToken) {
        String endPoint = "/usermanagement/users/" + userId + "/profile";
        String queryParams = String.format("applicationName=%s", applicationName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("accessToken", accessToken);
        //sign(headers, endPoint, queryParams, HttpMethod.GET, null);
        Response response = sendRequest(HttpMethod.GET, endPoint, queryParams, headers, null);

        if (!response.code.equals(RESPONSE_OK)) {
            LOGGER.error(createLogMessageBuilder("Error getting user profile").appendUserId(userId).append(response));
            throw new ErrorGettingUserProfile(response.message);
        }

        return getUserProfile(response);
    }

    private Response resetPassword(Map<String, String> requestBody) {
        String queryParams = "applicationName=" + applicationName;
        HttpHeaders headers = new HttpHeaders();

        sign(headers, PATH_RECOVER_PASSWORD, queryParams, HttpMethod.POST, requestBody);
        return sendRequest(HttpMethod.POST, PATH_RECOVER_PASSWORD, queryParams, headers, requestBody);
    }

    private List<Photo> getPhotos(Map<String, Object> responseMap) {
        List<Map<String, String>> rawPhotos = MapUtils.extract(responseMap, "exchange.user.profile.photos");

        if (rawPhotos == null)
            return null;

        List<Photo> photos = new ArrayList<>();

        for (Map<String, String> photoMap : rawPhotos)
            photos.add(new Photo(photoMap.get("type"), photoMap.get("value")));

        return photos;
    }

    private UserProfile getUserProfile(Response response) {
        String userUUID = MapUtils.extract(response.rawBody, "exchange.user.userUUID");
        String loginId = MapUtils.extract(response.rawBody, "exchange.user.loginId");
        String givenName = MapUtils.extract(response.rawBody, "exchange.user.profile.givenName");
        String middleName = MapUtils.extract(response.rawBody, "exchange.user.profile.middleName");
        String displayName = MapUtils.extract(response.rawBody, "exchange.user.profile.displayName");
        String gender = MapUtils.extract(response.rawBody, "exchange.user.profile.gender");
        String birthDay = MapUtils.extract(response.rawBody, "exchange.user.profile.birthday");
        String preferredLanguage = MapUtils.extract(response.rawBody, "exchange.user.profile.preferredLanguage");
        String currentLocation = MapUtils.extract(response.rawBody, "exchange.user.profile.currentLocation");
        String familyName = MapUtils.extract(response.rawBody, "exchange.user.profile.familyName");
        String locale = MapUtils.extract(response.rawBody, "exchange.user.profile.locale");
        String timeZone = MapUtils.extract(response.rawBody, "exchange.user.profile.timeZone");
        String country = MapUtils.extract(response.rawBody, "exchange.user.profile.primaryAddress.country");
        Double height = remapZeroOrNegativeToNull(MapUtils.extract(response.rawBody, "exchange.user.profile.height"));
        Double weight = remapZeroOrNegativeToNull(MapUtils.extract(response.rawBody, "exchange.user.profile.weight"));
        List<Photo> photos = getPhotos(response.rawBody);
        Address address = null;
        if (country != null && !country.equals("")) {
            address = new Address(country);
        }
        return new UserProfile(userUUID, loginId, null, new Profile(
                givenName, middleName, familyName, birthDay, currentLocation, displayName, locale, gender, timeZone, preferredLanguage, height, weight,
                address,
                photos));
    }

    private Double remapZeroOrNegativeToNull(Double value) {
        if (value == null || value <= 0.0)
            return null;

        return value;
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String accessToken) {
        return createLogMessageBuilder(message).appendUserId(userId).appendAccessToken(accessToken);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String accessToken, Response response) {
        return createLogMessage(message, userId, accessToken).append(response);
    }

    private LogMessageBuilder createLogMessage(String message, String userId, String accessToken, Response response, Profile userProfile) {
        return createLogMessage(message, userId, accessToken, response).append(userProfile);
    }

}
