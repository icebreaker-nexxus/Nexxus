package com.icebreakers.nexxus.helpers;

import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Similarities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by amodi on 4/8/17.
 */

public class SimilaritiesFinder {

    /**
     * Given mainProfile and a list of profiles to compare against, it finds similaries between the mainProfile and
     * each of the elements in the profileList
     */
    public static Map<String, Similarities> findSimilarities(Profile mainProfile, List<Profile> profileList) {
        Map<String, Similarities> similaritiesMap = new HashMap<>();
        Set<Profile.Position> mainProfilePositionSet = new HashSet<>(mainProfile.positionList);
        Set<Profile.Education> mainProfileEducationSet = new HashSet<>(mainProfile.educationList);

        for (Profile profileToCompare : profileList) {
            List<Profile.Education> educationList = new ArrayList<>();
            List<Profile.Position> positionList = new ArrayList<>();

            if (profileToCompare.educationList != null) {
                for (Profile.Education education : profileToCompare.educationList) {
                    if (mainProfileEducationSet.contains(education)) {
                        educationList.add(education);
                    }
                }
            }

            if (profileToCompare.positionList != null) {
                for (Profile.Position position : profileToCompare.positionList) {
                    if (mainProfilePositionSet.contains(position)) {
                        positionList.add(position);
                    }
                }
            }

            Similarities similarities = new Similarities(positionList, educationList);
            similaritiesMap.put(profileToCompare.id, similarities);
        }

        return similaritiesMap;

    }
}
