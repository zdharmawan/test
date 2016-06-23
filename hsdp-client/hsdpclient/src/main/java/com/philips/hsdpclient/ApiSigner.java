/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.google.common.base.*;

public class ApiSigner {

    private static final String ALGORITHM_NAME = "HmacSHA256";
    private static final String SECRET_KEY_PREFIX = "DHPWS";

    private final String secretKey;
    private final String sharedKey;

    public ApiSigner(String sharedKey, String secretKey) {
        if (sharedKey == null || secretKey == null)
            throw new IllegalArgumentException("Missing authentication signing keys");

        this.sharedKey = sharedKey;
        this.secretKey = secretKey;
    }

    public String buildAuthorizationHeaderValue(HttpMethod requestMethod, String queryString, HttpHeaders headers, String url, String requestbody) {
        byte[] signatureKey = hashRequest(requestMethod, queryString, requestbody, joinHeaders(headers));
        String signature = signString(signatureKey, url);

        return buildAuthorizationHeaderValue(joinHeaders(headers), signature);
    }

    private String joinHeaders(HttpHeaders headers) {
        List<String> headerList = new ArrayList<>(headers.size());

        for (Map.Entry<String, String> header : headers.toSingleValueMap().entrySet())
            headerList.add(header.getKey() + ":" + header.getValue());

        return String.format("%s;", Joiner.on(";").join(headerList));
    }

    private String buildAuthorizationHeaderValue(String requestHeader, String signature) {
        StringBuilder buffer = new StringBuilder(ALGORITHM_NAME);
        buffer.append(";");
        buffer.append("Credential:");
        buffer.append(sharedKey);
        buffer.append(";");
        buffer.append("SignedHeaders:");

        if (!Strings.isNullOrEmpty(requestHeader)) {
            String[] requestHeadersSplitted = requestHeader.split(";");
            for (int i = 0; i < requestHeadersSplitted.length; i++) {
                String headerName = requestHeadersSplitted[i].split(":")[0];
                buffer.append(headerName);
                if (i != (requestHeadersSplitted.length - 1))
                    buffer.append(",");
            }
        }

        buffer.append(";");
        buffer.append("Signature:");
        buffer.append(signature);

        return buffer.toString();
    }

    private byte[] hashRequest(HttpMethod requestMethod, String queryString, String requestBody, String requestHeaders) {
        byte[] kSecret = (SECRET_KEY_PREFIX + secretKey).getBytes(Charsets.UTF_8);
        final byte[] kMethod = hash(requestMethod.toString(), kSecret);
        final byte[] kQueryString = hash(queryString, kMethod);
        final byte[] kBody = hash(requestBody, kQueryString);
        return hash(requestHeaders, kBody);
    }

    private String signString(byte[] signatureKey, String uriToBeSigned) {
        byte[] signatureArray = hash(uriToBeSigned, signatureKey);
        return DatatypeConverter.printBase64Binary(signatureArray);
    }

    private byte[] hash(String data, byte[] key) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM_NAME);
            mac.init(new SecretKeySpec(key, ALGORITHM_NAME));
            return mac.doFinal(data.getBytes(Charsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
