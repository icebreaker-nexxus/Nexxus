package com.icebreakers.nexxus.models.messaging;

import com.icebreakers.nexxus.models.Profile;
import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by amodi on 4/13/17.
 */

public class User implements IUser {

    String id;
    String name;
    String avatar;

    private User() {
    }

    public User(Profile profile) {
        this.id = profile.id;
        this.name = profile.firstName + " " + profile.lastName;
        this.avatar = profile.pictureUrl;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}
