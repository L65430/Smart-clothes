package com.example.l.l_clothers.View;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

import com.example.l.l_clothers.Utils.GifHelper;
import com.example.l.l_clothers.Utils.ImgUtil;

/**
 * Created by L on 2017/6/6.
 */

public class GifDrawable extends AnimationDrawable {

    public GifDrawable(Context context, int id) {
        GifHelper helper = new GifHelper();
        helper.read(context.getResources().openRawResource(id));
        int gifCount = helper.getFrameCount();
        if (gifCount <= 0) {
            return;
        }
        BitmapDrawable bd = new BitmapDrawable(null, helper.getImage());
        addFrame(bd, helper.getDelay(0));
        for (int i = 1; i < helper.getFrameCount(); i++) {
            int t =helper.getDelay(i)*1000;
            addFrame(new BitmapDrawable(null, helper.nextBitmap()),t);
        }
        setBounds(0, 0, ImgUtil.dip2px(context, helper.getImage().getWidth() / 2),
                ImgUtil.dip2px(context, helper.getImage().getHeight() / 2));
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
        invalidateSelf();
    }

}
