/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.exception;

public class ErrorLastUpdatedConflict extends RuntimeException {

    private static final long serialVersionUID = 5478075999586726656L;

    public ErrorLastUpdatedConflict(String message) {
        super(message);
    }
}
