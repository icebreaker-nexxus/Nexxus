package com.icebreakers.nexxus.helpers;

import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Similarities;

import java.util.ArrayList;
import java.util.Collections;
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
        Set<Profile.Position> mainProfilePositionSet = mainProfile.educationList != null ? new HashSet<>(mainProfile.positionList) : Collections.emptySet();
        Set<Profile.Education> mainProfileEducationSet = mainProfile.positionList != null ? new HashSet<>(mainProfile.educationList) : Collections.emptySet();

        for (Profile profileToCompare : profileList) {
            if (profileToCompare.id.equals(mainProfile.id)) {
                // do not compare for self view
                continue;
            }
            Set<Profile.Education> educationList = new HashSet<>();
            Set<Profile.Position> positionList = new HashSet<>();

            if (mainProfileEducationSet.size() >= 1) {
                // compare only if there is something to compare against
                if (profileToCompare.educationList != null) {
                    for (Profile.Education education : profileToCompare.educationList) {
                        if (mainProfileEducationSet.contains(education)) {
                            educationList.add(education);
                        }
                    }
                }
            }

            if (mainProfilePositionSet.size() >= 1) {
                if (profileToCompare.positionList != null) {
                    for (Profile.Position position : profileToCompare.positionList) {
                        if (mainProfilePositionSet.contains(position)) {
                            positionList.add(position);
                        }
                    }
                }
            }

            Similarities similarities = new Similarities(new ArrayList<>(positionList), new ArrayList<>(educationList));
            similaritiesMap.put(profileToCompare.id, similarities);
        }

        return similaritiesMap;

    }

    public static final Similarities findSimilarities(Profile mainProfile, Profile profileToCompare) {
        List<Profile> profileList = new ArrayList<>();
        profileList.add(profileToCompare);
        return findSimilarities(mainProfile, profileList).get(profileToCompare.id);
    }
}
