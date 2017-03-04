package com.pautena.hackupc.services.callback;

import com.pautena.hackupc.entities.VideoRoom;

/**
 * Created by pautenavidal on 4/3/17.
 */

public interface CreateRoomCallback {

    void onCreateRoomFinish(VideoRoom videoRoom);
}
