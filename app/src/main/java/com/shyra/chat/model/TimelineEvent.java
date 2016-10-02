package com.shyra.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Bean class for Timeline Events
 * Created by Rachit Goyal for ShyRa on 10/2/16.
 */

public class TimelineEvent implements Parcelable {

    private long id;
    private String title;
    private String description;
    private String imageUrl;
    private String date;
    private int color;

    public TimelineEvent() {
    }

    public TimelineEvent(String title, String description, String imageUrl, String date, int color) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.date = date;
        this.color = color;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    protected TimelineEvent(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        date = in.readString();
        color = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(date);
        dest.writeInt(color);
    }

    public static final Creator<TimelineEvent> CREATOR = new Creator<TimelineEvent>() {
        @Override
        public TimelineEvent createFromParcel(Parcel in) {
            return new TimelineEvent(in);
        }

        @Override
        public TimelineEvent[] newArray(int size) {
            return new TimelineEvent[size];
        }
    };
}
