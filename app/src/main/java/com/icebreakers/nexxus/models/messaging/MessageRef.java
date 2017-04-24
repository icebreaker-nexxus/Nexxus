package com.icebreakers.nexxus.models.messaging;

import org.parceler.Parcel;

/**
 * Created by radhikak on 4/23/17.
 */

@Parcel
public class MessageRef {

    private String messageRowId;
    private String otherProfileId;

    public MessageRef(String rowId, String profileId) {
        messageRowId = rowId;
        otherProfileId = profileId;
    }

    public MessageRef() {}

    public String getMessageRowId() {
        return messageRowId;
    }

    public void setMessageRowId(String messageRowId) {
        this.messageRowId = messageRowId;
    }

    public String getOtherProfileId() {
        return otherProfileId;
    }

    public void setOtherProfileId(String otherProfileId) {
        this.otherProfileId = otherProfileId;
    }
}
