package com.pautena.hackupc.utils.filters;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by pautenavidal on 4/3/17.
 */

public abstract class Filter {

    private final Context context;

    public Filter(Context context){
        this.context = context;
    }

    public abstract void calculate(Canvas canvas);

    public abstract void paint(Canvas canvas);

    public Context getContext() {
        return context;
    }
}
