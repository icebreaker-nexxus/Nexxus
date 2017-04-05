package com.icebreakers.nexxus.network;

import android.content.Context;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.listeners.ApiListener;

/**
 * Created by amodi on 4/4/17.
 */

public class LinkedInClient {

    Context applicationContext;
    public LinkedInClient(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    private static final String BASIC_PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,num-connections,picture-url,headline,summary,public-profile-url,email-address)?format=json";

    public void fetchBasicProfileInformation(ApiListener apiListener) {
        APIHelper apiHelper = APIHelper.getInstance(applicationContext);
        apiHelper.getRequest(applicationContext, BASIC_PROFILE_URL, apiListener);
    }
}
