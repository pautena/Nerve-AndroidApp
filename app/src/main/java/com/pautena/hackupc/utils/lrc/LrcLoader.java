package com.pautena.hackupc.utils.lrc;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by pautenavidal on 4/3/17.
 */

public final class LrcLoader {
    private static final String TAG = LrcLoader.class.getSimpleName();

    public static Lrc load(Context context, String assetName) {
        Lrc lrc = new Lrc();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(assetName)));

            // do reading, usually loop until end of file reading
            String mLine;
            LrcPart part = new LrcPart();

            while ((mLine = reader.readLine()) != null) {
                Log.d(TAG, mLine);
                if (mLine.equals("") && !part.isEmpty()) {
                    lrc.addPart(part);
                    part = new LrcPart();
                    Log.d(TAG, "finishing line!!!");
                } else {
                    part.addLine(mLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        return lrc;
    }

    private LrcLoader() {
    }

}
