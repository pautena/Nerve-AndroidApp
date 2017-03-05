package com.pautena.hackupc.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pautenavidal on 5/3/17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = CameraPreview.class.getSimpleName();
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private int cameraIndex = 1;
    private Camera.Face faceDetection = null;
    private Camera.Face[] detectedFaces = new Camera.Face[0];
    Paint drawingPaint;

    class MyFaceDetectionListener implements Camera.FaceDetectionListener {


        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length > 0) {
                Log.d("FaceDetection", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY());

                detectedFaces = faces;
                CameraPreview.this.invalidate();

            } else {
                faceDetection = null;
            }
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Log.d(TAG, "file wirted to path: " + pictureFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    public CameraPreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        //Faces detection paint
        drawingPaint = new Paint();
        drawingPaint.setColor(Color.GREEN);
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(2);
        mCamera = getCameraInstance();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated. camera: " + mCamera);
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            setWillNotDraw(false);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(); // attempt to get a Camera instance

        } catch (Exception e) {
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return camera; // returns null if camera is unavailable
    }


    public void applySepiaFliter() {
        Camera.Parameters par = mCamera.getParameters();
        par.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
        mCamera.setParameters(par);
    }

    public void applyGreyFliter() {
        Camera.Parameters par = mCamera.getParameters();
        par.setColorEffect(Camera.Parameters.EFFECT_MONO);
        mCamera.setParameters(par);
    }

    public void applyInvFilter() {
        Camera.Parameters par = mCamera.getParameters();
        par.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
        mCamera.setParameters(par);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void releaseMediaRecorder() {
        Log.d(TAG, "releaseMediaRecorder");
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    public void onPause() {

        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private boolean prepareVideoRecorder() {

        mCamera = getCameraInstance();

        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void takePicture() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, mPicture);
        }
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void stopRecording() {
        Log.d(TAG, "stopRecording. isRecording: " + isRecording());
        if (isRecording()) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            isRecording = false;
        }
    }

    public boolean startRecording() {
        Log.d(TAG, "startRecording. isRecording: " + isRecording());
        if (!isRecording()) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                // inform the user that recording has started
                isRecording = true;
                return true;
            } else {
                Log.e(TAG, "prepare didn't work, release the camera");
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
        return false;
    }

    public void nextCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();

        cameraIndex++;
        cameraIndex %= numberOfCameras;

        Log.d(TAG, "nextCamera. index: " + cameraIndex + ", numberOfCameras: " + numberOfCameras);

        if (mCamera != null) {
            mCamera.stopPreview();
        }
        releaseCamera();

        mCamera = getCameraInstance();


        Camera.Parameters param = mCamera.getParameters();
        Log.d("camera", "parameters: " + param.flatten());

        try {
            mCamera.setPreviewDisplay(getHolder());
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }


    public void enableFaceDetection() {
        mCamera.setFaceDetectionListener(new MyFaceDetectionListener());

        // Try starting Face Detection
        Camera.Parameters params = mCamera.getParameters();

        // start face detection only *after* preview has started
        if (params.getMaxNumDetectedFaces() > 0) {
            // camera supports face detection, so can start it:
            mCamera.startFaceDetection();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        drawDetectedFace(canvas);
    }

    private void drawDetectedFace(Canvas canvas) {
        int vWidth = getWidth();
        int vHeight = getHeight();

        Log.d("CameraPreview", "faceDetection: " + faceDetection);
        for (int i = 0; i < detectedFaces.length; i++) {

            int l = detectedFaces[i].rect.left;
            int t = detectedFaces[i].rect.top;
            int r = detectedFaces[i].rect.right;
            int b = detectedFaces[i].rect.bottom;

            int left = (l + 1000) * vWidth / 2000;
            int top = (t + 1000) * vHeight / 2000;
            int right = (r + 1000) * vWidth / 2000;
            int bottom = (b + 1000) * vHeight / 2000;
            canvas.drawRect(vWidth - left, top, vWidth - right, bottom,drawingPaint);
        }
    }
}
