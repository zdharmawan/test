/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.exception;

public class ErrorDeletingSubjectProfile extends RuntimeException {

    private static final long serialVersionUID = -4188454712079818729L;

    public ErrorDeletingSubjectProfile(String message) {
        super(message);
    }
}
