package com.pautena.hackupc.utils.lrc;

import android.util.Log;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pautenavidal on 4/3/17.
 */

public class Lrc {
    private static final String TAG = Lrc.class.getSimpleName();
    private List<LrcPart> parts;

    public Lrc() {
        parts = new ArrayList<>();
    }

    public LrcPart getPart(Time time) {

        SimpleDateFormat format = new SimpleDateFormat("mm:ss.S");
        String strtime = format.format(time);

        for (int i = 0; i < parts.size(); ++i) {
            LrcPart part = parts.get(i);
            if (strtime.compareTo(part.getTime()) < 0) {
                Log.d(TAG, "return element " + i + ", strtime: " + strtime + ", partTime:" + part.getTime());

                if (i == 0) {
                    return parts.get(0);
                } else {
                    return parts.get(i-1);
                }
            }
        }

        if (parts.isEmpty()) {
            Log.d(TAG, " no match found. parts is empty");
            return null;
        } else {
            Log.d(TAG, "no match found. return 0.strtime: " + strtime + ", partTime:" + parts.get(0).getTime());
            return parts.get(0);
        }
    }

    public void addPart(LrcPart part) {
        this.parts.add(part);
    }
}
