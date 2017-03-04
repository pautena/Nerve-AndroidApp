package com.pautena.hackupc.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class RequestUser extends RealmObject {

    @PrimaryKey
    private String id;
    private String email;
    private String username;

    public RequestUser(){}

    public RequestUser(String id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
