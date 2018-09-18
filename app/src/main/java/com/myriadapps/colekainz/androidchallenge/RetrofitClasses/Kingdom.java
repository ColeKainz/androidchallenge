package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Kingdom extends RetrofitObject{

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image")
    @Expose
    private String imageURL;

    @SerializedName("climate")
    @Expose
    private String climate;

    @SerializedName("population")
    @Expose
    private int population;

    @SerializedName("quests")
    @Expose
    private List<Quest> quests;

    public Kingdom(int id, String name, String imageURL, String climate, int population, List<Quest> quests) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.climate = climate;
        this.population = population;
        this.quests = quests;
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

    public String getClimate() {
        return climate;
    }

    public int getPopulation() {
        return population;
    }

    public List<Quest> getQuests() {
        return quests;
    }
}
