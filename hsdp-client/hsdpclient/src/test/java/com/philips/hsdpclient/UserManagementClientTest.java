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
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.junit.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.philips.hsdpclient.exception.*;
import com.philips.hsdpclient.request.*;
import com.philips.hsdpclient.response.Response;
import com.philips.hsdpclient.response.UserRegistrationResponse;
import com.philips.hsdpclient.util.MapUtils;
import com.philips.hsdpclient.util.ServerSpy;

public class UserManagementClientTest extends BaseClientTest {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String ACCESS_TOKEN = "3qc9m9s99qgey385";

    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(REST_TEMPLATE);

    private static final String API_BASE_URL = "http://example.org/user-management";
    private static final String APPLICATION_NAME = "APPLICATION";

    private UserManagementClient userManagementClientOld = new UserManagementClient(new ApiClientConfiguration(API_BASE_URL, APPLICATION_NAME, "uGrowProp", "key", "secret", ""));
    private UserManagementClient userManagementClient;
    private UserProfile retrievedUserProfile;

    @Before
    public void setUp() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        userManagementClient = new UserManagementClient(new ApiClientConfiguration(BASE_URL, APPLICATION_NAME, "uGrowProp", "key", "secret", ""));
        userManagementClientOld.setRestTemplate(REST_TEMPLATE);
        userManagementClient.setRestTemplate(serverSpy);
        DateTimeUtils.setCurrentMillisFixed(DateTime.parse("2015-07-30T09:30:10.119+0000", DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).getMillis());
    }

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void getsUserProfile() {
        givenTheServerRespondsWith(BASE_URL + "/usermanagement/users/" + USER_ID + "/profile?applicationName=" + APPLICATION_NAME, USER_PROFILE_RESPONSE_JSON);
        whenGettingUserProfile(USER_ID);
        thenUserProfileIsReturned(testProfile);
        thenTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.GET);
    }

    @Test
    public void getsUserProfile_AddressSetToNullWhenGetEmptyString() {
        givenTheServerRespondsWith(BASE_URL + "/usermanagement/users/" + USER_ID + "/profile?applicationName=" + APPLICATION_NAME,
                createUserProfileResponseFor(profileWithEmptyCountryCode));
        whenGettingUserProfile(USER_ID);
        thenUserProfileIsReturned(profileWithNullCountryCode);
        thenTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.GET);
    }

    @Test(expected = ErrorGettingUserProfile.class)
    public void throwsExceptionWhenGettingUserProfileFails() {
        givenTheServerRespondsWith(BASE_URL + "/usermanagement/users/" + USER_ID + "/profile?applicationName=" + APPLICATION_NAME, ERROR_JSON);
        whenGettingUserProfile(USER_ID);
    }

    @Test
    public void updateProfile() {
        givenTheServerRespondsWith(BASE_URL + "/usermanagement/users/" + USER_ID + "/profile?applicationName=" + APPLICATION_NAME, USER_PROFILE_RESPONSE_JSON);
        whenUpdatingUserProfile(USER_ID, testProfile);
        thenTheSentBodyIs(createRequestJsonFor(testProfile));
        andTheUsedMethodIs(HttpMethod.PUT);
        andHeaderIsSent("accessToken", ACCESS_TOKEN);
    }

    @Test(expected = ErrorUpdatingUserProfile.class)
    public void throwsExceptionWhenUpdatingUserProfileFails() {
        givenTheServerRespondsWith(BASE_URL + "/usermanagement/users/" + USER_ID + "/profile?applicationName=" + APPLICATION_NAME, ERROR_JSON);
        whenUpdatingUserProfile(USER_ID, testProfile);
    }

    @Test(expected = TokenExpired.class)
    public void throwsTokenExpiredExceptionWhenTokenIsExpired() {
        givenTheServerRespondsWith(BASE_URL + "/usermanagement/users/" + USER_ID + "/profile?applicationName=" + APPLICATION_NAME, TOKEN_EXPIRED_JSON);
        whenUpdatingUserProfile(USER_ID, testProfile);
    }

    @Test
    public void createsAccountForUserRegistration() {
        mockServer.expect(requestTo(API_BASE_URL + "/usermanagement/users?applicationName=" + APPLICATION_NAME))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonBody("{\n" +
                                    "    \"loginId\": \"hsdprelease1.2@philips.com\",\n" +
                                    "    \"password\": \"Password\",\n" +
                                    "    \"profile\": {\n" +
                                    "        \"givenName\": \"First Name\",\n" +
                                    "        \"familyName\": \"Last Name\",\n" +
                                    "        \"birthday\": \"1989-03-18\",\n" +
                                    "        \"currentLocation\": \"NL\",\n" +
                                    "        \"locale\": \"nl-NL\",\n" +
                                    "        \"gender\": \"Male\",\n" +
                                    "        \"timeZone\": \"Europe+Amsterdam\",\n" +
                                    "        \"preferredLanguage\": \"NL\",\n" +
                                    "        \"height\": 170,\n" +
                                    "        \"weight\": 80,\n" +
                                    "        \"primaryAddress\": {\n" +
                                    "            \"country\": \"NL\"\n" +
                                    "        },\n" +
                                    "        \"photos\": [{\n" +
                                    "            \"type\": \"jpeg\",\n" +
                                    "            \"value\": \"someBase64\"\n" +
                                    "        }]\n" +
                                    "    }\n" +
                                    "}"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(SUCCESSFUL_ACCOUNT_CREATION_RESPONSE, MediaType.APPLICATION_JSON));

        UserProfile request = new UserProfile(
                "hsdprelease1.2@philips.com",
                "Password",
                new Profile(
                        "First Name",
                        null,
                        "Last Name",
                        "1989-03-18",
                        "NL",
                        null,
                        "nl-NL",
                        "Male",
                        "Europe+Amsterdam",
                        "NL",
                        170.0,
                        80.0,
                        new Address("NL"),
                        ImmutableList.of(new Photo("jpeg", "someBase64"))));

        UserRegistrationResponse registrationResponse = userManagementClientOld.registerUser(request);
        assertEquals("200", registrationResponse.code);
        assertEquals("eec2b7a6404cb44abe4b587498ae59b33f85", registrationResponse.userId);
        assertEquals("hsdprelease1.2@philips.com", MapUtils.extract(registrationResponse.rawBody, "exchange.user.loginId"));

        mockServer.verify();
    }

    @Test
    public void signsCreateAccountRequests() {
        mockServer
                .expect(requestTo(API_BASE_URL + "/usermanagement/users?applicationName=" + APPLICATION_NAME))
                .andExpect(method(HttpMethod.POST))
                .andExpect(
                        header(HttpHeaders.AUTHORIZATION,
                                "HmacSHA256;Credential:key;SignedHeaders:Country-Code,SignedDate;Signature:h24y4y7rdD46178fHwrTyIPDYMmMg04hDERcZSzylDI="))
                .andRespond(withSuccess(SUCCESSFUL_ACCOUNT_CREATION_RESPONSE, MediaType.APPLICATION_JSON));

        userManagementClientOld.registerUser(VALID_USER_REGISTRATION);

        mockServer.verify();
    }

    @Test
    public void changePassword_returnsResponse() {
        mockServer.expect(requestTo(API_BASE_URL + "/authentication/credential/changePassword?applicationName=" +
                                    APPLICATION_NAME))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("accessToken", ACCESS_TOKEN))
                .andExpect(jsonBody("{\n" +
                                    "    \"loginId\": \"hsdprelease1.2@philips.com\",\n" +
                                    "    \"currentPassword\": \"philips1\",\n" +
                                    "    \"newPassword\": \"philips\"\n" +
                                    "}"))
                .andRespond(withSuccess("{ \"responseCode\": \"200\", \"responseMessage\": \"Success\"  }", MediaType.APPLICATION_JSON));

        assertEquals(Response.SUCCESS, userManagementClientOld.changePassword("hsdprelease1.2@philips.com", "philips1", "philips", ACCESS_TOKEN));
        mockServer.verify();
    }

    @Test
    public void changeForgottenPassword_returnsResponse() {
        mockServer.expect(requestTo(API_BASE_URL + "/authentication/credential/changePasswordWithCode?applicationName=" +
                                    APPLICATION_NAME))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonBody("{\n" +
                                    "    \"code\": \"somecode\",\n" +
                                    "    \"newPassword\": \"password\",\n" +
                                    "    \"confirmPassword\": \"password\",\n" +
                                    "    \"redirectURI\": \"http://someuri.com\"" +
                                    "}"))
                .andRespond(withSuccess("{ \"responseCode\": \"200\", \"responseMessage\": \"Success\"  }", MediaType.APPLICATION_JSON));

        assertEquals(Response.SUCCESS, userManagementClientOld.changeForgottenPassword("somecode", "password", "http://someuri.com"));
        mockServer.verify();
    }

    @Test
    public void resetPassword_returnsResponse() {
        mockServer.expect(requestTo(API_BASE_URL + "/authentication/credential/recoverPassword?applicationName=" + APPLICATION_NAME))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "HmacSHA256;Credential:key;SignedHeaders:SignedDate;Signature:1ocMBAoQyBx+DeAjuiyVZX7jyuRbL8Cra3SjadqNSEs="))
                .andExpect(jsonBody("{ \"loginId\": \"hsdprelease1.2@philips.com\" }"))
                .andRespond(withSuccess("{ \"responseCode\": \"200\", \"responseMessage\": \"Success\"  }", MediaType.APPLICATION_JSON));

        assertEquals(Response.SUCCESS, userManagementClientOld.resetPassword("hsdprelease1.2@philips.com"));
        mockServer.verify();
    }

    @Test
    public void resetPassword_withRedirectUri_returnsResponse() {
        mockServer.expect(requestTo(API_BASE_URL + "/authentication/credential/recoverPassword?applicationName=" + APPLICATION_NAME))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "HmacSHA256;Credential:key;SignedHeaders:SignedDate;Signature:s074FywRi7Y7wtJ9+lz7dEeMQuHeU0N4yPeAZI1SaTI="))
                .andExpect(jsonBody("{ \"loginId\": \"testuser@philips.com\", \"redirectURI\": \"http://someredirecturi.com\" }"))
                .andRespond(withSuccess("{ \"responseCode\": \"200\", \"responseMessage\": \"Success\"  }", MediaType.APPLICATION_JSON));

        assertEquals(Response.SUCCESS, userManagementClientOld.resetPassword("testuser@philips.com", "http://someredirecturi.com"));
        mockServer.verify();
    }

    @Test
    public void resendConfirmation_returnsResponse() {
        mockServer.expect(requestTo(API_BASE_URL + "/usermanagement/users/activate?applicationName=" + APPLICATION_NAME))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "HmacSHA256;Credential:key;SignedHeaders:SignedDate;Signature:IIUZI/vJ2q2QsQRLSsPs9KZUKP5+58NV6l392Evu24c="))
                .andExpect(jsonBody("{ \"loginId\": \"testuser@philips.com\" }"))
                .andRespond(withSuccess("{ \"responseCode\": \"200\", \"responseMessage\": \"Success\"  }", MediaType.APPLICATION_JSON));

        assertEquals(Response.SUCCESS, userManagementClientOld.resendConfirmation("testuser@philips.com"));
        mockServer.verify();
    }

    private void whenGettingUserProfile(String userId) {
        retrievedUserProfile = userManagementClient.getProfile(userId, "ff");
    }

    private void whenUpdatingUserProfile(String userId, UserProfile profile) {
        userManagementClient.updateProfile(userId, profile.profile, ACCESS_TOKEN);
    }

    private void thenUserProfileIsReturned(UserProfile testProfile) {
        assertEquals(testProfile, retrievedUserProfile);
    }

    private String createRequestJsonFor(UserProfile profile) {
        return "{\"givenName\":\"" + profile.profile.givenName + "\",\"middleName\":\"" + profile.profile.middleName + "\",\"familyName\":\"" + profile.profile.familyName
               + "\",\"birthday\":\"" + profile.profile.birthday + "\",\"currentLocation\":\"" + profile.profile.currentLocation + "\",\"displayName\":\""
               + profile.profile.displayName + "\",\"locale\":\"" + profile.profile.locale + "\",\"gender\":\"" + profile.profile.gender + "\",\"timeZone\":\""
               + profile.profile.timeZone + "\",\"preferredLanguage\":\"" + profile.profile.preferredLanguage + "\",\"height\":" + profile.profile.height + ",\"weight\":"
               + profile.profile.weight + ",\"primaryAddress\":{\"country\":\"" + profile.profile.primaryAddress.country + "\"},\"photos\":[{\"type\":\""
               + profile.profile.photos.get(0).type + "\",\"value\":\"" + profile.profile.photos.get(0).value + "\"}]}";
    }

    private final static UserProfile testProfile = new UserProfile(USER_ID, "hsdprelease1.2@philips.com", null, new Profile("John", "middle", "family", "2014-08-22",
            "IN", "Sjaak", "en-US", "male", "IST", "blah", 180.0, 86.0, new Address("Nederland"), Arrays.asList(new Photo("png", "BASE64"))));
    private final static UserProfile profileWithEmptyCountryCode = new UserProfile(USER_ID, "hsdprelease1.2@philips.com", null,
            new Profile("John", "middle", "family", "2014-08-22",
                    "IN", "Sjaak", "en-US", "male", "IST", "blah", 180.0, 86.0, new Address(""), Arrays.asList(new Photo("png", "BASE64"))));
    private final static UserProfile profileWithNullCountryCode = new UserProfile(USER_ID, "hsdprelease1.2@philips.com", null, new Profile("John", "middle", "family", "2014-08-22",
            "IN", "Sjaak", "en-US", "male", "IST", "blah", 180.0, 86.0, null, Arrays.asList(new Photo("png", "BASE64"))));
    private static final UserProfile VALID_USER_REGISTRATION = new UserProfile("testuser@philips.com", "Password",
            new Profile("First Name", null, "Last Name", "1989-03-18", "NL", null, "nl-NL", "Male", "Europe+Amsterdam", "NL", 170.0, 80.0,
                    new Address("NL"), ImmutableList.of(new Photo("jpeg", "someBase64"))));

    private static final String SUCCESSFUL_ACCOUNT_CREATION_RESPONSE = "{\n" +
                                                                       "    \"exchange\": {\n" +
                                                                       "        \"user\": {\n" +
                                                                       "            \"loginId\": \"hsdprelease1.2@philips.com\",\n" +
                                                                       "            \"profile\": {\n" +
                                                                       "                \"givenName\": \"Philips\",\n" +
                                                                       "                \"middleName\": \"pic\",\n" +
                                                                       "                \"gender\": \"male\",\n" +
                                                                       "                \"birthday\": \"2014408422\",\n" +
                                                                       "                \"preferredLanguage\": \"ENGLISH\",\n" +
                                                                       "                \"receiveMarketingEmail\": \"Yes\",\n" +
                                                                       "                \"currentLocation\": \"IN\",\n" +
                                                                       "                \"displayName\": \"philips\",\n" +
                                                                       "                \"familyName\": \"philipshsdp\",\n" +
                                                                       "                \"locale\": \"en-US\",\n" +
                                                                       "                \"timeZone\": \"IST\",\n" +
                                                                       "                \"primaryAddress\": {\n" +
                                                                       "                    \"country\": \"IN\"\n" +
                                                                       "                },\n" +
                                                                       "                \"photos\": [],\n" +
                                                                       "                \"height\": 167,\n" +
                                                                       "                \"weight\": 42\n" +
                                                                       "            },\n" +
                                                                       "            \"userUUID\": \"eec2b7a6404cb44abe4b587498ae59b33f85\",\n" +
                                                                       "            \"userIsActive\": 0\n" +
                                                                       "        }\n" +
                                                                       "    },\n" +
                                                                       "    \"responseCode\": \"200\",\n" +
                                                                       "    \"responseMessage\": \"Success\"\n" +
                                                                       "}";

    private String createUserProfileResponseFor(UserProfile userProfile) {
        return "{\n" +
               "    \"exchange\": {\n" +
               "        \"user\": {\n" +
               "            \"loginId\": \"" + userProfile.loginId + "\", \n" +
               "            \"profile\": {\n" +
               "                \"givenName\": \"" + userProfile.profile.givenName + "\",\n" +
               "                \"middleName\": \"" + userProfile.profile.middleName + "\",\n" +
               "                \"gender\": \"" + userProfile.profile.gender + "\",\n" +
               "                \"birthday\": \"" + userProfile.profile.birthday + "\",\n" +
               "                \"preferredLanguage\": \"" + userProfile.profile.preferredLanguage + "\",\n" +
               "                \"receiveMarketingEmail\": \"Yes\",\n" +
               "                \"currentLocation\": \"" + userProfile.profile.currentLocation + "\",\n" +
               "                \"displayName\": \"" + userProfile.profile.displayName + "\",\n" +
               "                \"familyName\": \"" + userProfile.profile.familyName + "\",\n" +
               "                \"locale\": \"" + userProfile.profile.locale + "\",\n" +
               "                \"timeZone\": \"" + userProfile.profile.timeZone + "\",\n" +
               "                \"primaryAddress\": {\n" +
               "                \"country\": \"" + userProfile.profile.primaryAddress.country + "\"\n" +
               "                },\n" +
               "                 \"photos\": [{\n" +
               "                     \"type\":\"" + userProfile.profile.photos.get(0).type + "\",\n" +
               "                    \"value\":\"" + userProfile.profile.photos.get(0).value + "\"\n" +
               "                    }],\n" +
               "                \"height\": " + userProfile.profile.height + ",\n" +
               "                \"weight\": " + userProfile.profile.weight + "\n" +
               "            },\n" +
               "            \"userIsActive\": 1,\n" +
               "            \"userUUID\": \"" + userProfile.uuid + "\"\n" +
               "        },\n" +
               "        \"accessCredential\": {\n" +
               "            \"accessToken\": \"" + ACCESS_TOKEN + "\"\n" +
               "        }\n" +
               "    },\n" +
               "    \"responseCode\": \"200\",\n" +
               "    \"responseMessage\": \"Success\"\n" +
               "}\n";
    }

    private final String USER_PROFILE_RESPONSE_JSON = createUserProfileResponseFor(testProfile);

}