/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.response;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class AuthenticationResponseTest {
    @Test
    public void implementsEqualsAndHashCode() {
        EqualsVerifier
                .forClass(AuthenticationResponse.class)
                .withRedefinedSuperclass()
                .verify();
    }
}