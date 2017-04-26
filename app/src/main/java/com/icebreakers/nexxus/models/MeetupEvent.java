
package com.icebreakers.nexxus.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.icebreakers.nexxus.models.internal.MeetupEventRef;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class MeetupEvent {

    public static final String EVENT_ID_CODEPATH = "codepath-event";

    @SerializedName("created")
    @Expose
    private Long created;
    @SerializedName("duration")
    @Expose
    private Long duration;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("updated")
    @Expose
    private Long updated;
    @SerializedName("utc_offset")
    @Expose
    private Long utcOffset;
    @SerializedName("waitlist_count")
    @Expose
    private Long waitlistCount;
    @SerializedName("yes_rsvp_count")
    @Expose
    private Long yesRsvpCount;
    @SerializedName("venue")
    @Expose
    private Venue venue;
    @SerializedName("group")
    @Expose
    private Group group;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("visibility")
    @Expose
    private String visibility;

    public boolean fakeEvent = false;
    public String imageUrl = "";

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Long getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(Long utcOffset) {
        this.utcOffset = utcOffset;
    }

    public Long getWaitlistCount() {
        return waitlistCount;
    }

    public void setWaitlistCount(Long waitlistCount) {
        this.waitlistCount = waitlistCount;
    }

    public Long getYesRsvpCount() {
        return yesRsvpCount;
    }

    public void setYesRsvpCount(Long yesRsvpCount) {
        this.yesRsvpCount = yesRsvpCount;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return name;
    }

    public MeetupEventRef getEventRef() {
        return new MeetupEventRef(id);
    }

    public static MeetupEvent getCodePathEvent() {
        MeetupEvent codePathEvent = new MeetupEvent();
        codePathEvent.fakeEvent = true;
        codePathEvent.id = EVENT_ID_CODEPATH;
        codePathEvent.name = "Netflix + CodePath Android Bootcamp";
        codePathEvent.time = new Date().getTime();

        codePathEvent.venue = new Venue();
        codePathEvent.venue.setName("Netflix");
        codePathEvent.venue.setAddress1("121 Albright way");
        codePathEvent.venue.setCity("Los Gatos");
        codePathEvent.venue.setLat(37.257103);
        codePathEvent.venue.setLon(-121.964178);

        codePathEvent.description = "We are partnering with Netflix to provide our accelerated 8-week Android evening bootcamp starting on March 6th. The course was designed to cover all the major topics required to build Android apps according to current industry best practices incorporating the following elements:\n" +
                "Weekly 2 hour session to review key mobile concepts and topics.\n" +
                "Weekly lab session to collaborate on projects and complete coding challenges.\n" +
                "Weekly app assignments to immediately apply each new topic introduced.\n" +
                "Group project to design and develop a complete app with a team over the course.\n" +
                "At the end of the program, your group will present at Demo Day and you will join our CodePath alumni network which provides access to curated events, opportunities to network with other alums, and priority selection for future classes.\n" +
                "The in-person class will take place at the beautiful Netflix campus in Los Gatos. Throughout the course, Netflix engineers will be helping out as mentors and presenting talks about their real-world mobile use cases.";


        codePathEvent.imageUrl = "http://cdn.bgr.com/2016/02/netflix-sign-2.jpg?quality=98&strip=all"; //"https://i.imgur.com/XgxWfyF.png";

        return codePathEvent;
    }
}
