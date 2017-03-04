package com.pautena.hackupc.ui.twillio.utils;

/**
 * Created by pautenavidal on 4/3/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.twilio.video.CameraCapturer;
import com.twilio.video.VideoCapturer;
import com.twilio.video.VideoDimensions;
import com.twilio.video.VideoFormat;
import com.twilio.video.VideoFrame;
import com.twilio.video.VideoPixelFormat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewCapturer implements VideoCapturer {
    public static final int BACK_CAMERA = 0;
    public static final int FRONT_CAMERA = 1;
    private static final int VIEW_CAPTURER_FRAMERATE_MS = 100;
    private static final String TAG = ViewCapturer.class.getSimpleName();

    private final Context context;
    private final int cameraIndex;
    private final Camera camera;
    private byte[] data = null;
    private Handler handler = new Handler(Looper.getMainLooper());
    private VideoCapturer.Listener videoCapturerListener;
    private AtomicBoolean started = new AtomicBoolean(false);
    private final Runnable viewCapturer = new Runnable() {
        @Override
        public void run() {
            boolean dropFrame = (data == null);

            // Only capture the view if the dimensions have been established
            if (!dropFrame) {
                // Draw view into bitmap backed canvas
                Camera.Size size = camera.getParameters().getPreviewSize();

                int width = size.width;
                int height = size.height;

                final long captureTimeNs =
                        TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());

                // Create video frame
                VideoDimensions dimensions = new VideoDimensions(width, height);
                VideoFrame videoFrame = new VideoFrame(data, dimensions, 0, captureTimeNs);

                // Notify the listener
                if (started.get()) {
                    videoCapturerListener.onFrameCaptured(videoFrame);
                }
            }

            // Schedule the next capture
            if (started.get()) {
                handler.postDelayed(this, VIEW_CAPTURER_FRAMERATE_MS);
            }
        }
    };

    public ViewCapturer(Context context, int cameraIndex) {
        this.context = context;
        this.cameraIndex = cameraIndex;
        camera = Camera.open(cameraIndex);

        camera.setPreviewCallback(new Camera.PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                ViewCapturer.this.data = data;
                Log.d(TAG, "new previewFrame");
            }
        });


        camera.startPreview();
    }

    /**
     * Returns the list of supported formats for this view capturer. Currently, only supports
     * capturing to RGBA_8888 bitmaps.
     *
     * @return list of supported formats.
     */
    @Override
    public List<VideoFormat> getSupportedFormats() {
        Camera.Size size = camera.getParameters().getPreviewSize();

        int width = size.width;
        int height = size.height;

        List<VideoFormat> videoFormats = new ArrayList<>();
        VideoDimensions videoDimensions = new VideoDimensions(width, height);
        VideoFormat videoFormat = new VideoFormat(videoDimensions, 30, VideoPixelFormat.RGBA_8888);

        videoFormats.add(videoFormat);

        return videoFormats;
    }

    /**
     * Returns true because we are capturing screen content.
     */
    @Override
    public boolean isScreencast() {
        return true;
    }

    /**
     * This will be invoked when it is time to start capturing frames.
     *
     * @param videoFormat the video format of the frames to be captured.
     * @param listener    capturer listener.
     */
    @Override
    public void startCapture(VideoFormat videoFormat, Listener listener) {
        Log.d(TAG, "startCapture");
        // Store the capturer listener
        this.videoCapturerListener = listener;
        this.started.set(true);

        // Notify capturer API that the capturer has started
        boolean capturerStarted = handler.postDelayed(viewCapturer,
                VIEW_CAPTURER_FRAMERATE_MS);
        this.videoCapturerListener.onCapturerStarted(capturerStarted);
    }

    /**
     * Stop capturing frames. Note that the SDK cannot receive frames once this has been invoked.
     */
    @Override
    public void stopCapture() {
        this.started.set(false);
        handler.removeCallbacks(viewCapturer);
    }
}
