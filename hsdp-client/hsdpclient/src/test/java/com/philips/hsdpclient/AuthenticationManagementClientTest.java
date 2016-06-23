/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static com.philips.hsdpclient.RestTestUtils.jsonBody;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.philips.hsdpclient.response.AuthenticationResponse;
import com.philips.hsdpclient.response.Response;

public class AuthenticationManagementClientTest {
    private static final String API_BASE_URL = "http://example.org/api";
    private static final String APPLICATION_NAME = "APPLICATION";
    private static final String USER_ID = "hsdprelease1.2@philips.com";
    private static final String PASSWORD = "philips";

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(REST_TEMPLATE);
    private AuthenticationManagementClient authenticationManagementClient;

    @Before
    public void before() {
        authenticationManagementClient = new AuthenticationManagementClient(new ApiClientConfiguration(
                API_BASE_URL,
                APPLICATION_NAME, "uGrowProp",
                "key",
                "secret", ""));
        authenticationManagementClient.setRestTemplate(REST_TEMPLATE);
    }

    @Test
    public void checksAuthentication() {
        stubAuthenticationApi(USER_ID, PASSWORD).andRespond(withSuccess(SUCCESS_RESPONSE, MediaType.APPLICATION_JSON));

        AuthenticationResponse authenticationResponse = authenticationManagementClient.authenticate(USER_ID, PASSWORD);

        assertEquals("zzew2anpgtx4x7pr", authenticationResponse.accessToken);
        assertEquals("wm56gy4qvnqnbxupgvts", authenticationResponse.refreshToken);
        assertEquals("a3ae170a-6d29-42c2-a3ee-2b4af3adc2b8", authenticationResponse.userId);
        assertEquals((Integer) 3600, authenticationResponse.expiresIn);
    }

    @Test
    public void signsAuthenticateApiRequests() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.parse("2015-07-30T09:30:10.119+0000", DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).getMillis());
        stubAuthenticationApi(USER_ID, PASSWORD)
                .andExpect(header(HttpHeaders.AUTHORIZATION, "HmacSHA256;Credential:key;SignedHeaders:SignedDate;Signature:qJSDJquNVZAv27SGuqhn+OtbAxqWVj82kr8xCn+euYo="))
                .andRespond(withSuccess(SUCCESS_RESPONSE, MediaType.APPLICATION_JSON));

        authenticationManagementClient.authenticate(USER_ID, PASSWORD);
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test(expected = CommunicationException.class)
    public void authenticateThrowsException_onAnyNonSuccessfulResponse() {
        stubAuthenticationApi(USER_ID, PASSWORD).andRespond(withServerError());

        authenticationManagementClient.authenticate(USER_ID, PASSWORD);
    }

    @Test
    public void authenticateReturnsResponseCode_authenticationNotSucceeded() {
        stubAuthenticationApi(USER_ID, PASSWORD)
                .andRespond(withSuccess(FAIL_AUTHORIZATION_RESPONSE, MediaType.APPLICATION_JSON));

        assertEquals(new AuthenticationResponse(ImmutableMap.of("responseCode", "1006", "responseMessage", "Invalid login credentials")),
                authenticationManagementClient.authenticate(USER_ID, PASSWORD));
    }

    @Test
    public void refreshesTokens() {
        stubRefreshTokenApi("userId", "refreshToken")
                .andRespond(withSuccess(REFRESH_TOKEN_SUCCESS_RESPONSE, MediaType.APPLICATION_JSON));

        AuthenticationResponse authenticationResponse = authenticationManagementClient.refresh("userId", "refreshToken");

        assertEquals("newAccessToken", authenticationResponse.accessToken);
        assertEquals("newRefreshToken", authenticationResponse.refreshToken);
    }

    @Test(expected = CommunicationException.class)
    public void throwsException_onAnyNonSuccessfulRefreshTokenResponse() {
        stubRefreshTokenApi("userId", "refreshToken")
                .andRespond(withServerError());

        authenticationManagementClient.refresh("userId", "refreshToken");
    }

    @Test
    public void returnsResponseCode_refreshTokenNotValid() {
        stubRefreshTokenApi("userId", "invalidRefreshToken")
                .andRespond(withSuccess(REFRESH_TOKEN_NOT_VALID_RESPONSE, MediaType.APPLICATION_JSON));

        assertEquals(new AuthenticationResponse(ImmutableMap.of("responseCode", "1151", "responseMessage", "Invalid refresh token")),
                authenticationManagementClient.refresh("userId", "invalidRefreshToken"));
    }

    @Test
    public void returnsResponseCode_validateToken() {
        stubTokenStatusApi("userId").andRespond(withSuccess(ACCESS_TOKEN_VALID_RESPONSE, MediaType.APPLICATION_JSON));

        assertEquals(new Response(ImmutableMap.of("responseCode", "1152", "responseMessage", "Valid access token")),
                authenticationManagementClient.validateToken("userId", "accessToken"));

        mockServer.verify();
    }

    @Test
    public void returnsResponseCode_logout() {
        stubLogoutApi("userId", "theToken").andRespond(withSuccess(LOGOUT_SUCCESS_RESPONSE, MediaType.APPLICATION_JSON));

        assertEquals(Response.SUCCESS, authenticationManagementClient.logout("userId", "theToken"));

        mockServer.verify();
    }

    private ResponseActions stubLogoutApi(String userId, String token) {
        return mockServer.expect(requestTo(API_BASE_URL + "/authentication/users/" + userId + "/logout?applicationName=" + APPLICATION_NAME))
                .andExpect(header("accessToken", token));
    }

    private ResponseActions stubTokenStatusApi(String userId) {
        return mockServer.expect(requestTo(API_BASE_URL + "/authentication/users/" + userId + "/tokenStatus?applicationName=" + APPLICATION_NAME));
    }

    private ResponseActions stubAuthenticationApi(String username, String password) {
        return mockServer.expect(requestTo(API_BASE_URL + "/authentication/login?applicationName=" + APPLICATION_NAME))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonBody("{\n" +
                                    "  \"loginId\": \"" + username + "\",\n" +
                                    "  \"password\": \"" + password + "\"\n" +
                                    "}"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private ResponseActions stubRefreshTokenApi(String userId, String refreshToken) {
        return mockServer.expect(requestTo(API_BASE_URL + "/authentication/users/" + userId + "/refreshToken?applicationName=" + APPLICATION_NAME))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonBody("{\n" +
                                    "  \"refreshToken\": \"" + refreshToken + "\"\n" +
                                    "}"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private static final String LOGOUT_SUCCESS_RESPONSE = "{\n" +
                                                          "   \"responseCode\": \"200\",\n" +
                                                          "   \"responseMessage\": \"Success\"\n" +
                                                          "}";

    private static final String ACCESS_TOKEN_VALID_RESPONSE = "{\n" +
                                                              "   \"responseCode\": \"1152\",\n" +
                                                              "   \"responseMessage\": \"Valid access token\"\n" +
                                                              "}";

    private static final String REFRESH_TOKEN_NOT_VALID_RESPONSE = "{\n" +
                                                                   "   \"responseCode\": \"1151\",\n" +
                                                                   "   \"responseMessage\": \"Invalid refresh token\"\n" +
                                                                   "}";

    private static final String REFRESH_TOKEN_SUCCESS_RESPONSE = "{\n" +
                                                                 "    \"exchange\": {\n" +
                                                                 "        \"refreshToken\": \"newRefreshToken\",\n" +
                                                                 "        \"accessToken\": \"newAccessToken\",\n" +
                                                                 "        \"expiresIn\": \"3600\"\n" +
                                                                 "    },\n" +
                                                                 "    \"responseCode\": \"200\",\n" +
                                                                 "    \"responseMessage\": \"Success\"\n" +
                                                                 "}";

    private static final String FAIL_AUTHORIZATION_RESPONSE = "{\n" +
                                                              "   \"responseCode\": \"1006\",\n" +
                                                              "   \"responseMessage\": \"Invalid login credentials\"\n" +
                                                              "}";

    private static final String SUCCESS_RESPONSE = "{\n" +
                                                   "   \"exchange\": {\n" +
                                                   "       \"user\": {\n" +
                                                   "           \"loginId\": \"hsdprelease1.2@philips.com\",\n" +
                                                   "           \"profile\": {\n" +
                                                   "               \"givenName\": \"Philips\",\n" +
                                                   "               \"middleName\": \"pic\",\n" +
                                                   "               \"gender\": \"male\",\n" +
                                                   "               \"birthday\": \"2014-08-22\",\n" +
                                                   "               \"preferredLanguage\": \"ENGLISH\",\n" +
                                                   "               \"receiveMarketingEmail\": \"Yes\",\n" +
                                                   "               \"currentLocation\": \"india\",\n" +
                                                   "               \"displayName\": \"philips\",\n" +
                                                   "               \"familyName\": \"philipshsdp\",\n" +
                                                   "               \"locale\": \"en-US\",\n" +
                                                   "               \"timeZone\": \"10:50 GMT\",\n" +
                                                   "               \"primaryAddress\": {\n" +
                                                   "                   \"country\": \"india\"\n" +
                                                   "               },\n" +
                                                   "               \"photos\": [],\n" +
                                                   "               \"height\": 167,\n" +
                                                   "               \"weight\": 42\n" +
                                                   "           },\n" +
                                                   "           \"userUUID\": \"a3ae170a-6d29-42c2-a3ee-2b4af3adc2b8\",\n" +
                                                   "           \"userIsActive\": 1\n" +
                                                   "       },\n" +
                                                   "       \"accessCredential\": {\n" +
                                                   "           \"refreshToken\": \"wm56gy4qvnqnbxupgvts\",\n" +
                                                   "           \"accessToken\": \"zzew2anpgtx4x7pr\",\n" +
                                                   "           \"expiresIn\": \"3600\"\n" +
                                                   "       }\n" +
                                                   "   },\n" +
                                                   "   \"responseCode\": \"200\",\n" +
                                                   "   \"responseMessage\": \"Success\"\n" +
                                                   "}";

}