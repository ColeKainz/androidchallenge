package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quest extends RetrofitObject{
    @SerializedName("id")
    @Expose
    int id;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("image")
    @Expose
    String imageURL;

    @SerializedName("giver")
    @Expose
    Giver giver;

    public Quest(int id, String name, String description, String imageURL, Giver giver) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.giver = giver;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Giver getGiver() {
        return giver;
    }
}
