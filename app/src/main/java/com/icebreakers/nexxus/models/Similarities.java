package com.icebreakers.nexxus.models;

import java.util.List;

/**
 * Created by amodi on 4/8/17.
 */

public class Similarities {

    public final List<Profile.Position> similarPositions;
    public final List<Profile.Education> similarEducations;
    public final int numOfSimilarities;


    public Similarities(List<Profile.Position> similarPositions,
                        List<Profile.Education> similarEducations) {
        this.similarPositions = similarPositions;
        this.similarEducations = similarEducations;
        this.numOfSimilarities = similarEducations.size() + similarPositions.size();
    }
}
