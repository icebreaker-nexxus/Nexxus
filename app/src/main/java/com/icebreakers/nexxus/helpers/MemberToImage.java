package com.icebreakers.nexxus.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amodi on 5/6/17.
 */

public class MemberToImage {

    private static Map<String, String> backgroundImageMap = new HashMap<>();

    static {
        backgroundImageMap.put( "4qHi9-qdlA", "http://imgur.com/download/OXp1CdW"); // Aditya
        backgroundImageMap.put( "hj8uHrci7d", "http://imgur.com/download/jtnR651"); // Nidhi
        backgroundImageMap.put( "PtewWOGlGt", "http://imgur.com/download/4Yo17qe"); // Jason
        backgroundImageMap.put( "-GKTP4lCqZ", "http://imgur.com/download/1pmnyec"); // Radhika
        backgroundImageMap.put( "zZmtV3wJO1", "http://imgur.com/download/desFVhY"); // Drew

    }

    public static final String getImageUrl(String memberId) {
        if (!backgroundImageMap.containsKey(memberId)) {
            return "http://imgur.com/download/OXp1CdW";
        } else {
            return backgroundImageMap.get(memberId);
        }
    }
}
