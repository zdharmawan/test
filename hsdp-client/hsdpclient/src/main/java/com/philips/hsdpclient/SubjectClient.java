/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static com.philips.hsdpclient.logging.LogMessageBuilder.createLogMessageBuilder;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.philips.hsdpclient.datamodel.Photo;
import com.philips.hsdpclient.datamodel.SubjectProfile;
import com.philips.hsdpclient.exception.*;
import com.philips.hsdpclient.request.SubjectPhotoRequestEntity;
import com.philips.hsdpclient.request.SubjectProfileRequestEntity;
import com.philips.hsdpclient.response.Response;
import com.philips.hsdpclient.util.DateTimeUtils;
import com.philips.hsdpclient.util.MapUtils;

public class SubjectClient extends ApiClient {

    private static final Logger LOGGER = Logger.getLogger(SubjectClient.class);
    private static final String IMAGE_NOT_FOUND = "3597";
    private static final String LAST_UPDATED_CONFLICT = "3614";

    public SubjectClient(ApiClientConfiguration apiClientConfiguration) {
        super(apiClientConfiguration);
    }

    public void createProfile(String userId, SubjectProfile subjectProfile) {
        logInfo("Creating subject profile", userId);
        String endpoint = "/personalhealth/subjectmanagement/subject";
        HttpHeaders headers = new HttpHeaders();
        SubjectProfileRequestEntity requestEntity = new SubjectProfileRequestEntity(subjectProfile);
        sign(headers, endpoint, "", HttpMethod.POST, requestEntity);
        headers.add("applicationName", applicationName);
        headers.add("propositionName", propositionName);
        headers.add("api-version", apiVersion);
        headers.add("userUUID", userId);
        Response response = sendRequest(HttpMethod.POST, endpoint, "", headers, requestEntity);
        if (!response.code.equals(RESPONSE_OK)) {
            throw new ErrorCreatingSubjectProfile(response.message);
        }
        String subjectID = MapUtils.extract(response.rawBody, "exchange.subjectUUID");
        logInfo("Created subject profile", userId, subjectID, response);

        subjectProfile.guid = subjectID;
        subjectProfile.lastUpdated = DateTimeUtils.toObservationDateTime(MapUtils.extract(response.rawBody, "exchange.meta.lastUpdated"));
    }

    public SubjectProfile getProfile(String userId, String subjectProfileId) {
        String endpoint = "/personalhealth/subjectmanagement/subject/" + subjectProfileId;
        HttpHeaders headers = new HttpHeaders();
        sign(headers, endpoint, "", HttpMethod.GET, null);
        headers.add("applicationName", applicationName);
        headers.add("propositionName", propositionName);
        headers.add("api-version", apiVersion);
        headers.add("userUUID", userId);
        Response response = sendRequest(HttpMethod.GET, endpoint, "", headers, null);
        if (!response.code.equals(RESPONSE_OK)) {
            throw new ErrorGettingSubjectProfile(response.message);
        }
        return createSubjectProfileFrom(response);
    }

    public void update(String userId, SubjectProfile subjectProfile) {
        logInfo("Updating subject profile", userId, subjectProfile.guid);
        String endpoint = "/personalhealth/subjectmanagement/subject/" + subjectProfile.guid;
        SubjectProfileRequestEntity requestEntity = new SubjectProfileRequestEntity(subjectProfile);
        HttpHeaders headers = new HttpHeaders();
        sign(headers, endpoint, "", HttpMethod.PUT, requestEntity);
        headers.add("applicationName", applicationName);
        headers.add("propositionName", propositionName);
        headers.add("api-version", apiVersion);
        headers.add("userUUID", userId);
        Response response = sendRequest(HttpMethod.PUT, endpoint, "", headers, requestEntity);
        if (!response.code.equals(RESPONSE_OK)) {
            if (response.code.equals(LAST_UPDATED_CONFLICT)) {
                throw new ErrorLastUpdatedConflict(response.message);
            }
            throw new ErrorUpdatingSubjectProfile(response.message);
        }
        logInfo("Updated subject profile", userId, subjectProfile.guid, response);
        subjectProfile.lastUpdated = DateTimeUtils.toObservationDateTime(MapUtils.extract(response.rawBody, "exchange.meta.lastUpdated"));
    }

    public void deleteProfile(String userId, SubjectProfile subjectProfile) {
        logInfo("Deleting subject profile", userId, subjectProfile.guid);
        String endpoint = "/personalhealth/subjectmanagement/subject/" + subjectProfile.guid;
        HttpHeaders headers = new HttpHeaders();
        sign(headers, endpoint, "", HttpMethod.DELETE, null);
        headers.add("applicationName", applicationName);
        headers.add("propositionName", propositionName);
        headers.add("api-version", apiVersion);
        headers.add("userUUID", userId);
        headers.add("lastUpdated", DateTimeUtils.asString(subjectProfile.lastUpdated));
        Response response = sendRequest(HttpMethod.DELETE, endpoint, "", headers, null);
        if (response.code.equals(LAST_UPDATED_CONFLICT)) {
            throw new ErrorLastUpdatedConflict(response.message);
        } else if (!response.code.equals(RESPONSE_OK)) {
            throw new ErrorDeletingSubjectProfile(response.message);
        }
        logInfo("Deleted subject profile", userId, subjectProfile.guid, response);
    }

    public String createPhoto(String userId, String subjectProfileId, com.philips.hsdpclient.datamodel.Photo photo) {
        logInfo("Creating subject profile photo", userId, subjectProfileId);
        String endpoint = "/personalhealth/subjectmanagement/subject/" + subjectProfileId + "/photo";
        HttpHeaders headers = new HttpHeaders();
        SubjectPhotoRequestEntity requestEntity = new SubjectPhotoRequestEntity(photo, null);
        sign(headers, endpoint, "", HttpMethod.POST, requestEntity);
        headers.add("applicationName", applicationName);
        headers.add("propositionName", propositionName);
        headers.add("api-version", apiVersion);
        headers.add("userUUID", userId);
        Response response = sendRequest(HttpMethod.POST, endpoint, "", headers, requestEntity);
        if (!response.code.equals(RESPONSE_OK)) {
            throw new ErrorCreatingSubjectPhoto(response.message);
        }
        logInfo("Created subject profile photo", userId, subjectProfileId, response);
        return MapUtils.extract(response.rawBody, "exchange.meta.lastUpdated");
    }

    public Photo getPhoto(String userId, String subjectProfileId) {
        String endpoint = "/personalhealth/subjectmanagement/subject/" + subjectProfileId + "/photo";
        HttpHeaders headers = new HttpHeaders();
        sign(headers, endpoint, "", HttpMethod.GET, null);
        headers.add("applicationName", applicationName);
        headers.add("propositionName", propositionName);
        headers.add("api-version", apiVersion);
        headers.add("userUUID", userId);
        Response response = sendRequest(HttpMethod.GET, endpoint, "", headers, null);
        if (response.code.equals(IMAGE_NOT_FOUND))
            return null;
        if (!response.code.equals(RESPONSE_OK)) {
            throw new ErrorGettingSubjectPhoto(response.message);
        }
        return createPhotoFrom(response);
    }

    public String updatePhoto(String userId, String subjectProfileId, com.philips.hsdpclient.datamodel.Photo photo) {
        logInfo("Updating subject profile photo", userId, subjectProfileId);
        String endpoint = "/personalhealth/subjectmanagement/subject/" + subjectProfileId + "/photo";
        HttpHeaders headers = new HttpHeaders();
        SubjectPhotoRequestEntity requestEntity = new SubjectPhotoRequestEntity(photo, photo.lastUpdated);
        sign(headers, endpoint, "", HttpMethod.PUT, requestEntity);
        headers.add("applicationName", applicationName);
        headers.add("propositionName", propositionName);
        headers.add("api-version", apiVersion);
        headers.add("userUUID", userId);
        Response response = sendRequest(HttpMethod.PUT, endpoint, "", headers, requestEntity);
        if (response.code.equals(IMAGE_NOT_FOUND)) {
            throw new SubjectPhotoNotFound(response.message);
        } else if (response.code.equals(LAST_UPDATED_CONFLICT)) {
            throw new ErrorLastUpdatedConflict(response.message);
        } else if (!response.code.equals(RESPONSE_OK)) {
            throw new ErrorUpdatingSubjectPhoto(response.message);
        }
        logInfo("Updated subject profile photo", userId, subjectProfileId, response);
        return MapUtils.extract(response.rawBody, "exchange.meta.lastUpdated");
    }

    private SubjectProfile createSubjectProfileFrom(Response response) {
        SubjectProfile subjectProfile = new SubjectProfile();
        subjectProfile.name = MapUtils.extract(response.rawBody, "exchange.profile.name");
        subjectProfile.givenName = MapUtils.extract(response.rawBody, "exchange.profile.givenName");
        subjectProfile.familyName = MapUtils.extract(response.rawBody, "exchange.profile.familyName");
        subjectProfile.middleName = MapUtils.extract(response.rawBody, "exchange.profile.middleName");
        subjectProfile.gender = MapUtils.extract(response.rawBody, "exchange.profile.gender");
        subjectProfile.birthday = DateTimeUtils.toDateTime(MapUtils.extract(response.rawBody, "exchange.profile.birthday"));
        subjectProfile.lastUpdated = DateTimeUtils.toObservationDateTime(MapUtils.extract(response.rawBody, "exchange.meta.lastUpdated"));
        Object meta = MapUtils.extract(response.rawBody, "exchange.profile.metadata");
        if (!"".equals(meta)) {
            subjectProfile.metadata = MapUtils.extract(response.rawBody, "exchange.profile.metadata");
        }
        return subjectProfile;
    }

    private Photo createPhotoFrom(Response response) {
        String type = MapUtils.extract(response.rawBody, "exchange.photo.type");
        String base64Value = MapUtils.extract(response.rawBody, "exchange.photo.value");
        DateTime lastUpdated = DateTimeUtils.toObservationDateTime(MapUtils.extract(response.rawBody, "exchange.meta.lastUpdated"));
        return new Photo(type, base64Value, lastUpdated);
    }

    private void logInfo(String message, String userId) {
        LOGGER.info(createLogMessageBuilder(message)
                .appendUserId(userId));
    }

    private void logInfo(String message, String userId, String subjectId) {
        LOGGER.info(createLogMessageBuilder(message)
                .appendUserId(userId)
                .appendSubjectId(subjectId));
    }

    private void logInfo(String message, String userId, String subjectId, Response response) {
        LOGGER.info(createLogMessageBuilder(message)
                .appendUserId(userId)
                .appendSubjectId(subjectId)
                .append(response));
    }
}
