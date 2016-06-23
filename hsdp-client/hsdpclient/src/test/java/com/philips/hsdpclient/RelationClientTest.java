/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.exception.ErrorGettingRelations;
import com.philips.hsdpclient.exception.NotSubscribedToApplicationOrProposition;
import com.philips.hsdpclient.util.ServerSpy;

public class RelationClientTest extends BaseClientTest {

    private final String RELATION1 = "relation1";
    private final String RELATION2 = "relation2";
    private final String NO_RELATIONS_FOUND_JSON = "{\"responseCode\": \"3735\", \"responseMessage\":\"Error.\"}";
    private final String relationResponse = "{\"exchange\": { \"relations\": [ { \"relationId\": \"\", \"trustorUUID\": \"" + RELATION1 + "\", \"trusteeUUID\": \"" + USER_ID
                                            + "\", \"relationshipType\": \"Primary\", \"permissions\": [ ], \"metadata\": []}, { \"relationId\": \"\", \"trustorUUID\": \""
                                            + RELATION2 + "\", \"trusteeUUID\": \"" + USER_ID
                                            + "\", \"relationshipType\": \"Primary\", \"permissions\": [ ], \"metadata\": []} ] }, \"responseCode\": \"200\", \"responseMessage\": \"Success\" }";

    private RelationClient relationClient;
    private List<String> returnedRelations;

    @Before
    public void before() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        relationClient = new RelationClient(clientConfiguration);
        relationClient.setRestTemplate(serverSpy);
    }

    @Test
    public void getAllUserRelations() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/identity-relation/Relation?entityUUID=" + USER_ID, relationResponse);
        whenGettingAllUserRelations(USER_ID);
        thenTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.GET);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
        andHeaderIsSent("api-version", HSDP_API_VERSION);
        andHeaderIsSent("propositionName", HSDP_PROPOSITION_NAME);
        andHeaderIsSent("userUUID", USER_ID);
        thenRelationsAreReturned(RELATION1, RELATION2);
    }

    @Test(expected = ErrorGettingRelations.class)
    public void throwsException_WhenGettingRelationsFails() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/identity-relation/Relation?entityUUID=" + USER_ID, ERROR_JSON);
        whenGettingAllUserRelations(USER_ID);
    }

    @Test(expected = NotSubscribedToApplicationOrProposition.class)
    public void throwsException_WhenNotSubscribedToApplicationOrProposition() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/identity-relation/Relation?entityUUID=" + USER_ID, ERROR_NOT_SUBSCRIBED_TO_APPLICATION_OR_PROPOSITION_JSON);
        whenGettingAllUserRelations(USER_ID);
    }

    @Test
    public void returnsEmptyListWhenNoRelationsFound() {
        givenTheServerRespondsWith(BASE_URL + "/personalhealth/identity-relation/Relation?entityUUID=" + USER_ID, NO_RELATIONS_FOUND_JSON);
        whenGettingAllUserRelations(USER_ID);
        thenEmptyListIsReturned();
    }

    private void thenEmptyListIsReturned() {
        assertTrue(returnedRelations.isEmpty());
    }

    private void thenRelationsAreReturned(String... relations) {
        assertEquals(relations.length, returnedRelations.size());
        for (String relation : relations) {
            assertTrue(returnedRelations.contains(relation));
        }
    }

    private void whenGettingAllUserRelations(String userId) {
        returnedRelations = relationClient.getUserRelations(userId);
    }

}