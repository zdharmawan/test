/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.util.ServerSpy;

public class UtilityServiceClientTest extends BaseClientTest {

    private UtilityServiceClient utilityClient;

    @Before
    public void setUp() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        utilityClient = new UtilityServiceClient(clientConfiguration);
        utilityClient.setRestTemplate(serverSpy);
    }

    @Test
    public void deletesTermsAndConditionsForUser() {
        givenTheServerRespondsWith(BASE_URL + "/subscription/applications/" + HSDP_APPLICATION_NAME + "/users/" + USER_ID + "/termsAndConditions",
                "{\"exchange\":{ }, \"responseCode\":\"200\",\"responseMessage\":\"Success\" }");
        whenDeletingTermsAndConditions(USER_ID);
        thenTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.DELETE);
        andHeaderIsSent("Authorization");
        andHeaderIsSent("applicationName", HSDP_APPLICATION_NAME);
    }

    private void whenDeletingTermsAndConditions(String userId) {
        utilityClient.deleteTermsAndConditions(userId);
    }

}
