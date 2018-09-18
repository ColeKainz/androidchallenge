package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Giver extends RetrofitObject{

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("bio")
    @Expose
    private String description;

    @SerializedName("profession")
    @Expose
    private String profession;

    @SerializedName("image")
    @Expose
    private String imageURL;

    public Giver(int id, String name, String description, String profession, String imageURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profession = profession;
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getDescription() {
        return description;
    }

    public String getProfession() {
        return profession;
    }
}
