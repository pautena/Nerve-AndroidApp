package com.pautena.hackupc.ui.twillio.listeners;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.entities.User;
import com.pautena.hackupc.entities.VideoRoom;
import com.pautena.hackupc.ui.twillio.activity.VideoActivity;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.db.Document;
import io.realm.Realm;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class MeteorListener implements MeteorCallback {
    private static final String TAG = MeteorListener.class.getSimpleName();


    private final VideoActivity activity;
    private final User user;
    private final Meteor meteor;
    private final Realm realm;

    public MeteorListener(VideoActivity activity, Realm realm, User user, Meteor meteor) {
        this.activity = activity;
        this.user = user;
        this.meteor = meteor;
        this.realm = realm;
    }

    /*
    METEOR WBSOCKET FUNCTIONS
     */
    @Override
    public void onConnect(boolean signedInAutomatically) {
        Log.d(TAG, "onConnect. signedInAutomatically: " + signedInAutomatically);
    }

    @Override
    public void onDisconnect() {
        Log.d(TAG, "onDisconnect");

    }

    @Override
    public void onException(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
        Log.d(TAG, "onDataAdded. collectionName: " + collectionName);

    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updated, String removed) {
        Log.d(TAG, "onDataChanged. collectionName: " + collectionName);


        try {
            jsonObjectChange(collectionName, documentID, updated, removed);
        } catch (IllegalStateException e) {
            jsonArrayChange(collectionName, documentID, updated, removed);
        }
    }

    public void jsonObjectChange(String collectionName, String documentID, String updated, String removed) {
        if (collectionName.equals("rooms")) {
            Log.d(TAG, "removed: " + removed);
            Log.d(TAG, "updated: " + updated);


            JsonParser parser = new JsonParser();
            JsonObject updatedJson = null;
            JsonObject removedJson = null;

            if (updated != null) {
                updatedJson = parser.parse(updated).getAsJsonObject();
            }

            if (removed != null) {
                removedJson = parser.parse(removed).getAsJsonObject();
            }

            if (updatedJson.has("singer2")) {
                String sender2 = updatedJson.get("singer2").getAsString();
                if (sender2.equals(user.getId())) {
                    Document document = meteor.getDatabase().getCollection(collectionName).getDocument(documentID);

                    Log.d(TAG, "document: " + document);

                    String songId = (String) document.getField("songId");
                    String singer1Id = (String) document.getField("singer1");
                    String singer2Id = (String) document.getField("singer2");

                    Song song = realm.where(Song.class).equalTo("id", songId).findFirst();

                    realm.beginTransaction();
                    VideoRoom room = realm.createObject(VideoRoom.class, documentID);
                    room.setSong(song);
                    room.setSinger1Id(singer1Id);
                    room.setSinger2Id(singer2Id);
                    realm.commitTransaction();

                    activity.onRequestReceived(room);
                }
            } else if (updatedJson.has("start") && updatedJson.get("start").getAsBoolean()) {
                activity.playSong();
            }
        }
    }

    public void jsonArrayChange(String collectionName, String documentID, String updated, String removed) {
        if (collectionName.equals("rooms")) {
            Log.d(TAG, "removed: " + removed);
            Log.d(TAG, "updated: " + updated);


            JsonParser parser = new JsonParser();
            JsonArray updatedJson = null;
            JsonArray removedJson = null;

            if (updated != null) {
                updatedJson = parser.parse(updated).getAsJsonArray();
            }

            if (removed != null) {
                removedJson = parser.parse(removed).getAsJsonArray();
            }


            Document document = meteor.getDatabase().getCollection(collectionName).getDocument(documentID);

            Log.d(TAG, "document: " + document);
        }
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        Log.d(TAG, "onDataRemoved. collectionName: " + collectionName);
    }
}
