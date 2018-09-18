package com.myriadapps.colekainz.androidchallenge.Signin;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    public Account(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    //Used when deleting saved accounts.
    public boolean equals(Account account) {
        return account.getEmail().equals(email) &&
                account.getName().equals(name);
    }
}
