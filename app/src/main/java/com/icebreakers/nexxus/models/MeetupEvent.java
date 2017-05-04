
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
        codePathEvent.name = "Netflix CodePath Demo Day";
        codePathEvent.time = new Date().getTime();

        codePathEvent.venue = new Venue();
        codePathEvent.venue.setName("Netflix HQ");
        codePathEvent.venue.setAddress1("121 Albright way");
        codePathEvent.venue.setCity("Los Gatos");
        codePathEvent.venue.setLat(37.257103);
        codePathEvent.venue.setLon(-121.964178);

        codePathEvent.description = "Over the past couple months, CodePath and Netflix have partnered  to provide an intense Android bootcamp for practicing software engineers to develop knowlege and skill in industry standard Android development.\n" +
                "\n" +
                "Less than 3% of applicants were accepted into the bootcamp and the course required participants to spend 20+ hours a week on top of their full time jobs for 8 weeks. Netflix supported the course by providing design and engineering mentorship as well as hosting the class at their headquarters in Los Gatos. In the later half of the course, students formed teams and worked together to build polished Android applications.\n" +
                "\n" +
                "On May 8th, 2017 participants will showcase their final projects in front of an all engineering audience of CodePath alumni, Netflix engineers, and influential technology leaders judging the winning apps.\n" +
                "\n" +
                "Complimentary food and alcohol will be served. Netflix will be providing swag and prizes to attendees and demo day winners.  ";

        codePathEvent.imageUrl = "http://thetvpage.com/wp-content/uploads/2016/01/netflix-logo.jpg";
        //"https://i2.wp.com/blog.codepath.com/wp-content/uploads/2017/01/Netflix-Image-2.jpg";
                //"http://cdn.bgr.com/2016/02/netflix-sign-2.jpg?quality=98&strip=all"; //"https://i.imgur.com/XgxWfyF.png";

        return codePathEvent;
    }
}
