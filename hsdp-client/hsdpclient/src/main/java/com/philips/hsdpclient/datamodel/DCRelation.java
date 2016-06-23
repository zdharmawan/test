/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.datamodel;

import org.joda.time.DateTime;

import com.philips.hsdpclient.Formattable;
import com.philips.hsdpclient.util.StringFormatter;

public class DCRelation implements Formattable {

    protected static final String RESOURCE_TYPE = "relation";

    public final String resourceType = RESOURCE_TYPE;
    public Trustor trustor;
    public Trustee trustee;
    public DateTime expireDate;
    public Type type;
    public String[] permissions;
    public String metadata;

    public DCRelation() {
        this.type = new Type();
        this.permissions = new String[] {};
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        DCRelation relation = (DCRelation) obj;

        return trustor.equals(relation.trustor)
                && trustee.equals(relation.trustee)
                && expireDate.equals(relation.expireDate)
                && type.equals(relation.type)
                && metadata.equals(metadata);
    }

    @Override
    public String toString(StringFormatter stringFormatter) {
        return stringFormatter.format(this);
    }

    public static class Trustor {

        static final String SYSTEM = "urn:cphuser";

        public final String system = SYSTEM;
        public String value;

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;

            Trustor trustor = (Trustor) obj;

            return system.equals(trustor.system) && value.equals(trustor.value);
        }
    }

    public static class Trustee {
        public final String system = "";
        public String value;

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;

            Trustee trustee = (Trustee) obj;

            return system.equals(trustee.system) && value.equals(trustee.value);
        }
    }

    public static class Type {

        static String SYSTEM = "urn:ugrow";
        static String VALUE = "observation_receiver";

        public String system = SYSTEM;
        public String value = VALUE;

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;

            Type type = (Type) obj;

            return system.equals(type.system) && value.equals(type.value);
        }
    }

}
