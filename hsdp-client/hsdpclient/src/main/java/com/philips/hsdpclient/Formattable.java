/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient;

import com.philips.hsdpclient.util.StringFormatter;

public interface Formattable {

    String toString(StringFormatter stringFormatter);
}
