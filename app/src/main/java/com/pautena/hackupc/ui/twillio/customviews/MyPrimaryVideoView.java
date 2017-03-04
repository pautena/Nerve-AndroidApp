package com.pautena.hackupc.ui.twillio.customviews;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.pautena.hackupc.R;
import com.pautena.hackupc.utils.filters.Filter;
import com.twilio.video.VideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class MyPrimaryVideoView extends VideoView {
    private static final String TAG = MyPrimaryVideoView.class.getSimpleName();

    private int delay = -1;
    private List<String> lyricsLines;
    private int lyricLineSeparation;

    private int numberColor;
    private int numberTextSize;

    private int lyricsColor;
    private int lyricsTextSize;

    private Filter filter;
    private Typeface textTypeFace;

    public MyPrimaryVideoView(Context context) {
        super(context);
        initialize();
    }


    public MyPrimaryVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }


    private void initialize() {
        lyricsLines = new ArrayList<>();
        lyricLineSeparation = getContext().getResources().getInteger(R.integer.lyric_line_separation);

        numberColor = getContext().getResources().getColor(R.color.primary_video_number_color);
        lyricsColor = getContext().getResources().getColor(R.color.primary_video_lyrics_color);

        numberTextSize = getContext().getResources().getInteger(R.integer.primary_video_number_size);
        lyricsTextSize = getContext().getResources().getInteger(R.integer.primary_video_lyrics_size);

        AssetManager assetManager = getContext().getAssets();
        Typeface plain = Typeface.createFromAsset(assetManager, "LuckiestGuy.ttf");
        textTypeFace = Typeface.create(plain, 1);

    }

    public void delayToStart(int delay) {
        this.delay = delay;
        invalidate();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        setWillNotDraw(false);

    }

    public void setLyricsLines(List<String> lyricsLines) {
        this.lyricsLines = lyricsLines;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");


        writeTexts(canvas);
        filters(canvas);
    }

    private void filters(Canvas canvas) {
        if (filter != null) {
            filter.calculate(canvas);
            filter.paint(canvas);
        }
    }

    private void writeTexts(Canvas canvas) {
        if (delay > -1) {
            paintNumber(canvas, delay);
        }

        for (int i = 0; i < lyricsLines.size(); ++i) {
            String line = lyricsLines.get(i);

            writeLyricLine(i, canvas, line);
        }
    }

    private void paintNumber(Canvas canvas, int number) {
        Rect r = new Rect();
        String text = String.valueOf((number / 1000));
        canvas.getClipBounds(r);

        Paint paint = new Paint();
        paint.setTypeface(textTypeFace);
        paint.setColor(numberColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(numberTextSize);

        paint.getTextBounds(text, 0, text.length(), r);


        Log.d(TAG, "r.height(): " + r.height());

        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2 + r.height() / 2;
        canvas.drawText(text, x, y, paint);


        //STROKE
        Paint stkPaint = new Paint();
        stkPaint.setTypeface(textTypeFace);
        stkPaint.setStyle(Paint.Style.STROKE);
        stkPaint.setStrokeWidth(5);
        stkPaint.setTextAlign(Paint.Align.CENTER);
        stkPaint.setTextSize(numberTextSize);
        stkPaint.setColor(Color.BLACK);
        canvas.drawText(text, x, y, stkPaint);
    }

    private void writeLyricLine(int index, Canvas canvas, String line) {
        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() - 300 + lyricLineSeparation * index;

        Paint paint = new Paint();
        paint.setTypeface(textTypeFace);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(lyricsColor);
        paint.setTextSize(lyricsTextSize);
        canvas.drawText(line, x, y, paint);

        //STROKE
        Paint stkPaint = new Paint();
        stkPaint.setTypeface(textTypeFace);
        stkPaint.setStyle(Paint.Style.STROKE);
        stkPaint.setStrokeWidth(3);
        stkPaint.setTextAlign(Paint.Align.CENTER);
        stkPaint.setColor(Color.BLACK);
        stkPaint.setTextSize(lyricsTextSize);
        canvas.drawText(line, x, y, stkPaint);

        Log.d(TAG, "write line: " + line + ", x: " + x + ", y: " + y);
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void deleteFilter() {
        this.filter = null;
    }
}
