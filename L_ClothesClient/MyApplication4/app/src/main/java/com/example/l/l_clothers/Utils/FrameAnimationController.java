package com.example.l.l_clothers.Utils;

import android.os.Message;

/**
 * Created by L on 2017/6/4.
 */

public class FrameAnimationController {//控制播放动画
    private static final int MSG_ANIMATE=1000;
    private static final int ANIMATION_FRAME_DURATION=1000/60;//动画持续时间
    private static final android.os.Handler mHandler=new AnimationHandler();


    private FrameAnimationController()
    {
        throw new UnsupportedOperationException();
    }

    public static void requestAnimationFrame(Runnable runnable)
    {
        Message message=new Message();
        message.what=MSG_ANIMATE;//动画效果
        message.obj=runnable;
        mHandler.sendMessageDelayed(message,ANIMATION_FRAME_DURATION);
    }

    public static void requesetFrameDelay(Runnable runnable,long delay)
    {
        Message message = new Message();
        message.what = MSG_ANIMATE;
        message.obj = runnable;
        mHandler.sendMessageDelayed(message, delay);
    }//延迟请求时间

    private static class AnimationHandler extends android.os.Handler {
        public void handleMessage(Message m) {
            switch (m.what) {
                case MSG_ANIMATE:
                    if (m.obj != null) {
                        ((Runnable) m.obj).run();
                    }
                    break;
            }
        }
    }
}

