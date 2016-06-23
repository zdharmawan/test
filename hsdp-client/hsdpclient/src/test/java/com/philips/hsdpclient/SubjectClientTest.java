/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.datamodel.Photo;
import com.philips.hsdpclient.datamodel.SubjectProfile;
import com.philips.hsdpclient.exception.*;
import com.philips.hsdpclient.util.KeyValuePair;
import com.philips.hsdpclient.util.ServerSpy;

public class SubjectClientTest extends BaseClientTest {

    private final String SUCCESS_JSON = "{\"exchange\": {\"meta\": {\"versionId\": \"string\",\"lastUpdated\": \"string\"}},\"responseCode\": \"200\",\"responseMessage\": \"Success\"}";
    private final String SUBJECT_ERROR_JSON = "{\"responseCode\": \"3575\", \"responseMessage\":\"Error.\"}";
    private String LAST_UPDATED_REQUIRED_ERROR = "{\"responseCode\": \"3598\", \"responseMessage\": \"Lastupdated field is mandatory.\"}";
    private String LAST_UPDATED_MISMATCH_ERROR = "{\"responseCode\": \"3614\", \"responseMessage\": \"Lastupdate date conflict.\"}";

    @Before
    public void before() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        subjectClient = new SubjectClient(clientConfiguration);
        subjectClient.setRestTemplate(serverSpy);
    }

    @Test
    public void createsSubjectProfile() {
        givenTheServerRespondsWith(
                BASE_URL + "/personalhealth/subjectmanagement/subject",
                "{\"exchange\":{ \"meta\":{ \"versionId\":\"1.0\", \"lastUpdated\":\"2015-01-26T13:27:00.00Z\" }, \"subjectUUID\":\"aaa287b6-04cb-4abe-b888-21ae59b44f60\" }, \"responseCode\":\"200\", \"responseMessage\":\"Success\" }");
        whenCreatingASubjectProfile(USER_ID, createSubjectProfile("", "Harry", "2011-05-05", "Male", null, new KeyValuePair("weight", "2.3")));
        thenSubjectProfileIsCreatedWithGuid("aaa287b6-04cb-4abe-b888-21ae59b44f60");
        thenSubjectProfileIsCreatedWithLastUpdatedDate(dateFrom("2015-01-26T13:27:00.00Z"));
        andTheSentBodyIs("{\"profile\":{\"name\":\"Harry\",\"birthday\":\"2011-05-05\",\"gender\":\"Male\",\"metadata\":{\"weight\":\"2.3\"}}}");
        andTheUsedMethodIs(HttpMethod.POST);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
        andHeaderIsSent("api-version", HSDP_API_VERSION);
        andHeaderIsSent("propositionName", HSDP_PROPOSITION_NAME);
        andHeaderIsSent("userUUID", USER_ID);
    }

    @Test
    public void createSubjectProfileWithoutBirthday() {
        givenTheServerRespondsWith(
                BASE_URL + "/personalhealth/subjectmanagement/subject",
                "{\"exchange\":{ \"meta\":{ \"versionId\":\"1.0\", \"lastUpdated\":\"2015-01-26T13:27:00.00Z\" }, \"subjectUUID\":\"aaa287b6-04cb-4abe-b888-21ae59b44f60\" }, \"responseCode\":\"200\", \"responseMessage\":\"Success\" }");
        whenCreatingASubjectProfile(USER_ID, createSubjectProfile("", "Harry", null, "Male", null));
        thenSubjectProfileIsCreatedWithGuid("aaa287b6-04cb-4abe-b888-21ae59b44f60");
        thenSubjectProfileIsCreatedWithLastUpdatedDate(dateFrom("2015-01-26T13:27:00.00Z"));
    }

    @Test(expected = ErrorCreatingSubjectProfile.class)
    public void throwsExceptionWhenHsdpRespondsWithErrorWhileCreatingSubjectProfile() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject", ERROR_JSON);
        whenCreatingASubjectProfile(USER_ID, createSubjectProfile("", "John", "2015-01-01", "Male", null));
    }

    @Test
    public void getsSubjectProfile() {
        givenTheServerRespondsWith(
                BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID,
                "{\"exchange\":{ \"meta\":{ \"versionId\":\"1.0\", \"lastUpdated\":\"2012-02-02T11:00:00.00Z\" }, \"profile\":{ \"name\":\"The happy boy\", \"givenName\":\"John\", \"familyName\":\"Doe\", \"middleName\":\"M\", \"birthday\":\"2012-03-23\", \"gender\":\"male\", \"preferences\":{ \"nutrition interests\":\"yes\", \"notify feeding time\":\"yes\" }, \"metadata\":{ \"First Child\":\"yes\", \"Hospital Name\":\"Children Hospital\" } } }, \"responseCode\":\"200\", \"responseMessage\":\"Success\" }");
        whenGettingSubjectProfile(USER_ID, SUBJECT_PROFILE_ID);
        thenBabyProfileIsReturned("The happy boy", "John", "Doe", "M", dateFrom("2012-03-23"), "male",
                new DateTime("2012-02-02T11:00:00.00Z", DateTimeZone.UTC), createMetaData("First Child", "yes"),
                createMetaData("Hospital Name", "Children Hospital"));
        thenSubjectProfileIsReturnedWithLastUpdatedDate(dateFrom("2012-02-02T11:00:00.00Z"));
        andTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.GET);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
        andHeaderIsSent("api-version", HSDP_API_VERSION);
        andHeaderIsSent("propositionName", HSDP_PROPOSITION_NAME);
        andHeaderIsSent("userUUID", USER_ID);
    }

    @Test
    public void getSubjectProfileWithoutBirthday() {
        givenTheServerRespondsWith(
                BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID,
                "{\"exchange\":{ \"meta\":{ \"versionId\":\"1.0\", \"lastUpdated\":\"2012-02-02T11:00:00.00Z\" }, \"profile\":{ \"name\":\"The happy boy\", \"givenName\":\"John\", \"familyName\":\"Doe\", \"middleName\":\"M\", \"birthday\":\"\", \"gender\":\"male\", \"preferences\":{ \"nutrition interests\":\"yes\", \"notify feeding time\":\"yes\" }, \"metadata\":{ \"First Child\":\"yes\", \"Hospital Name\":\"Children Hospital\" } } }, \"responseCode\":\"200\", \"responseMessage\":\"Success\" }");
        whenGettingSubjectProfile(USER_ID, SUBJECT_PROFILE_ID);
        thenBabyProfileIsReturned("The happy boy", "John", "Doe", "M", null, "male",
                DateTime.parse("2012-02-02T11:00:00:00Z", DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss:SSSZ").withZoneUTC()), createMetaData("First Child", "yes"),
                createMetaData("Hospital Name", "Children Hospital"));
        thenSubjectProfileIsReturnedWithLastUpdatedDate(dateFrom("2012-02-02T11:00:00.00Z"));
    }

    @Test(expected = ErrorGettingSubjectProfile.class)
    public void throwsExceptionWhenHsdpRespondsWithErrorWhileGettingSubjectProfile() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, ERROR_JSON);
        whenGettingSubjectProfile(USER_ID, SUBJECT_PROFILE_ID);
    }

    @Test
    public void returnsNullWhenImageDoesNotExist() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo", ERROR_PHOTO_NOT_FOUND_JSON);
        whenGettingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID);
        thenNoSubjectPhotoIsReturned();
    }

    @Test
    public void updatesSubjectProfile() {
        givenTheServerRespondsWith(
                BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID,
                HttpMethod.PUT,
                "{ \"exchange\":{ \"meta\":{ \"versionId\":\"1.0\", \"lastUpdated\":\"2015-01-26T13:27:00.00Z\" }, \"subjectUUID\":\"aaa287b6-04cb-4abe-b888-21ae59b44f60\" }, \"responseCode\":\"200\", \"responseMessage\":\"Success\" }");
        whenUpdatingSubjectProfile(
                USER_ID,
                createSubjectProfile(SUBJECT_PROFILE_ID, "John", "2011-01-01", "Male", "2015-01-25T13:27:00.00Z", new KeyValuePair("weight", "2.3"), new KeyValuePair("height",
                        "2.0")));
        thenTheSentBodyIs("{\"profile\":{\"name\":\"John\",\"birthday\":\"2011-01-01\",\"gender\":\"Male\",\"metadata\":{\"weight\":\"2.3\",\"height\":\"2.0\"}},\"lastUpdated\":\"2015-01-25T13:27:00.00Z\"}");
        thenSubjectProfileIsUpdatedWithLastUpdatedDate(dateFrom("2015-01-26T13:27:00.00Z"));
        andTheUsedMethodIs(HttpMethod.PUT);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
        andHeaderIsSent("api-version", HSDP_API_VERSION);
        andHeaderIsSent("propositionName", HSDP_PROPOSITION_NAME);
        andHeaderIsSent("userUUID", USER_ID);
    }

    @Test(expected = ErrorUpdatingSubjectProfile.class)
    public void throwsExceptionWhenHsdpRespondsWithErrorWhileUpdatingSubjectProfile() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, ERROR_JSON);
        whenUpdatingSubjectProfile(USER_ID, createSubjectProfile(SUBJECT_PROFILE_ID, "", "2011-01-01", "", "2015-01-26T13:27:00.00Z"));
    }

    @Test(expected = ErrorUpdatingSubjectProfile.class)
    public void throwsExceptionWhenUpdatingSubjectProfileWithoutLastModifiedDate() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, LAST_UPDATED_REQUIRED_ERROR);
        whenUpdatingSubjectProfile(USER_ID, createSubjectProfile(SUBJECT_PROFILE_ID, "", "2011-01-01", "", "2015-01-26T13:27:00.00Z"));
    }

    @Test(expected = ErrorUpdatingSubjectPhoto.class)
    public void throwsExceptionWhenUpdatingSubjectPhotoWithoutLastModifiedDate() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo", LAST_UPDATED_REQUIRED_ERROR);
        whenUpdatingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID, "2015-01-26T13:27:00.00Z", "type", "value");
    }

    @Test(expected = ErrorDeletingSubjectProfile.class)
    public void throwsExceptionWhenDeletingSubjectProfileWithoutLastModifiedDate() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, LAST_UPDATED_REQUIRED_ERROR);
        whenDeletingTheSubjectProfile(USER_ID, createSubjectProfile(SUBJECT_PROFILE_ID, "", "2011-01-01", "", "2015-01-26T13:27:00.00Z"));
    }

    @Test(expected = ErrorLastUpdatedConflict.class)
    public void throwsExceptionWhenLastUpdatedIsWrong() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, HttpMethod.PUT, LAST_UPDATED_MISMATCH_ERROR);
        whenUpdatingSubjectProfile(USER_ID, createSubjectProfile(SUBJECT_PROFILE_ID, "", "2011-01-01", "", "2015-01-26T13:27:00.00Z"));
    }

    @Test(expected = ErrorLastUpdatedConflict.class)
    public void throwsExceptionWhenUpdatingPhotoAndLastUpdatedIsWrong() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo", LAST_UPDATED_MISMATCH_ERROR);
        whenUpdatingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID, "2015-01-26T13:27:00.00Z", "type", "value");
    }

    @Test(expected = ErrorLastUpdatedConflict.class)
    public void throwsExceptionWhenDeletingProfileAndLastUpdatedIsWrong() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, HttpMethod.DELETE, LAST_UPDATED_MISMATCH_ERROR);
        whenDeletingTheSubjectProfile(USER_ID, createSubjectProfile(SUBJECT_PROFILE_ID, "", "2011-01-01", "", "2015-01-26T13:27:00.00Z"));
    }

    @Test
    public void deletesSubjectProfile() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, HttpMethod.DELETE,
                "{\"exchange\":{ }, \"responseCode\":\"200\",\"responseMessage\":\"Success\" }");
        whenDeletingTheSubjectProfile(USER_ID,
                createSubjectProfile(SUBJECT_PROFILE_ID, "John", "2011-01-01", "Male", "2015-01-26T13:27:00.00Z", new KeyValuePair("weight", "2.3"), new KeyValuePair("height",
                        "2.0")));
        thenTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.DELETE);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
        andHeaderIsSent("api-version", HSDP_API_VERSION);
        andHeaderIsSent("propositionName", HSDP_PROPOSITION_NAME);
        andHeaderIsSent("userUUID", USER_ID);
        andHeaderIsSent("lastUpdated", "2015-01-26T13:27:00.00Z");
    }

    @Test(expected = ErrorDeletingSubjectProfile.class)
    public void throwsExceptionWhenHsdpRespondsWithErrorWhileDeletingSubjectProfile() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, HttpMethod.DELETE, SUBJECT_ERROR_JSON);
        whenDeletingTheSubjectProfile(USER_ID, createSubjectProfile(SUBJECT_PROFILE_ID, "", "2011-01-01", "", "2015-01-26T13:27:00.00Z"));
    }

    @Test(expected = ErrorDeletingSubjectProfile.class)
    public void throwsExceptionWhenHsdpRespondsWithErrorWhileDeletingSubjectProfileWithoutLastModifiedDate() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID, HttpMethod.DELETE, SUBJECT_ERROR_JSON);
        whenDeletingTheSubjectProfile(USER_ID,
                createSubjectProfile(SUBJECT_PROFILE_ID, "John", "2011-01-01", "Male", null, new KeyValuePair("weight", "2.3"), new KeyValuePair("height",
                        "2.0")));
    }

    @Test
    public void createsSubjectPhoto() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo",
                "{\"exchange\": {\"meta\": {\"versionId\": \"string\",\"lastUpdated\": \"string\"}},\"responseCode\": \"200\",\"responseMessage\": \"Success\"}");
        whenCreatingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID, "png", "BINARY");
        thenSubjectPhotoIsCreatedWithLastUpdatedDate();
        thenTheSentBodyIs("{\"photo\":{\"type\":\"png\",\"value\":\"BINARY\"}}");
        andTheUsedMethodIs(HttpMethod.POST);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
        andHeaderIsSent("api-version", HSDP_API_VERSION);
        andHeaderIsSent("propositionName", HSDP_PROPOSITION_NAME);
        andHeaderIsSent("userUUID", USER_ID);
    }

    @Test(expected = ErrorCreatingSubjectPhoto.class)
    public void throwsExceptionWhenHsdpRespondsWithErrorWhileCreatingSubjectPhoto() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo", ERROR_JSON);
        whenCreatingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID, "PNG", "BINARY");
    }

    @Test
    public void getsSubjectPhoto() {
        givenTheServerRespondsWith(
                BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo",
                "{\"exchange\":{\"meta\":{\"versionId\":\"1.0\",\"lastUpdated\":\"2015-10-07T09:45:37.00Z\"},\"photo\":{\"type\":\"png\",\"value\":\"BINARY\"}},\"responseCode\":\"200\",\"responseMessage\":\"Success\"})");
        whenGettingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID);
        thenSubjectPhotoIsReturned("png", "BINARY");
        thenSubjectPhotoIsReturnedWithLastUpdatedDate(dateFrom("2015-10-07T09:45:37.00Z"));
        thenTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.GET);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
        andHeaderIsSent("api-version", HSDP_API_VERSION);
        andHeaderIsSent("propositionName", HSDP_PROPOSITION_NAME);
        andHeaderIsSent("userUUID", USER_ID);
    }

    @Test(expected = ErrorGettingSubjectPhoto.class)
    public void throwsExceptionWhenHsdpRespondsWithErrorWhileGettingSubjectPhoto() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo", ERROR_JSON);
        whenGettingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID);
    }

    @Test
    public void updatesSubjectPhoto() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo", HttpMethod.PUT, SUCCESS_JSON);
        whenUpdatingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID, "2015-10-07T09:45:37.00Z", "jpg", "AnotherBinary");
        thenSubjectPhotoIsUpdatedWithLastUpdatedDate();
        thenTheSentBodyIs("{\"photo\":{\"type\":\"jpg\",\"value\":\"AnotherBinary\"},\"lastUpdated\":\"2015-10-07T09:45:37.00Z\"}");
        andTheUsedMethodIs(HttpMethod.PUT);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
        andHeaderIsSent("api-version", HSDP_API_VERSION);
        andHeaderIsSent("propositionName", HSDP_PROPOSITION_NAME);
        andHeaderIsSent("userUUID", USER_ID);
    }

    @Test(expected = ErrorUpdatingSubjectPhoto.class)
    public void throwsExceptionWhenHsdpRespondsWithErrorWhileUpdatingSubjectPhoto() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo", ERROR_JSON);
        whenUpdatingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID, "2015-10-07T09:45:37.00Z", "type", "value");
    }

    @Test(expected = SubjectPhotoNotFound.class)
    public void throwsExceptionWhenPhotoCannotBeFoundInHsdp() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/subjectmanagement/subject/" + SUBJECT_PROFILE_ID + "/photo", HttpMethod.PUT, PHOTO_NOT_FOUND_JSON);
        whenUpdatingSubjectPhoto(USER_ID, SUBJECT_PROFILE_ID, "2015-10-07T09:45:37.00Z", "type", "value");
    }

    private SubjectProfile createSubjectProfile(String guid, String name, String birthday, String gender, String lastUpdated, KeyValuePair... metadata) {
        SubjectProfile profile = new SubjectProfile();
        profile.guid = guid;
        profile.name = name;
        profile.birthday = new DateTime(birthday, DateTimeZone.UTC);
        profile.gender = gender;
        if (lastUpdated != null) {
            profile.lastUpdated = new DateTime(lastUpdated, DateTimeZone.UTC);
        }

        for (KeyValuePair keyValuePair : metadata) {
            profile.metadata.put(keyValuePair.key, keyValuePair.value);
        }
        return profile;
    }

    private void whenDeletingTheSubjectProfile(String userId, SubjectProfile subjectProfile) {
        subjectClient.deleteProfile(userId, subjectProfile);
    }

    private void whenCreatingSubjectPhoto(String userId, String subjectProfileId, String type, String value) {
        createdPhotoLastModifiedDate = subjectClient.createPhoto(userId, subjectProfileId, new com.philips.hsdpclient.datamodel.Photo(type, value, null));
    }

    private void whenGettingSubjectProfile(String userId, String subjectProfileId) {
        returnedSubjectProfile = subjectClient.getProfile(userId, subjectProfileId);
    }

    private void whenGettingSubjectPhoto(String userId, String subjectProfileId) {
        returnedPhoto = subjectClient.getPhoto(userId, subjectProfileId);
    }

    private void whenCreatingASubjectProfile(String userId, SubjectProfile subjectProfile) {
        subjectClient.createProfile(userId, subjectProfile);
        createdSubjectProfile = subjectProfile;
    }

    private void whenUpdatingSubjectProfile(String userId, SubjectProfile subjectProfile) {
        subjectClient.update(userId, subjectProfile);
        updatedSubjectProfile = subjectProfile;
    }

    private void whenUpdatingSubjectPhoto(String userId, String subjectProfileId, String lastUpdated, String type, String value) {
        updatedPhotoLastModifiedDate = subjectClient.updatePhoto(userId, subjectProfileId, new Photo(type, value, dateFrom(lastUpdated)));
    }

    private void thenSubjectProfileIsCreatedWithGuid(String expectedSubjectProfileId) {
        assertEquals(expectedSubjectProfileId, createdSubjectProfile.guid);
    }

    private void thenSubjectProfileIsCreatedWithLastUpdatedDate(DateTime expectedLastUpdated) {
        assertEquals(expectedLastUpdated, createdSubjectProfile.lastUpdated);
    }

    private void thenSubjectProfileIsUpdatedWithLastUpdatedDate(DateTime expectedLastUpdated) {
        assertEquals(expectedLastUpdated, updatedSubjectProfile.lastUpdated);
    }

    private void thenSubjectProfileIsReturnedWithLastUpdatedDate(DateTime expectedLastUpdated) {
        assertEquals(expectedLastUpdated, returnedSubjectProfile.lastUpdated);
    }

    private void thenSubjectPhotoIsCreatedWithLastUpdatedDate() {
        assertEquals("string", createdPhotoLastModifiedDate);
    }

    private void thenSubjectPhotoIsUpdatedWithLastUpdatedDate() {
        assertEquals("string", updatedPhotoLastModifiedDate);
    }

    private void thenSubjectPhotoIsReturnedWithLastUpdatedDate(DateTime expectedLastUpdated) {
        assertEquals(expectedLastUpdated, returnedPhoto.lastUpdated);
    }

    private void thenSubjectPhotoIsReturned(String png, String binary) {
        assertEquals(png, returnedPhoto.type);
        assertEquals(binary, returnedPhoto.base64Value);
    }

    private void thenNoSubjectPhotoIsReturned() {
        assertNull(returnedPhoto);
    }

    private void thenBabyProfileIsReturned(String name, String givenName, String familyName, String middleName, DateTime birthday, String gender, DateTime lastUpdated, KeyValuePair... metadatas) {
        assertEquals(name, returnedSubjectProfile.name);
        assertEquals(givenName, returnedSubjectProfile.givenName);
        assertEquals(familyName, returnedSubjectProfile.familyName);
        assertEquals(middleName, returnedSubjectProfile.middleName);
        assertEquals(birthday, returnedSubjectProfile.birthday);
        assertEquals(gender, returnedSubjectProfile.gender);
        assertEquals(lastUpdated, returnedSubjectProfile.lastUpdated);
        assertEquals(metadatas.length, returnedSubjectProfile.metadata.size());
        for (KeyValuePair pair : metadatas) {
            assertTrue(returnedSubjectProfile.metadata.containsKey(pair.key));
            assertEquals(returnedSubjectProfile.metadata.get(pair.key), pair.value);
        }
    }

    private KeyValuePair createMetaData(String key, String value) {
        return new KeyValuePair(key, value);
    }

    private DateTime dateFrom(String date) {
        if (date == null)
            return null;
        return new DateTime(date, DateTimeZone.UTC);
    }

    private String createdSubjectProfileId;
    private SubjectProfile createdSubjectProfile;
    private SubjectProfile returnedSubjectProfile;
    private SubjectProfile updatedSubjectProfile;
    private String createdPhotoLastModifiedDate;
    private Photo returnedPhoto;
    private String updatedPhotoLastModifiedDate;
    private SubjectClient subjectClient;
}
