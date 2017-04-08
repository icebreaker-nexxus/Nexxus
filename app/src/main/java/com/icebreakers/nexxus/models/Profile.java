package com.icebreakers.nexxus.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amodi on 4/6/17.
 */

@Parcel
public class Profile {

    public String id;

    public String name;

    public String firstName;

    public String lastName;

    public String headline;

    public String publicProfileUrl;

    public String pictureUrl;

    public String emailAddress;

    public List<Education> educationList;

    public List<Position> positionList;



    @Parcel
    public static class LIDate {
        int year;
    }

    @Parcel
    public static class Education {
        public String schoolName;
        public LIDate startDate;
        public LIDate endDate;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Education))
                return false;
            if (obj == this)
                return true;
            Education rhs = (Education) obj;
            return this.schoolName.equals(rhs.schoolName);
        }

        @Override
        public int hashCode() {
            return schoolName.hashCode();
        }
    }

    @Parcel
    public static class Position {
        public Long companyId;
        public String companyName;
        public boolean isCurrent;
        public String title;
        public LIDate startDate;
        public LIDate endDate;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Position))
                return false;
            if (obj == this)
                return true;
            Position rhs = (Position) obj;
            return this.companyId.equals(rhs.companyId);
        }

        @Override
        public int hashCode() {
            return companyId.hashCode();
        }
    }

    public static Profile convertFromInternalProfile(com.icebreakers.nexxus.models.internal.Profile internalProfile) {
        Profile profile = new Profile();
        profile.id = internalProfile.id;
        profile.firstName = internalProfile.firstName;
        profile.lastName = internalProfile.lastName;
        profile.name = profile.firstName + " " + profile.lastName;
        profile.headline = internalProfile.headline;
        profile.emailAddress = internalProfile.emailAddress;
        profile.pictureUrl = internalProfile.pictureUrl;
        profile.publicProfileUrl = internalProfile.publicProfileUrl;
        List<Education> educationList = new ArrayList<>();
        for (com.icebreakers.nexxus.models.internal.Profile.Education internalEducation : internalProfile.educationInfo.educationList) {
            Education education = new Education();
            education.schoolName = internalEducation.schoolName;
            education.startDate = internalEducation.startDate;
            education.endDate = internalEducation.endDate;
            educationList.add(education);
        }

        List<Position> positionList = new ArrayList<>();
        for (com.icebreakers.nexxus.models.internal.Profile.Position internalPosition : internalProfile.currentCompanyInfo.positionInfos) {
            Position position = new Position();
            position.companyId = internalPosition.company.id;
            position.companyName = internalPosition.company.name;
            position.isCurrent = internalPosition.isCurrent;
            position.startDate = internalPosition.startDate;
            position.endDate = internalPosition.endDate;
            position.title = internalPosition.title;
            positionList.add(position);
        }

        for (com.icebreakers.nexxus.models.internal.Profile.Position internalPosition : internalProfile.pastCompanyInfo.positionInfos) {
            Position position = new Position();
            position.companyId = internalPosition.company.id;
            position.companyName = internalPosition.company.name;
            position.isCurrent = internalPosition.isCurrent;
            position.startDate = internalPosition.startDate;
            position.endDate = internalPosition.endDate;
            position.title = internalPosition.title;
            positionList.add(position);
        }

        profile.educationList = educationList;
        profile.positionList = positionList;

        return profile;
    }

}
