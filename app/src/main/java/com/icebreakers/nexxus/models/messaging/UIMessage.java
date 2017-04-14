package com.icebreakers.nexxus.models.messaging;

import com.icebreakers.nexxus.models.Message;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

/**
 * Created by amodi on 4/13/17.
 */

public class UIMessage implements IMessage {

    String id;
    String text;
    IUser user;
    Date date;

    public UIMessage(Message message, User user) {
        id = message.id;
        text = message.text;
        date = new Date(message.timestamp);
        this.user = user;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return date;
    }
}
