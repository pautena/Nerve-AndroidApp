package com.pautena.hackupc.entities;

import android.content.Context;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pautenavidal on 27/7/16.
 */
public class User extends RealmObject {

    @PrimaryKey
    private String id;
    private String username;
    private String serverToken;
    private String firebaseToken;

    public User() {
    }

    public User(String id,String username, String serverToken, String firebaseToken) {
        this.id=id;
        this.username = username;
        this.serverToken = serverToken;
        this.firebaseToken = firebaseToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServerToken() {
        return serverToken;
    }

    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public boolean hasToken() {
        return serverToken != null;
    }
}
