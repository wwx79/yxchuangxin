package com.p2p.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;

import com.p2p.core.MediaPlayer.IVideoPTS;
import com.p2p.core.global.P2PConstants;

import java.io.IOException;

public abstract class BasePlayBackActivity extends BaseP2PviewActivity {
    boolean isFullScreen = true;

    private static final int NONE = 0;
    private static final int PAUSE = 2;
    private static final int START = 3;
    private static final int JUMP = 4;
    private static final int NEXT = 5;
    private static final int PREVIOUS = 6;
    private static final int NAMEPLAY = 7;
    private static final int FASTPLAY = 8;
    private static final int FASTPLAY_CANCEL = 9;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化P2PView
     *
     * @param type       宽高类型
     * @param layoutType 布局类型默认分离使用（call与P2Pview分开）
     */
    public void initP2PView(int type, int layoutType) {
        initP2PView(type, layoutType, new GestureListener());
        pView.fullScreen();//全景增加
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onP2PViewSingleTap();
            return super.onSingleTapConfirmed(e);
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isFullScreen) {
                isFullScreen = true;
                pView.fullScreen();
            } else {
                //isFullScreen = false;//全景修改
                //pView.halfScreen();
            }
            DoubleTap();//全景增加
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //全景增加
            try {
                MediaPlayer.getInstance()._OnGesture(3, 1, e.getX(), e.getY());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                MediaPlayer.getInstance()._OnGesture(2, 1, velocityX, velocityY);//全景增加
            } catch (IOException e) {
                e.printStackTrace();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    //可供子类使用
    public void DoubleTap() {

    }

    public void pausePlayBack() {
        MediaPlayer.iRecFilePlayingControl(PAUSE, 0, "test".getBytes());
    }

    public void startPlayBack() {
        MediaPlayer.iRecFilePlayingControl(START, 0, "test".getBytes());
        pView.fullScreen();//全景增加
    }

    public boolean previous(String filename) {
        return MediaPlayer.iRecFilePlayingControl(NAMEPLAY, 0, filename.getBytes()) != 0;
    }

    public boolean next(String filename) {
        return MediaPlayer.iRecFilePlayingControl(NAMEPLAY, 0, filename.getBytes()) != 0;
    }

    public void jump(int value) {
        MediaPlayer.iRecFilePlayingControl(JUMP, value, "test".getBytes());
    }

    public int fastPlay(int value) {
        return MediaPlayer.iRecFilePlayingControl(FASTPLAY, value, "fast".getBytes());
    }

    public int fastPlayCancel(int value) {
        return MediaPlayer.iRecFilePlayingControl(FASTPLAY_CANCEL, value, "fast".getBytes());
    }

    public int SetNoneState() {
        return MediaPlayer.iRecFilePlayingControl(NONE, 0, "none".getBytes());
    }

    protected abstract void onP2PViewSingleTap();
}
