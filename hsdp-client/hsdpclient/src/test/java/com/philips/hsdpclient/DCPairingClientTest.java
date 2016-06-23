/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.datamodel.DCRelation;
import com.philips.hsdpclient.exception.*;
import com.philips.hsdpclient.util.ServerSpy;

public class DCPairingClientTest extends DCPairingBaseClientTest {

    @Before
    public void before() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        dcPairingClient = new DCPairingClient(clientConfiguration);
        dcPairingClient.setRestTemplate(serverSpy);
        returnedRelationId = null;
        expectedDCRelationsForUser = new ArrayList<>();
    }

    @Test
    public void deletesDCPairingByTrustorAndTrustee() {
        givenTheServerRespondsWith(BASE_URL + "/relation?trustor=urn:cphuser%7C" + USER_ID + "&trustee=%7C" + DEVICE_ID,
                "{\"issue\":[{\"Severity\":\"information\",\"Code\":{\"coding\":[{\"system\":\"Pairing\",\"code\":\"200\"}]},\"Details\":\"The requested relation is successfuly deleted.\"}]}");
        whenDeletingDCParingByTrustorAndTrustee(USER_ID, DEVICE_ID);
        thenTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.DELETE);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("api-version", API_VERSION);
    }

    @Test(expected = ErrorDeletingDCPairing.class)
    public void throwsExceptionWhenDCPairingRespondsWithErrorWhileDeletingDCPairingByTrustor() {
        givenTheServerRespondsWith(BASE_URL + "/relation?trustor=urn:cphuser%7C" + USER_ID + "&trustee=%7C" + DEVICE_ID, ERROR_DELETING_NO_RELATION_FOUND_JSON);
        whenDeletingDCParingByTrustorAndTrustee(USER_ID, DEVICE_ID);
    }

    @Test(expected = DCPairingNotFound.class)
    public void throwsExceptionWhenDCPairingRespondsWith404() {
        givenTheServerRespondsWith404Status(BASE_URL + "/relation?trustor=urn:cphuser%7C" + USER_ID + "&trustee=%7C" + DEVICE_ID);
        whenDeletingDCParingByTrustorAndTrustee(USER_ID, DEVICE_ID);
    }

    @Test
    public void upsertingDCPairing() {
        givenTheServerRespondsWithHeader(BASE_URL + "/relation",
                "{\"issue\":[{\"Severity\":\"information\",\"Code\":{\"coding\":[{\"system\":\"Pairing\",\"code\":\"201\"}]},\"Details\":\"Resource created.\"}]}",
                "https://uat.ps.cpp.philips.com/psrequesthandler/pairing/relation//1234");
        whenUpsertingDCPairing(USER_ID, createDCRelation("6093d435-81e5-46d5-aada-39a375761e99", DEVICE_ID, "2017-01-29T02:50:20.55Z",
                "{\"app\": \"uGrowApp\",\"prop\": \"uGrowProp\",\"vendor\": \"uGrow\",\"subjectIds\": [\"XXXXXX-a8bd-4420-aa67-a219d3ccbd19\"]}"));
        thenTheSentBodyIs("{\"resourceType\":\"relation\",\"trustor\":{\"system\":\"urn:cphuser\",\"value\":\"6093d435-81e5-46d5-aada-39a375761e99\"},\"trustee\":{\"system\":\"\",\"value\":\"123456FFFE78ABCD\"},\"expireDate\":\"2017-01-29T02:50:20.55Z\",\"type\":{\"system\":\"urn:ugrow\",\"value\":\"observation_receiver\"},\"permissions\":[],\"metadata\":\"{\\\"app\\\": \\\"uGrowApp\\\",\\\"prop\\\": \\\"uGrowProp\\\",\\\"vendor\\\": \\\"uGrow\\\",\\\"subjectIds\\\": [\\\"XXXXXX-a8bd-4420-aa67-a219d3ccbd19\\\"]}\"}");
        andTheUsedMethodIs(HttpMethod.PUT);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("api-version", API_VERSION);
        andTheResponseHeaderHasLocation("1234");
    }

    @Test
    public void getAllDCPairingForUser() {
        givenTheServerRespondsWith(
                BASE_URL + "/relation?trustor=urn:cphuser%7C" + USER_ID,
                "{\"entry\": [{\"resource\": {\"resourceType\":\"relation\",\"trustor\":{\"system\":\"urn:cphuser\",\"value\":\"6093d435-81e5-46d5-aada-39a375761e99\"},\"trustee\":{\"system\":\"\",\"value\":\"123456FFFE78ABCD\"},\"expireDate\":\"2017-01-29T02:50:20.55Z\",\"type\":{\"system\":\"urn:ugrow\",\"value\":\"observation_receiver\"},\"permissions\":[],\"metadata\":\"{\\\"app\\\": \\\"uGrowApp\\\",\\\"prop\\\": \\\"uGrowProp\\\",\\\"vendor\\\": \\\"uGrow\\\",\\\"subjectIds\\\": [\\\"XXXXXX-a8bd-4420-aa67-a219d3ccbd19\\\"]}\"}},"
                        + "{\"resource\": {\"resourceType\":\"relation\",\"trustor\":{\"system\":\"urn:cphuser\",\"value\":\"6093d435-81e5-46d5-aada-39a375761e99\"},\"trustee\":{\"system\":\"\",\"value\":\"123456FFFE78ABCE\"},\"expireDate\":\"2017-01-29T02:50:20.55Z\",\"type\":{\"system\":\"urn:ugrow\",\"value\":\"observation_receiver\"},\"permissions\":[],\"metadata\":\"{\\\"app\\\": \\\"uGrowApp\\\",\\\"prop\\\": \\\"uGrowProp\\\",\\\"vendor\\\": \\\"uGrow\\\",\\\"subjectIds\\\": [\\\"XXXXXX-a8bd-4420-aa67-a219d3ccbd19\\\"]}\"}}]}");
        whenGettingAllDCRelationsForUser(USER_ID);
        thenTheDCRelationsAreReturned();
    }

    @Test(expected = ErrorUpsertingDCParing.class)
    public void throwsExceptionWhenDCParingRespondsWithErrorWhileUpsertingDCParing() {
        givenTheServerRespondsWith(BASE_URL + "/relation", ERROR_UPSERTING_INVALID_TRUSTEE_JSON);
        whenUpsertingDCPairing(USER_ID, createDCRelation("6093d435-81e5-46d5-aada-39a375761e99", "", EXPIRE_DATE,
                "{\"app\": \"uGrowApp\",\"prop\": \"uGrowProp\",\"vendor\": \"uGrow\",\"subjectIds\": [\"d3e8af91-a8bd-4420-aa67-a219d3ccbd19\"]}"));
    }

    @Test(expected = ErrorGettingDCParing.class)
    public void throwsExceptionWhenDCParingRespondsWithErrorWhilegetAllDCPairingForUser() {
        givenTheServerRespondsWithError(BASE_URL + "/relation?trustor=urn:cphuser%7C" + USER_ID);
        whenGettingAllDCRelationsForUser(USER_ID);
    }

    public void whenUpsertingDCPairing(String userId, DCRelation relation) {
        returnedRelationId = dcPairingClient.upsertDCPairing(userId, relation);
    }

    private void whenGettingAllDCRelationsForUser(String userId) {
        returnedDCRelationsForUser = dcPairingClient.getAllDCRelations(userId);
    }

    private void whenDeletingDCParingByTrustorAndTrustee(String trustorId, String trusteeId) {
        dcPairingClient.deleteDCPairingByTrustorAndTrustee(trustorId, trusteeId);
    }

    private void andTheResponseHeaderHasLocation(String relationId) {
        assertEquals(relationId, returnedRelationId);
    }

    private void thenTheDCRelationsAreReturned() {
        createExpectedDCRelationsForUser();
        assertEquals(expectedDCRelationsForUser, returnedDCRelationsForUser);
    }

    private void createExpectedDCRelationsForUser() {
        DCRelation dcRelation = createDCRelation(USER_ID, DEVICE_ID, EXPIRE_DATE,
                "{\"app\": \"uGrowApp\",\"prop\": \"uGrowProp\",\"vendor\": \"uGrow\",\"subjectIds\": [\"XXXXXX-a8bd-4420-aa67-a219d3ccbd19\"]}");
        expectedDCRelationsForUser.add(dcRelation);
        dcRelation = createDCRelation(USER_ID, ANOTHER_DEVICE_ID, EXPIRE_DATE,
                "{\"app\": \"uGrowApp\",\"prop\": \"uGrowProp\",\"vendor\": \"uGrow\",\"subjectIds\": [\"XXXXXX-a8bd-4420-aa67-a219d3ccbd19\"]}");
        expectedDCRelationsForUser.add(dcRelation);
    }

    private DCRelation createDCRelation(String trustorValue, String trusteeValue, String expireDate, String metadata) {
        DCRelation relation = new DCRelation();
        relation.trustor = new DCRelation.Trustor();
        relation.trustor.value = trustorValue;
        relation.trustee = new DCRelation.Trustee();
        relation.trustee.value = trusteeValue;
        relation.expireDate = new DateTime(expireDate, DateTimeZone.UTC);
        relation.type = new DCRelation.Type();
        relation.permissions = new String[] {};
        relation.metadata = metadata;
        return relation;
    }

    private DCPairingClient dcPairingClient;
    private String returnedRelationId;
    private List<DCRelation> returnedDCRelationsForUser;
    private List<DCRelation> expectedDCRelationsForUser;
    private static final String DEVICE_ID = "123456FFFE78ABCD";
    private static final String ANOTHER_DEVICE_ID = "123456FFFE78ABCE";
    private static final String EXPIRE_DATE = "2017-01-29T02:50:20.55Z";
}
