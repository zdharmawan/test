/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import com.google.common.base.Strings;

public abstract class Environment {

    private Environment() {
    }

    public static String get(String key) {
        return get(key, null);
    }

    private static String get(String key, String defaultValue) {
        String value = System.getenv(key);
        if (Strings.isNullOrEmpty(value))
            value = System.getProperty(key);
        if (Strings.isNullOrEmpty(value))
            value = defaultValue;
        return value;
    }
}
