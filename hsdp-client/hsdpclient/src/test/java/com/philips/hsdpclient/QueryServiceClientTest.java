/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import com.philips.hsdpclient.datamodel.observation.Observation;
import com.philips.hsdpclient.exception.ErrorGettingObservations;
import com.philips.hsdpclient.util.*;

public class QueryServiceClientTest extends BaseClientTest {

    @Before
    public void before() {
        serverSpy = new ServerSpy();
        server = MockRestServiceServer.createServer(serverSpy);
        queryServiceClient = new QueryServiceClient(clientConfiguration);
        queryServiceClient.setRestTemplate(serverSpy);
    }

    @Test
    public void getObservations() {
        givenTheServerRespondsWith(BASE_URL + "/queryservice/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/users/" + USER_ID
                                   + "/hourly?observationList=" + OBSERVATION_IDS + "&startDate=" + START_TIME + "&subjectUUID=" + SUBJECT_ID,
                successObservationsResponse);
        whenGettingObservations(USER_ID, SUBJECT_ID, OBSERVATION_IDS, START_TIME_UTC);
        thenTheSentBodyIs(null);
        andTheUsedMethodIs(HttpMethod.GET);
        andHeaderIsSent("Authorization");
        thenObservationsAreReturned(OBSERVATION1, OBSERVATION2);
    }

    @Test
    public void getObservationsWhenNoRecords() {
        givenTheServerRespondsWith(BASE_URL + "/queryservice/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/users/" + USER_ID
                                   + "/hourly?observationList=" + OBSERVATION_IDS + "&startDate=" + START_TIME + "&subjectUUID=" + SUBJECT_ID,
                noRecordsObservationResponse);
        whenGettingObservations(USER_ID, SUBJECT_ID, OBSERVATION_IDS, START_TIME_UTC);
        thenObservationsAreReturned();
    }

    @Test(expected = ErrorGettingObservations.class)
    public void throwsExeptionWhenGettingObservationsFails() {
        givenTheServerRespondsWith(BASE_URL + "/queryservice/applications/" + HSDP_APPLICATION_NAME + "/propositions/" + HSDP_PROPOSITION_NAME + "/users/" + USER_ID
                                   + "/hourly?observationList=" + OBSERVATION_IDS + "&startDate=" + START_TIME + "&subjectUUID=" + SUBJECT_ID,
                errorObservationsResponse);
        whenGettingObservations(USER_ID, SUBJECT_ID, OBSERVATION_IDS, START_TIME_UTC);
    }

    private void thenObservationsAreReturned(Observation... observations) {
        assertArrayEquals(observations, returnedObservations.toArray());
    }

    private void whenGettingObservations(String userId, String subjectId, String observationIds, DateTime startTimeUtc) {
        returnedObservations = queryServiceClient.getObservations(userId, subjectId, observationIds, startTimeUtc);
    }

    private static final String OBSERVATION_IDS = "14,19";

    private static final String LAST_MODIFIED_1 = "2016-02-11T12:14:24.00Z";

    private static final String LAST_MODIFIED_2 = "2016-02-11T12:23:03.00Z";
    private static final String RESOURCE_ID_1 = "resourceId";
    private static final String RESOURCE_ID_2 = "resourceId2";
    private static final String TIMESTAMP_1 = "2016-01-29T10:27:00.00Z";
    private static final String TIMESTAMP_2 = "2016-01-29T10:32:00.00Z";
    private static final String START_TIME = "2016-01-29T10:00:00.00Z";
    private static final DateTime START_TIME_UTC = new DateTime(START_TIME, DateTimeZone.UTC);
    private static final String SUBJECT_ID = "SUBJECT_ID";
    private static final Observation OBSERVATION1 = createObservation();

    private static Observation createObservation() {
        ObservationBuilder builder = new ObservationBuilder("RoomTemperature");
        builder.addSource()
                .withSourceName("uGrow")
                .addValue().withValue("40.5").withTimeStamp(DateTimeUtils.toObservationDateTime(TIMESTAMP_1))
                .withSubjectId(SUBJECT_ID).withDevice(COMPASS).withLastModifed(DateTimeUtils.toObservationDateTime(LAST_MODIFIED_1))
                .withVisible("0").withResourceId(RESOURCE_ID_1);

        return builder.build();
    }

    private static Observation createAnotherObservation() {
        ObservationBuilder builder = new ObservationBuilder("RelativeHumidity");
        builder.addSource()
                .withSourceName("uGrow")
                .addValue().withValue("39.5").withTimeStamp(DateTimeUtils.toObservationDateTime(TIMESTAMP_2))
                .withSubjectId(SUBJECT_ID).withLastModifed(DateTimeUtils.toObservationDateTime(LAST_MODIFIED_2))
                .withResourceId(RESOURCE_ID_2);

        return builder.build();
    }

    private static final String COMPASS = "Compass";

    private static final Observation OBSERVATION2 = createAnotherObservation();
    private QueryServiceClient queryServiceClient;
    private List<Observation> returnedObservations;
    private String errorObservationsResponse = "{\n" +
                                               "  \"responseCode\": \"104\",\n" +
                                               "  \"responseMessage\": \"Invalid request\"\n" +
                                               "}";

    private String noRecordsObservationResponse = "{\n" +
                                                  "  \"responseCode\": \"1410\",\n" +
                                                  "  \"responseMessage\": \"No Records found for the search criteria\"\n" +
                                                  "}";

    private String successObservationsResponse = "{\n" +
                                                 "  \"exchange\": {\n" +
                                                 "    \"userUUID\": \"" + USER_ID + "\",\n" +
                                                 "    \"appName\": \"uGrowApp\",\n" +
                                                 "    \"propName\": \"uGrowProp\",\n" +
                                                 "    \"startDT\": \"" + START_TIME_UTC + "\",\n" +
                                                 "    \"returnRecords\": 5,\n" +
                                                 "    \"responseObject\": {\n" +
                                                 "      \"codeSystem\": {\n" +
                                                 "        \"name\": \"ISO/IEEE 11073-10101:2004\",\n" +
                                                 "        \"uri\": \"urn:std:iso:11073:10101\"\n" +
                                                 "      },\n" +
                                                 "      \"observations\": [\n" +
                                                 "        {\n" +
                                                 "          \"observationType\": \"RoomTemperature\",\n" +
                                                 "          \"code\": \"\",\n" +
                                                 "          \"sources\": [\n" +
                                                 "            {\n" +
                                                 "              \"sourceName\": \"uGrow\",\n" +
                                                 "              \"values\": [\n" +
                                                 "                {\n" +
                                                 "                  \"v\": \"40.5\",\n" +
                                                 "                  \"ts\": \"" + TIMESTAMP_1 + "\",\n" +
                                                 "                  \"subjectId\": \"" + SUBJECT_ID + "\",\n" +
                                                 "                  \"device\": \"" + COMPASS + "\",\n" +
                                                 "                  \"lastModTS\": \"" + LAST_MODIFIED_1 + "\",\n" +
                                                 "                  \"visible\": \"0\",\n" +
                                                 "                  \"resourceId\": \"" + RESOURCE_ID_1 + "\"\n" +
                                                 "                }\n" +
                                                 "              ]\n" +
                                                 "            }\n" +
                                                 "          ]\n" +
                                                 "        },\n" +
                                                 "        {\n" +
                                                 "          \"observationType\": \"RelativeHumidity\",\n" +
                                                 "          \"code\": \"\",\n" +
                                                 "          \"sources\": [\n" +
                                                 "            {\n" +
                                                 "              \"sourceName\": \"uGrow\",\n" +
                                                 "              \"values\": [\n" +
                                                 "                {\n" +
                                                 "                  \"v\": \"39.5\",\n" +
                                                 "                  \"ts\": \"" + TIMESTAMP_2 + "\",\n" +
                                                 "                  \"subjectId\": \"" + SUBJECT_ID + "\",\n" +
                                                 "                  \"lastModTS\": \"" + LAST_MODIFIED_2 + "\",\n" +
                                                 "                  \"resourceId\": \"" + RESOURCE_ID_2 + "\"\n" +
                                                 "                }\n" +
                                                 "              ]\n" +
                                                 "            }\n" +
                                                 "          ]\n" +
                                                 "        }\n" +
                                                 "      ]\n" +
                                                 "    }\n" +
                                                 "  },\n" +
                                                 "  \"responseCode\": \"200\",\n" +
                                                 "  \"responseMessage\": \"Success\"\n" +
                                                 "}";

}