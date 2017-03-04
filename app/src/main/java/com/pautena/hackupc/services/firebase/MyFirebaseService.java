package com.pautena.hackupc.services.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class MyFirebaseService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived: " + remoteMessage);
    }
}
