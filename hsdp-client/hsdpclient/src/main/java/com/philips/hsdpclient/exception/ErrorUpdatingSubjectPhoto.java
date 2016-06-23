/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.exception;

public class ErrorUpdatingSubjectPhoto extends RuntimeException {
    private static final long serialVersionUID = -6168847028467620328L;

    public ErrorUpdatingSubjectPhoto(String message) {
        super(message);
    }
}
