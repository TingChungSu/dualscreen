package com.android.dualdisplaydemo;

import android.app.Presentation;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.widget.VideoView;

import java.io.File;
import java.lang.ref.WeakReference;

import pager.AutoScrollViewPager;
import pager.MyPagerAdapter;
import playlist.PlayList;

/**
 * Created by 鼎鈞 on 2016/7/7.
 */
final class ExtendPresentation extends Presentation {

    private String[] mVideoPath = {"/sdcard/Download/Asics_right.mp4", "/sdcard/Download/Asics_left.mp4"};
    private int mVideoIndex = 0;
    private String tmp_videoPath;
    public static PlayList myPlayListLeft = null;
    public static PlayList myPlayListRight = null;
    private VideoView myVideo = null;
    private AutoScrollViewPager myPager;
    private static Handler handler;

    public ExtendPresentation(Context context, Display display, String strPath) {
        super(context, display);
        tmp_videoPath = strPath;
    }

    public ExtendPresentation(Context context, Display display, PlayList left, PlayList right) {
        super(context, display);
        myPlayListLeft = left;
        myPlayListRight = right;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend);
        myVideo = (VideoView) findViewById(R.id.myVideoView2);
        myPager = (AutoScrollViewPager) findViewById(R.id.myViewPager2);
        handler = new MyHandler(myPager);

        if (myPlayListRight == null) {
            setVideo(myVideo, tmp_videoPath);
            switchToVideo();
        } else {
            MyPagerAdapter myAdapter = new MyPagerAdapter(getContext(), myPlayListRight);
            myPager.setAdapter(myAdapter);
            //ExtendPresentation.sendMessage(0, 0);
        }
    }

    private void switchToPager() {
        if (myPager != null)
            myPager.setVisibility(View.VISIBLE);
        if (myVideo != null)
            myVideo.setVisibility(View.GONE);
    }

    private void switchToVideo() {
        if (myPager != null)
            myPager.setVisibility(View.GONE);
        if (myVideo != null)
            myVideo.setVisibility(View.VISIBLE);
    }


    private void setVideo(final VideoView mVideo, String path) {
        File file = new File(path);
        if (file.exists()) {
            myPlayListRight.setVideo(true);
            mVideo.setVideoPath(file.getAbsolutePath());
            mVideo.start();
            switchToVideo();
            mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    //switchToPager();
                    MainActivity._mutex.lock();
                    myPlayListRight.setVideo(false);
                    if(!myPlayListLeft.isVideoPlay())
                        MainActivity.sendMessage(5, 0);
                    MainActivity._mutex.unlock();

                }
            });
        } else ;
    }

    public static void sendMessage(int msg, long delayTimeInMills) {
        handler.removeMessages(msg);
        handler.sendEmptyMessageDelayed(msg, delayTimeInMills);
    }

    private class MyHandler extends Handler {

        private final WeakReference<AutoScrollViewPager> autoScrollViewPager;

        public MyHandler(AutoScrollViewPager autoScrollViewPager) {
            this.autoScrollViewPager = new WeakReference<AutoScrollViewPager>(autoScrollViewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:// initial case
                    if (myPlayListRight.getList().get(myPlayListRight.getCurrentIndex()).isVedio()) {
                        setVideo(myVideo, myPlayListRight.getList().get(myPlayListRight.getCurrentIndex()).getPath());
                        switchToVideo();
                    } else if (myPlayListRight.getList().get(myPlayListRight.getCurrentIndex()).isImage()) {
                        switchToPager();
                        AutoScrollViewPager pager = this.autoScrollViewPager.get();
                        if (pager != null) {
                            //ExtendPresentation.sendMessage(1, 5000);
                        }
                    }
                    break;
                case 1:// change view
                    if(myPlayListRight.isVideoPlay()){
                        myPlayListRight.playLast();
                        break;
                    }
                    if(myPlayListRight.getCurrentIndex()==0 && myPlayListLeft.getNextIndex()!=0){
                        myPlayListRight.playLast();
                        break;
                    }
                    if (!myPlayListRight.getList().get(myPlayListRight.getCurrentIndex()).hasFile()) {
                        //myPlayListRight.setPause(true);
                        break;
                    }
                    if (myPlayListLeft.isPause()) {
                        //myPlayListLeft.setPause(false);
                        //MainActivity.sendMessage(1, 0);
                    }

                    AutoScrollViewPager pager = this.autoScrollViewPager.get();
                    if (pager != null) {
                        myPager.getScroller().setScrollDurationFactor(1);
                        myPager.scrollOnce();
                        myPager.getScroller().setScrollDurationFactor(1);
                        if (myPlayListRight.getList().get(myPlayListRight.getCurrentIndex()).isVedio()) {
                            setVideo(myVideo, myPlayListRight.getList().get(myPlayListRight.getCurrentIndex()).getPath());
                            switchToVideo();
                        } else if (myPlayListRight.getList().get(myPlayListRight.getCurrentIndex()).isImage()) {
                            switchToPager();
                            if (pager != null) {
                                //ExtendPresentation.sendMessage(1, 5000);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

}