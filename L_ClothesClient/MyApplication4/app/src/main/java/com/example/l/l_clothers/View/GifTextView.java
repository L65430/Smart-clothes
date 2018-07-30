package com.example.l.l_clothers.View;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.l.l_clothers.Utils.GifExpressionUtil;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by L on 2017/6/6.
 */

public class GifTextView extends android.support.v7.widget.AppCompatTextView implements Runnable {
    public static boolean mRunning = true;
    private Vector<GifDrawable> drawables;
    private Hashtable<Integer, GifDrawable> cache;
    private final int SPEED = 100;
    private Context context = null;

    public GifTextView(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;

        drawables = new Vector<GifDrawable>();
        cache = new Hashtable<Integer, GifDrawable>();

        new Thread(this).start();
    }

    public GifTextView(Context context) {
        super(context);
        this.context = context;

        drawables = new Vector<GifDrawable>();
        cache = new Hashtable<Integer, GifDrawable>();

        new Thread(this).start();
    }

    public void insertGif(SpannableString str) {
        if (drawables.size() > 0)
            drawables.clear();
        SpannableString spannableString = GifExpressionUtil.getExpressionString(
                context, str, cache, drawables);//就是形成那种变长度的效果
        setText(spannableString);
    }

    @Override
    public void run() {
        while (mRunning) {
            if (super.hasWindowFocus()) {
                for (int i = 0; i < drawables.size(); i++) {
                    drawables.get(i).run();
                }
                postInvalidate();
            }
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(SPEED);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        mRunning = false;
        drawables.clear();
        drawables = null;
    }

}

