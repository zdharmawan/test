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

import com.philips.hsdpclient.exception.ErrorGettingRelations;
import com.philips.hsdpclient.exception.NotSubscribedToApplicationOrProposition;
import com.philips.hsdpclient.response.Response;
import com.philips.hsdpclient.util.MapUtils;

public class RelationClient extends ApiClient {
    private static final Logger LOGGER = Logger.getLogger(RelationClient.class);
    private static final String NO_RELATIONS_FOUND = "3735";
    private static final String NOT_SUBSCRIBED_TO_APP_AND_PROP = "3756";
    private static final String RELATION_NOT_FOUND = "3744";

    public RelationClient(ApiClientConfiguration clientConfiguration) {
        super(clientConfiguration);
    }

    public List<String> getUserRelations(String userId) {
        String endpoint = "/personalhealth/identity-relation/Relation";
        HttpHeaders headers = new HttpHeaders();
        sign(headers, endpoint, "entityUUID=" + userId, HttpMethod.GET, null);
        headers.add("applicationName", applicationName);
        headers.add("propositionName", propositionName);
        headers.add("api-version", apiVersion);
        headers.add("userUUID", userId);
        Response response = sendRequest(HttpMethod.GET, endpoint, "entityUUID=" + userId, headers, null);
        if (!response.code.equals(RESPONSE_OK) && !response.code.equals(NO_RELATIONS_FOUND)) {
            LOGGER.error(createLogMessageBuilder("Error getting user relations").appendUserId(userId).append(response));
            if (response.code.equals(NOT_SUBSCRIBED_TO_APP_AND_PROP)) {
                throw new NotSubscribedToApplicationOrProposition("Not subscribed to application or proposition");
            } else if (response.code.equals(RELATION_NOT_FOUND)) {
                return new ArrayList<>();
            } else {
                throw new ErrorGettingRelations("Error getting user relations");
            }
        }
        return extractRelationsFrom(response);
    }

    private List<String> extractRelationsFrom(Response response) {
        List<String> relations = new ArrayList<>();
        List<Map<String, Object>> relationList = MapUtils.extract(response.rawBody, "exchange.relations");
        if (relationList == null)
            return relations;
        for (Map<String, Object> relation : relationList) {
            relations.add(MapUtils.extract(relation, "trustorUUID"));
        }
        return relations;
    }

}
