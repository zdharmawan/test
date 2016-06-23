/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import java.util.List;

public class SubscribeToStandardObservation {

    public final List<String> standardObservationNames;

    public SubscribeToStandardObservation(List<String> standardObservationNames) {
        this.standardObservationNames = standardObservationNames;
    }
}
