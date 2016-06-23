/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import static com.philips.hsdpclient.logging.LogMessageBuilder.createLogMessageBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import com.philips.hsdpclient.datamodel.DCRelation;
import com.philips.hsdpclient.exception.*;
import com.philips.hsdpclient.request.DCPairingRequestEntity;
import com.philips.hsdpclient.response.DCPairingResponse;
import com.philips.hsdpclient.util.MapUtils;

public class DCPairingClient extends ApiClientBasicAuthentication {

    private static final Logger LOGGER = Logger.getLogger(DCPairingClient.class);
    public static final String TRUSTOR_URN_CPHUSER = "trustor=urn:cphuser%7C";
    public static final String TRUSTEE_URN_CPHUSER = "&trustee=%7C";

    public DCPairingClient(ApiClientConfigurationBasicAuthentication apiClientConfiguration) {
        super(apiClientConfiguration);
    }

    public String upsertDCPairing(String userId, DCRelation relation) {
        logInfo("Creating or Updating a DC Relation ", userId);
        String endpoint = "/relation";
        DCPairingRequestEntity requestEntity = new DCPairingRequestEntity(relation);
        HttpHeaders headers = new HttpHeaders();
        headers.add("api-version", apiVersion);
        DCPairingResponse response = sendRequest(HttpMethod.PUT, endpoint, "", headers, requestEntity.relation);
        String relationId;
        if (response.code.equals(RESPONSE_CREATED_OK)) {
            relationId = getRelationIdFromHeader(response);
            logInfo("Created new DC Relation", userId, relationId, response);
        } else if (response.code.equals(RESPONSE_OK)) {
            relationId = getRelationIdFromHeader(response);
            logInfo("Updated DC Relation", userId, relationId, response);
        } else {
            throw new ErrorUpsertingDCParing(response.message);
        }
        return relationId;
    }

    public void deleteDCPairingByTrustorAndTrustee(String trustorId, String trusteeId) {
        logInfo("Deleting DC Relation by trustor and trustee", trustorId, trusteeId);
        String endpoint = "/relation";
        HttpHeaders headers = new HttpHeaders();
        headers.add("api-version", apiVersion);
        try {
            DCPairingResponse response = sendRequest(HttpMethod.DELETE, endpoint, TRUSTOR_URN_CPHUSER + trustorId + TRUSTEE_URN_CPHUSER + trusteeId, headers, null);
            if (!response.code.equals(RESPONSE_OK)) {
                throw new ErrorDeletingDCPairing(response.message);
            }
            logInfo("Deleted DC Relation", trustorId, trusteeId, response);
        } catch (Exception e) {
            Throwable t = e.getCause();
            if (t instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) t;
                if (httpClientErrorException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    throw new DCPairingNotFound(httpClientErrorException.getMessage());
                }
            }
            throw new ErrorDeletingDCPairing(e.getMessage());
        }
    }

    public List<DCRelation> getAllDCRelations(String trustorId) {
        logInfo("Getting DC Relations by trustor", trustorId);
        String endpoint = "/relation";
        HttpHeaders headers = new HttpHeaders();
        headers.add("api-version", apiVersion);
        try {
            DCPairingResponse response = sendRequest(HttpMethod.GET, endpoint, TRUSTOR_URN_CPHUSER + trustorId, headers, null);
            List<Map<String, Object>> relationEntries = MapUtils.extract(response.rawBody, "entry");
            List<DCRelation> dcRelations = new ArrayList<>();
            if (relationEntries != null) {
                relationEntries.stream().forEach((relationEntry) -> dcRelations.add(parseRelationEntry(relationEntry)));
            }
            return dcRelations;
        } catch (CommunicationException ex) {
            throw new ErrorGettingDCParing(ex.getMessage());
        }
    }

    private DCRelation parseRelationEntry(Map<String, Object> relationEntry) {
        DCRelation dcRelation = new DCRelation();
        dcRelation.trustor = new DCRelation.Trustor();
        dcRelation.trustor.value = MapUtils.extract(relationEntry, "resource.trustor.value");
        dcRelation.trustee = new DCRelation.Trustee();
        dcRelation.trustee.value = MapUtils.extract(relationEntry, "resource.trustee.value");

        dcRelation.expireDate = DateTime.parse(MapUtils.extract(relationEntry, "resource.expireDate"));
        dcRelation.type = new DCRelation.Type();
        dcRelation.permissions = new String[] {};
        dcRelation.metadata = MapUtils.extract(relationEntry, "resource.metadata");
        return dcRelation;
    }

    private String getRelationIdFromHeader(DCPairingResponse response) {
        String locationUrl = response.headers.get("Location");
        try {
            URL url = new URL(locationUrl);
            return (new File(url.getFile()).getName());
        } catch (MalformedURLException ignored) {
        }

        return null;
    }

    private void logInfo(String message, String userId) {
        LOGGER.info(createLogMessageBuilder(message)
                .appendUserId(userId));
    }

    private void logInfo(String message, String userId, String relationID) {
        LOGGER.info(createLogMessageBuilder(message)
                .appendUserId(userId)
                .appendDCPairingId(relationID));
    }

    private void logInfo(String message, String userId, String relationID, DCPairingResponse response) {
        LOGGER.info(createLogMessageBuilder(message)
                .appendUserId(userId)
                .appendDCPairingId(relationID)
                .append(response));

    }
}
