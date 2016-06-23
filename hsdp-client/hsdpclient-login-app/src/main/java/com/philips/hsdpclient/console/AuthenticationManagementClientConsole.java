/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.console;

import java.io.*;

import com.philips.hsdpclient.ApiClientConfiguration;
import com.philips.hsdpclient.AuthenticationManagementClient;
import com.philips.hsdpclient.response.AuthenticationResponse;

public class AuthenticationManagementClientConsole {

    public static void main(String[] args) {
        new AuthenticationManagementClientConsole().doMain(args);
    }

    private static final String API_BASE_URL = "https://newuser-registration-assembly15.cloud.pcftest.com";
    private static final String APPLICATION_NAME = System.getProperty("APPLICATION_NAME");
    private static final String PROPOSITION_NAME = System.getProperty("PROPOSITION_NAME");
    private static final String SIGNING_KEY = System.getProperty("SigningKey");
    private static final String SECRET_SIGNING_KEY = System.getProperty("SecretKey");
    private static final String API_VERSION = "1.0";

    public void doMain(String[] args) {

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("HSDP User Authentication Client");
            System.out.println("HSDP URL: " + API_BASE_URL);
            System.out.println("Application Name: " + APPLICATION_NAME);
            System.out.println("Proposition Name: " + PROPOSITION_NAME);
            System.out.println("API Version: " + API_VERSION);
            System.out.print("User Name: ");
            String username = null;
            try {
                username = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print("Password: ");
            String password = null;
            try {
                password = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Authentication User: " + username + "...");
            AuthenticationResponse authenticationResponse = clientAuthentication(username, password);

            System.out.println("Authentication Response:");
            System.out.println(authenticationResponse.toString());

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println();
        }

    }

    private AuthenticationResponse clientAuthentication(String username, String password) {
        ApiClientConfiguration apiClientConfiguration = new ApiClientConfiguration(API_BASE_URL, APPLICATION_NAME, PROPOSITION_NAME, SIGNING_KEY, SECRET_SIGNING_KEY,
                API_VERSION);

        AuthenticationManagementClient authenticationManagementClient = new AuthenticationManagementClient(apiClientConfiguration);

        return authenticationManagementClient.authenticate(username, password);
    }
}
