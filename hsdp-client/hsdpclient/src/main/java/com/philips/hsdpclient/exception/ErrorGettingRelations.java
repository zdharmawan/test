/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.exception;

public class ErrorGettingRelations extends RuntimeException {

    private static final long serialVersionUID = -327501822134321909L;

    public ErrorGettingRelations(String message) {
        super(message);
    }
}
