/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import com.philips.hsdpclient.datamodel.DCRelation;

public class DCPairingRequestEntity {

    public DCRelationJsonObject relation;

    public DCPairingRequestEntity(DCRelation dcRelation) {

        relation = new DCRelationJsonObject();
        relation.resourceType = dcRelation.resourceType;

        relation.trustor = new DCRelationTrustorJsonObject();
        relation.trustor.system = dcRelation.trustor.system;
        relation.trustor.value = dcRelation.trustor.value;


        relation.trustee = new DCRelationTrusteeJsonObject();
        relation.trustee.system = dcRelation.trustee.system;
        relation.trustee.value = dcRelation.trustee.value;

        relation.expireDate = dcRelation.expireDate != null ? dcRelation.expireDate.toString(ISO_DATE_TIME_FORMAT) : "";

        relation.type = new DCRelationTypeJsonObject();
        relation.type.system = dcRelation.type.system;
        relation.type.value = dcRelation.type.value;

        relation.permissions = dcRelation.permissions;
        relation.metadata = dcRelation.metadata;

    }

    class DCRelationJsonObject {
        public String resourceType;
        public DCRelationTrustorJsonObject trustor;
        public DCRelationTrusteeJsonObject trustee;
        public String expireDate;
        public DCRelationTypeJsonObject type;
        public String[] permissions;
        public String metadata;
    }

    class DCRelationTrustorJsonObject {
        public String system;
        public String value;
    }

    class DCRelationTrusteeJsonObject {
        public String system;
        public String value;
    }

    class DCRelationTypeJsonObject {
        public String system;
        public String value;
    }


    public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'";

}
