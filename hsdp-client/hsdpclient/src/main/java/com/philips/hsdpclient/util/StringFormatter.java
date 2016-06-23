/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import com.philips.hsdpclient.datamodel.DCRelation;
import com.philips.hsdpclient.datamodel.SubjectProfile;
import com.philips.hsdpclient.request.Photo;
import com.philips.hsdpclient.request.Profile;
import com.philips.hsdpclient.response.DCPairingResponse;
import com.philips.hsdpclient.response.Response;

public interface StringFormatter {

    String format(Profile userProfile);

    String format(SubjectProfile subjectProfile);

    String format(Response response);

    String format(Photo photo);

    String format(DCRelation dcRelation);

    String format(DCPairingResponse dcPairingResponse);
}
