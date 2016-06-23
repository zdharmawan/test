/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.request;

import java.util.List;

import com.google.common.base.MoreObjects;
import com.philips.hsdpclient.Formattable;
import com.philips.hsdpclient.util.StringFormatter;

public class Profile implements Formattable {
    public String givenName;
    public String middleName;
    public String familyName;
    public String birthday;
    public String currentLocation;
    public String displayName;
    public String locale;
    public String gender;
    public String timeZone;
    public String preferredLanguage;
    public Double height;
    public Double weight;
    public Address primaryAddress;
    public List<Photo> photos;

    public Profile(String givenName, String middleName, String familyName, String birthday, String currentLocation, String displayName,
            String locale, String gender, String timeZone, String preferredLanguage, Double height, Double weight, Address primaryAddress, List<Photo> photos) {
        this.givenName = givenName;
        this.middleName = middleName;
        this.familyName = familyName;
        this.birthday = birthday;
        this.currentLocation = currentLocation;
        this.displayName = displayName;
        this.locale = locale;
        this.gender = gender;
        this.timeZone = timeZone;
        this.preferredLanguage = preferredLanguage;
        this.height = height;
        this.weight = weight;
        this.primaryAddress = primaryAddress;
        this.photos = photos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Profile profile = (Profile) o;

        if (givenName != null ? !givenName.equals(profile.givenName) : profile.givenName != null)
            return false;
        if (middleName != null ? !middleName.equals(profile.middleName) : profile.middleName != null)
            return false;
        if (familyName != null ? !familyName.equals(profile.familyName) : profile.familyName != null)
            return false;
        if (birthday != null ? !birthday.equals(profile.birthday) : profile.birthday != null)
            return false;
        if (currentLocation != null ? !currentLocation.equals(profile.currentLocation) : profile.currentLocation != null)
            return false;
        if (displayName != null ? !displayName.equals(profile.displayName) : profile.displayName != null)
            return false;
        if (locale != null ? !locale.equals(profile.locale) : profile.locale != null)
            return false;
        if (gender != null ? !gender.equals(profile.gender) : profile.gender != null)
            return false;
        if (timeZone != null ? !timeZone.equals(profile.timeZone) : profile.timeZone != null)
            return false;
        if (preferredLanguage != null ? !preferredLanguage.equals(profile.preferredLanguage) : profile.preferredLanguage != null)
            return false;
        if (height != null ? !height.equals(profile.height) : profile.height != null)
            return false;
        if (weight != null ? !weight.equals(profile.weight) : profile.weight != null)
            return false;
        if (primaryAddress != null ? !primaryAddress.equals(profile.primaryAddress) : profile.primaryAddress != null)
            return false;
        return !(photos != null ? !photos.equals(profile.photos) : profile.photos != null);

    }

    @Override
    public int hashCode() {
        int result = givenName != null ? givenName.hashCode() : 0;
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (currentLocation != null ? currentLocation.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (timeZone != null ? timeZone.hashCode() : 0);
        result = 31 * result + (preferredLanguage != null ? preferredLanguage.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (primaryAddress != null ? primaryAddress.hashCode() : 0);
        result = 31 * result + (photos != null ? photos.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(StringFormatter stringFormatter) {
        return null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("givenName", givenName)
                .add("middleName", middleName)
                .add("familyName", familyName)
                .add("birthday", birthday)
                .add("currentLocation", currentLocation)
                .add("displayName", displayName)
                .add("locale", locale)
                .add("gender", gender)
                .add("timeZone", timeZone)
                .add("preferredLanguage", preferredLanguage)
                .add("height", height)
                .add("weight", weight)
                .add("primaryAddress", primaryAddress)
                .add("photos", photos)
                .toString();
    }
}
