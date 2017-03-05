package com.pautena.hackupc.ui.twillio.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.RequestUser;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.entities.User;
import com.pautena.hackupc.entities.VideoRoom;
import com.pautena.hackupc.entities.manager.UserManager;
import com.pautena.hackupc.services.ApiServiceAdapter;
import com.pautena.hackupc.services.callback.CreateRoomCallback;
import com.pautena.hackupc.services.callback.RequestJoinCallback;
import com.pautena.hackupc.ui.customview.CameraPreview;
import com.pautena.hackupc.ui.twillio.customviews.MyPrimaryVideoView;
import com.pautena.hackupc.ui.twillio.customviews.MyThumbnailVideoView;
import com.pautena.hackupc.ui.twillio.fragments.PlayingFragment;
import com.pautena.hackupc.ui.twillio.fragments.RequestFragment;
import com.pautena.hackupc.ui.twillio.fragments.SelectFriendFragment;
import com.pautena.hackupc.ui.twillio.fragments.SongSelectionFragment;
import com.pautena.hackupc.ui.twillio.fragments.StartOrJoinFragment;
import com.pautena.hackupc.ui.twillio.fragments.WaitForStartFragment;
import com.pautena.hackupc.ui.twillio.listeners.MeteorListener;
import com.pautena.hackupc.ui.twillio.utils.ViewCapturer;
import com.pautena.hackupc.utils.SongPlayer;
import com.plattysoft.leonids.ParticleSystem;
import com.twilio.video.AudioTrack;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalMedia;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.Media;
import com.twilio.video.Participant;
import com.twilio.video.Room;
import com.twilio.video.RoomState;
import com.twilio.video.TwilioException;
import com.twilio.video.VideoClient;
import com.twilio.video.VideoRenderer;
import com.twilio.video.VideoTrack;

import java.util.HashMap;
import java.util.Map;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import io.realm.Realm;

public class VideoActivity extends AppCompatActivity implements SongSelectionFragment.SongSelectionCallback,
        SelectFriendFragment.SelectFriendFragmentCallback, StartOrJoinFragment.StartOrJoinFragmentCallback,
        WaitForStartFragment.WaitForStartCallback, PlayingFragment.PlayingFragmentCallback,
        RequestFragment.RequestFragmentCallback, SongPlayer.SongPlayerListener {
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "VideoActivity";

    /*
     * You must provide a Twilio Access Token to connect to the Video service
     */
    //private static final String TWILIO_ACCESS_TOKEN_PAU="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzIyZTk5N2NkNDMyMjAwNzIzMmIzZDg5Mjk4NzViYTM5LTE0ODgxNDg3ODQiLCJncmFudHMiOnsiaWRlbnRpdHkiOiJ0ZXN0MiIsInJ0YyI6eyJjb25maWd1cmF0aW9uX3Byb2ZpbGVfc2lkIjoiVlMzZGUxYzAyYTA3YjllZTIyNTlmMzZkODUzMDBlNzA1NSJ9LCJ2aWRlbyI6eyJjb25maWd1cmF0aW9uX3Byb2ZpbGVfc2lkIjoiVlMzZGUxYzAyYTA3YjllZTIyNTlmMzZkODUzMDBlNzA1NSJ9fSwiaWF0IjoxNDg4MTQ4Nzg0LCJleHAiOjE0ODgxNTIzODQsImlzcyI6IlNLMjJlOTk3Y2Q0MzIyMDA3MjMyYjNkODkyOTg3NWJhMzkiLCJzdWIiOiJBQzRiNDEwYWQ3ZTVmMjdhNGUwOGJlMDZiODkxYTE4OTc2In0.uMP60q7AZ1o62srFYCBtu0kYy1mmPRoM6PLdt_HV8mg";
    //private static final String TWILIO_ACCESS_TOKEN_INGE = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzc1Y2VlMmJlNjU1ZTNlMWY2ZDUzZTIwM2U2NmUzZTE0LTE0ODc3NzcyNTgiLCJpc3MiOiJTSzc1Y2VlMmJlNjU1ZTNlMWY2ZDUzZTIwM2U2NmUzZTE0Iiwic3ViIjoiQUM0YjQxMGFkN2U1ZjI3YTRlMDhiZTA2Yjg5MWExODk3NiIsImV4cCI6MTQ4Nzc4MDg1OCwiZ3JhbnRzIjp7ImlkZW50aXR5IjoiaW5nZSIsInJ0YyI6eyJjb25maWd1cmF0aW9uX3Byb2ZpbGVfc2lkIjoiVlNkNmMwMzZlOWFkMWJlODg5NTAyYThjNDA4MTZjMWE5YyJ9fX0.YSB-l23Pd3j-Ci-spDQ48IlHhEldps8CFCtamhiNWio";
    //private static final String TWILIO_ACCESS_TOKEN = TWILIO_ACCESS_TOKEN_PAU;
    //private static final String TWILIO_ACCESS_TOKEN = TWILIO_ACCESS_TOKEN_INGE;
    /*
     * The Video Client allows a client to connect to a room
     */
    private VideoClient videoClient;

    private Meteor mMeteor;

    /*
     * A Room represents communication between the client and one or more participants.
     */
    private Room room;

    private boolean inRoom = false;

    /*
     * A VideoView receives frames from a local or remote video track and renders them
     * to an associated view.
     */
    private MyPrimaryVideoView primaryVideoView;
    private MyThumbnailVideoView thumbnailVideoView;

    /*
     * Android application UI elements
     */
    private TextView videoStatusTextView;
    //private CameraCapturer cameraCapturer;
    private ViewCapturer viewCapturer;
    private LocalMedia localMedia;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private android.support.v7.app.AlertDialog alertDialog;
    private AudioManager audioManager;
    private String participantIdentity;

    private int previousAudioMode;
    private VideoRenderer localVideoView;
    private boolean disconnectedFromOnDestroy;

    private User user;
    private Realm realm;
    private VideoRoom videoRoom;

    private SongSelectionFragment songSelectionFragment;
    private SongPlayer songPlayer;
    private RequestUser requestUser;
    private SelectFriendFragment selectFriendFragment;
    private StartOrJoinFragment startOrJoinFragment;
    private Song song;
    private boolean master = false;
    private Participant participant;
    private WaitForStartFragment waitForStartFragment;
    private PlayingFragment playingFragment;
    private RequestFragment requestFragment;
    private RelativeLayout rootContainer;
    private FrameLayout preview;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        realm = Realm.getDefaultInstance();
        user = UserManager.getInstance(this).getMainUser(realm);

        primaryVideoView = (MyPrimaryVideoView) findViewById(R.id.primary_video_view);
        songPlayer = new SongPlayer(this, primaryVideoView);
        songPlayer.setListener(this);
        thumbnailVideoView = (MyThumbnailVideoView) findViewById(R.id.thumbnail_video_view);
        videoStatusTextView = (TextView) findViewById(R.id.video_status_textview);
        rootContainer = (RelativeLayout) findViewById(R.id.video_container);

        mPreview = new CameraPreview(this);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Needed for setting/abandoning audio focus during call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        /*
         * Check camera and microphone permissions. Needed in Android M.
         */
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else {
            createLocalMedia();
            createVideoClient();
        }

        /*
        Connect to meteor database
         */
        meteorConfig();

        //Show start fragment
        showStartFragment();

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rainConfetti();
                //rainSnow();

            }
        }, 1000);*/
    }

    private void meteorConfig() {
        // create a new instance
        mMeteor = MeteorSingleton.getInstance();

        // register the callback that will handle events and receive messages
        mMeteor.addCallback(new MeteorListener(this, realm, user, mMeteor));

        // establish the connection
        mMeteor.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            boolean cameraAndMicPermissionGranted = true;

            for (int grantResult : grantResults) {
                cameraAndMicPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }

            if (cameraAndMicPermissionGranted) {
                createLocalMedia();
                createVideoClient();
            } else {
                Toast.makeText(this,
                        R.string.permissions_needed,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * If the local video track was removed when the app was put in the background, add it back.
         */
        if (localMedia != null && localVideoTrack == null) {
            //localVideoTrack = localMedia.addVideoTrack(true, cameraCapturer);
            localVideoTrack = localMedia.addVideoTrack(true, viewCapturer);
            localVideoTrack.addRenderer(localVideoView);
        }
    }

    @Override
    protected void onPause() {
        /*
         * Remove the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         *
         * If this local video track is being shared in a Room, participants will be notified
         * that the track has been removed.
         */
        if (localMedia != null && localVideoTrack != null) {
            localMedia.removeVideoTrack(localVideoTrack);
            localVideoTrack = null;
        }
        stopSing();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */
        if (room != null && room.getState() != RoomState.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;
        }

        /*
         * Release the local media ensuring any memory allocated to audio or video is freed.
         */
        if (localMedia != null) {
            localMedia.release();
            localMedia = null;
        }

        super.onDestroy();
    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                    R.string.permissions_needed,
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private void createLocalMedia() {
        localMedia = LocalMedia.create(this);

        // Share your microphone
        localAudioTrack = localMedia.addAudioTrack(true);

        // Share your camera
        //cameraCapturer = new CameraCapturer(this, CameraSource.FRONT_CAMERA);
        //localVideoTrack = localMedia.addVideoTrack(true, cameraCapturer);

        viewCapturer = new ViewCapturer(mPreview);
        localVideoTrack = localMedia.addVideoTrack(true, viewCapturer);

        primaryVideoView.setMirror(true);
        localVideoTrack.addRenderer(primaryVideoView);
        localVideoView = primaryVideoView;
    }

    private void createVideoClient() {
        /*
         * Create a VideoClient allowing you to connect to a Room
         */
        Log.d(TAG, "createVideoClient");

        Ion.with(this)
                // Make JSON request to server
                .load("http://pautena.com:4001/twilio/token?name=" + user.getUsername())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    // Handle response from server
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            Log.d(TAG, "result: " + result);
                            // The identity can be used to receive calls
                            String identity = result.get("identity").getAsString();
                            String accessToken = result.get("token").getAsString();
                            Log.i(TAG, "Token found: " + accessToken);

                            videoClient = new VideoClient(VideoActivity.this, accessToken);
                        } else {
                            Log.i(TAG, "Error fetching token from server");
                        }
                    }
                });

        // OPTION 1- Generate an access token from the getting started portal
        // https://www.twilio.com/console/video/dev-tools/testing-tools

        // OPTION 2- Retrieve an access token from your own web app
        // retrieveAccessTokenfromServer();
    }

    private void connectToRoom(String roomName) {
        inRoom = true;
        setAudioFocus(true);
        ConnectOptions connectOptions = new ConnectOptions.Builder()
                .roomName(roomName)
                .localMedia(localMedia)
                .build();
        room = videoClient.connect(connectOptions, roomListener());
        setDisconnectAction();
    }

    /*
     * The actions performed during disconnect.
     */
    private void setDisconnectAction() {
        //TODO
    }


    /*
     * Called when participant joins the room
     */
    private void addParticipant(Participant participant) {
        this.participant = participant;

        if (waitForStartFragment != null) {
            waitForStartFragment.setHaveParticipant(true);
        }
        /*
         * This app only displays video for one additional participant per Room
         */
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            return;
        }
        participantIdentity = participant.getIdentity();
        videoStatusTextView.setText("Participant " + participantIdentity + " joined");

        /*
         * Add participant renderer
         */
        if (participant.getMedia().getVideoTracks().size() > 0) {
            addParticipantVideo(participant.getMedia().getVideoTracks().get(0));
        }

        /*
         * Start listening for participant media events
         */
        participant.getMedia().setListener(mediaListener());
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private void addParticipantVideo(VideoTrack videoTrack) {
        moveLocalVideoToThumbnailView();
        primaryVideoView.setMirror(false);
        videoTrack.addRenderer(primaryVideoView);
    }

    private void moveLocalVideoToThumbnailView() {
        if (thumbnailVideoView.getVisibility() == View.GONE) {
            thumbnailVideoView.setVisibility(View.VISIBLE);
            localVideoTrack.removeRenderer(primaryVideoView);
            localVideoTrack.addRenderer(thumbnailVideoView);
            localVideoView = thumbnailVideoView;
            //TODO: thumbnailVideoView.setMirror(cameraCapturer.getCameraSource() == CameraSource.FRONT_CAMERA);
        }
    }

    /*
     * Called when participant leaves the room
     */
    private void removeParticipant(Participant participant) {
        videoStatusTextView.setText("Participant " + participant.getIdentity() + " left.");
        if (!participant.getIdentity().equals(participantIdentity)) {
            return;
        }

        /*
         * Remove participant renderer
         */
        if (participant.getMedia().getVideoTracks().size() > 0) {
            removeParticipantVideo(participant.getMedia().getVideoTracks().get(0));
        }
        participant.getMedia().setListener(null);
        moveLocalVideoToPrimaryView();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeRenderer(primaryVideoView);
    }

    private void moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            localVideoTrack.removeRenderer(thumbnailVideoView);
            thumbnailVideoView.setVisibility(View.GONE);
            localVideoTrack.addRenderer(primaryVideoView);
            localVideoView = primaryVideoView;
            //TODO primaryVideoView.setMirror(cameraCapturer.getCameraSource() == CameraSource.FRONT_CAMERA);
        }
    }

    /*
     * Room events listener
     */
    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                videoStatusTextView.setText("Connected to " + room.getName());
                setTitle(room.getName());

                for (Map.Entry<String, Participant> entry : room.getParticipants().entrySet()) {
                    addParticipant(entry.getValue());
                    break;
                }
            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
                videoStatusTextView.setText("Failed to connect");
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                videoStatusTextView.setText("Disconnected from " + room.getName());
                VideoActivity.this.room = null;
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    setAudioFocus(false);
                    moveLocalVideoToPrimaryView();
                }
            }

            @Override
            public void onParticipantConnected(Room room, Participant participant) {
                addParticipant(participant);

            }

            @Override
            public void onParticipantDisconnected(Room room, Participant participant) {
                removeParticipant(participant);
            }

            @Override
            public void onRecordingStarted(Room room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d(TAG, "onRecordingStarted");
            }

            @Override
            public void onRecordingStopped(Room room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d(TAG, "onRecordingStopped");
            }
        };
    }

    private Media.Listener mediaListener() {
        return new Media.Listener() {

            @Override
            public void onAudioTrackAdded(Media media, AudioTrack audioTrack) {
                videoStatusTextView.setText("onAudioTrackAdded");
            }

            @Override
            public void onAudioTrackRemoved(Media media, AudioTrack audioTrack) {
                videoStatusTextView.setText("onAudioTrackRemoved");
            }

            @Override
            public void onVideoTrackAdded(Media media, VideoTrack videoTrack) {
                videoStatusTextView.setText("onVideoTrackAdded");
                addParticipantVideo(videoTrack);
            }

            @Override
            public void onVideoTrackRemoved(Media media, VideoTrack videoTrack) {
                videoStatusTextView.setText("onVideoTrackRemoved");
                removeParticipantVideo(videoTrack);
            }

            @Override
            public void onAudioTrackEnabled(Media media, AudioTrack audioTrack) {

            }

            @Override
            public void onAudioTrackDisabled(Media media, AudioTrack audioTrack) {

            }

            @Override
            public void onVideoTrackEnabled(Media media, VideoTrack videoTrack) {

            }

            @Override
            public void onVideoTrackDisabled(Media media, VideoTrack videoTrack) {

            }
        };
    }

    private View.OnClickListener joinFriendActionListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSelectFriendFragment();
            }
        };
    }

    private View.OnClickListener searchSongActionListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSelectSongsFragment();
            }
        };
    }

    private void showSelectSongsFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        songSelectionFragment = SongSelectionFragment.newInstance();

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, songSelectionFragment, SongSelectionFragment.TAG)
                .addToBackStack(SongSelectionFragment.TAG)
                .commit();

    }

    private void showStartFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        startOrJoinFragment = StartOrJoinFragment.newInstance();

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, startOrJoinFragment, StartOrJoinFragment.TAG)
                .addToBackStack(StartOrJoinFragment.TAG)
                .commit();

    }

    private void showPlayingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        playingFragment = PlayingFragment.newInstance(videoRoom);

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, playingFragment, PlayingFragment.TAG)
                .addToBackStack(PlayingFragment.TAG)
                .commit();
    }

    private void showSelectFriendFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        selectFriendFragment = SelectFriendFragment.newInstance();

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, selectFriendFragment, SelectFriendFragment.TAG)
                .addToBackStack(SelectFriendFragment.TAG)
                .commit();
    }

    private void showWaitForStartFragment(boolean master) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        waitForStartFragment = WaitForStartFragment.newInstance(participant, master);

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, waitForStartFragment, WaitForStartFragment.TAG)
                .addToBackStack(WaitForStartFragment.TAG)
                .commit();
    }

    private void showRequestFragment(VideoRoom room) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        requestFragment = RequestFragment.newInstance(room);

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, requestFragment, RequestFragment.TAG)
                .addToBackStack(RequestFragment.TAG)
                .commit();
    }

    private void setAudioFocus(boolean focus) {
        if (focus) {
            previousAudioMode = audioManager.getMode();
            // Request audio focus before making any device switch.
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(previousAudioMode);
            audioManager.abandonAudioFocus(null);
        }
    }

    @Override
    public void onSelectSong(Song song) {
        this.song = song;


        ApiServiceAdapter.getInstance(this).createRoom(song, user.getUsername(), new CreateRoomCallback() {


            @Override
            public void onCreateRoomFinish(VideoRoom videoRoom) {
                VideoActivity.this.videoRoom = videoRoom;
                VideoActivity.this.master = true;
                connectToRoom(videoRoom.getId());
            }
        });
        showSelectFriendFragment();
    }

    public void playSong() {
        Song song = videoRoom.getSong();
        Log.d(TAG, "playSong(master:" + master + "). song: " + song);
        songPlayer.play(song);
        showPlayingFragment();
    }

    @Override
    public void onSelectFriend(RequestUser user) {
        this.requestUser = user;

        ApiServiceAdapter.getInstance(this).requestJoinToRoom(videoRoom, user, new RequestJoinCallback() {
            @Override
            public void onFinishRequest() {

            }
        });

        showWaitForStartFragment(master);
    }

    @Override
    public void onStartServer() {
        //TODO
        Log.d(TAG, "onStartServer");
        showSelectSongsFragment();
    }

    @Override
    public void onStartSing() {
        Log.d(TAG, "onStartSing. roomId: " + videoRoom.getId());

        Map<String, Object> query = new HashMap<>();
        query.put("_id", videoRoom.getId());

        Map<String, Object> values = new HashMap<>();
        values.put("started", true);

        Map<String, Object> set = new HashMap<>();
        set.put("$set", values);

        mMeteor.update("rooms", query, set);

    }

    public void stopSing() {
        Map<String, Object> query = new HashMap<>();
        query.put("_id", videoRoom.getId());

        Map<String, Object> values = new HashMap<>();
        values.put("started", false);

        Map<String, Object> set = new HashMap<>();
        set.put("$set", values);

        mMeteor.update("rooms", query, set);
    }

    @Override
    public void onSwitchCamera() {
        /*TODO if (cameraCapturer != null) {
            CameraSource cameraSource = cameraCapturer.getCameraSource();
            cameraCapturer.switchCamera();
            if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
                thumbnailVideoView.setMirror(cameraSource == CameraSource.BACK_CAMERA);
            } else {
                primaryVideoView.setMirror(cameraSource == CameraSource.BACK_CAMERA);
            }
        }*/
    }


    @Override
    public boolean localVideo() {
/*
                 * Enable/disable the local video track
                 */
        if (localVideoTrack != null) {
            boolean enable = !localVideoTrack.isEnabled();
            localVideoTrack.enable(enable);
            int icon;
            if (enable) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean muteAction() {

        /*
                 * Enable/disable the local audio track. The results of this operation are
                 * signaled to other Participants in the same Room. When an audio track is
                 * disabled, the audio is muted.
                 */
        if (localAudioTrack != null) {
            boolean enable = !localAudioTrack.isEnabled();
            localAudioTrack.enable(enable);
            return enable;
        }
        return true;
    }

    public void onRequestReceived(VideoRoom room) {
        this.videoRoom = room;

        if (!inRoom) {
            Log.d(TAG, "videoRoom. song: " + room.getSong().getTitle());
            showRequestFragment(room);
        }
    }

    @Override
    public void onAcceptRequest(VideoRoom videoRoom) {
        Map<String, Object> query = new HashMap<>();
        query.put("_id", videoRoom.getId());

        Map<String, Object> values = new HashMap<>();
        values.put("accepted", true);

        Map<String, Object> set = new HashMap<>();
        set.put("$set", values);


        mMeteor.update("rooms", query, set);

        connectToRoom(videoRoom.getId());
        showWaitForStartFragment(master);
    }


    public void setViewers(int viewers) {
        if (videoRoom != null) {
            realm.beginTransaction();
            videoRoom.setViewers(viewers);
            realm.commitTransaction();

            if (playingFragment != null) {
                playingFragment.setViewers(viewers);
            }
        }
    }

    @Override
    public void onFinishSong() {
        stopSing();
    }

    public void onUpdateVotes(int vote, int nVotes) {
        Log.d(TAG, "onUpdateVotes. vote: " + vote + ", nVotes: " + nVotes);
        int oldVote = videoRoom.getVote();

        realm.beginTransaction();
        videoRoom.setVote(vote);
        if (nVotes > 0) {
            videoRoom.setnVotes(nVotes);
        }
        realm.commitTransaction();

        if (master) {
            if (vote < oldVote) {
                rainConfetti();
            } else {
                rainSnow();
            }
        } else {
            if (vote > oldVote) {
                rainConfetti();
            } else {
                rainSnow();
            }
        }
    }

    private void rainSnow() {

        new ParticleSystem(this, 80, R.drawable.snowflake, 10000)
                .setSpeedModuleAndAngleRange(0.1f, 0.3f, 0, 90)
                .setRotationSpeed(144)
                .oneShot(findViewById(R.id.emiter_top_left), 20);


        new ParticleSystem(this, 80, R.drawable.snowflake, 10000)
                .setSpeedModuleAndAngleRange(0.1f, 0.3f, 90, 180)
                .setRotationSpeed(144)
                .oneShot(findViewById(R.id.emiter_top_right), 20);
    }

    private void rainConfetti() {

        new ParticleSystem(this, 60, R.drawable.confeti2, 10000)
                .setSpeedModuleAndAngleRange(0.4f, 0.9f, 0, 90)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.emiter_top_left), 60);


        new ParticleSystem(this, 60, R.drawable.confeti3, 10000)
                .setSpeedModuleAndAngleRange(0.4f, 0.9f, 90, 180)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.emiter_top_right), 60);
    }
}
