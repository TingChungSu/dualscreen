package com.android.dualdisplaydemo;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private String[] mVideoPath = {"/sdcard/Download/Asics_left.mp4", "/sdcard/Download/Asics_right.mp4"};
    private int mVideoIndex = 0;
    private Display[] mDisplays;//屏幕数组
    private VideoView vv1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lightOnScreen();
        setContentView(R.layout.activity_main);

        vv1 = (VideoView) findViewById(R.id.myVideoView1);
        setVideo(vv1, mVideoPath[mVideoIndex]);

        DisplayManager mDisplayManager; //屏幕管理类
        mDisplayManager = (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        mDisplays = mDisplayManager.getDisplays();
        Log.i(TAG, "displays: " + mDisplays.length);

        View view = findViewById(R.id.fullscreen_content);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        DemoPresentation mDemoPresentation = new DemoPresentation(this, mDisplays[1]); //displays[1]是副屏
        mDemoPresentation.show();
    }


    private void setVideo(final VideoView mVideo, String path) {
        File file = new File(path);
        if (file.exists()) {
            mVideo.setVideoPath(file.getAbsolutePath());
            mVideo.start();
            mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mVideoIndex++;
                    if (mVideoIndex >= mVideoPath.length)
                        mVideoIndex = 0;
                    setVideo(mVideo, mVideoPath[mVideoIndex]);
                }
            });
        } else ;
    }

    private void lightOnScreen(){
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
        keyguardLock.disableKeyguard();
    }


    private final class DemoPresentation extends Presentation {

        private String[] mVideoPath = {"/sdcard/Download/Asics_right.mp4", "/sdcard/Download/Asics_left.mp4"};
        private int mVideoIndex = 0;
        public DemoPresentation(Context context, Display display) {
            super(context, display);
        }

        private VideoView vv2 = null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_extend);
            vv2 = (VideoView) findViewById(R.id.myVideoView2           );
            setVideo(vv2, mVideoPath[mVideoIndex]);
        }


        private void setVideo(final VideoView mVideo, String path) {
            File file = new File(path);
            if (file.exists()) {
                mVideo.setVideoPath(file.getAbsolutePath());
                mVideo.start();
                mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mVideoIndex++;
                        if (mVideoIndex >= mVideoPath.length)
                            mVideoIndex = 0;
                        setVideo(mVideo, mVideoPath[mVideoIndex]);
                    }
                });
            } else ;
        }
    }
}
