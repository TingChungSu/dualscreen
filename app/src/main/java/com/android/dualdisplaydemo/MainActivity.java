package com.android.dualdisplaydemo;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;

import com.google.gson.Gson;

import ftpdownload.FTPDownload;
import libs.FileIO;
import playlist.PlayList;
import pager.AutoScrollViewPager;
import pager.MyPagerAdapter;
import playlist.SourceData;
import webservice.HttpConnectionUtil;

public class MainActivity extends Activity {
    private boolean tmp_light_on = true;
    KeyguardManager mKeyguardManager;
    private KeyguardManager.KeyguardLock mKeyguardLock;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    public boolean isViewChange = false;
    public static final Lock _mutex = new ReentrantLock(true);
    public int downloadNow = 0;
    public int downloadNum = 0;
    private static final String TAG = "MainActivity";
    public final static String strLeftListPath = "/sdcard/Download/playListLeft.txt";
    public final static String strRightListPath = "/sdcard/Download/playListRight.txt";
    private final static String strConfigPath = "/sdcard/Download/config.txt";
    private final static String strCurrentListHistory = "/sdcard/Download/CurrentList.txt";
    private final static String strUpdateTime = "/sdcard/Download/UpdateTime.txt";
    private final static String strDownload = "/sdcard/Download/";

    private static int pageInterval = 3000;
    private AutoScrollViewPager myPager;
    private static Handler handler;

    public static PlayList myPlayListLeft;
    public static PlayList myPlayListRight;
    private Display[] mDisplays;
    private VideoView myVideo = null;
    public static TextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mWakeLock = mPowerManager.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");

        lightOnScreen();
        deleteAllFile(new File(strCurrentListHistory));
        myVideo = (VideoView) findViewById(R.id.myVideoView1);
        myPager = (AutoScrollViewPager) findViewById(R.id.myViewPager1);
        myTextView = (TextView) findViewById(R.id.myTextView);
        myTextView.setText("FILE Downloading...\ndevice imei: " + getDeviceImei() + "\ndevice mac: " + getDeviceMacAddress());
        handler = new MyHandler(myPager);
        myPlayListLeft = new PlayList();
        myPlayListRight = new PlayList();

/*
        setLocalFile();
        setExtendList();
        setDualScreen(myPlayListRight);
        MainActivity.sendMessage(0, 0);
        if (isDualScrren())
            ExtendPresentation.sendMessage(0, 0);
        */
        startWebService("QueryCurrentPlaylist");
        startWebService("QueryPlaylistLastUpdate");

        //setVideoFromSDcard();
        //setVideo(myVideo,mAniPath[0]);
        //setDualScreen();

    }

    private void setAutoScroll() {
        MainActivity.sendMessage(2, 0);
    }

    private void startWebService(String strMethod) {
        Map<String, String> mapProperty = new HashMap<String, String>();
        mapProperty.put("strParam", "{\"IMEI\":\"" + getDeviceImei() + "\"  , \"MacAddress\":\"" + getDeviceMacAddress() + "\"}");
        HttpConnectionUtil.asyncSendRequestT(strMethod,
                mapProperty, mHandler);
    }


    private void setExtendList() {
        //String[] mAniPath = {"/sdcard/Download/Asics_right.mp4"};
        //String[] mPicPath = {"/sdcard/Download/sample320/sample320_03.jpg", "/sdcard/Download/sample320/sample320_02.jpg", "/sdcard/Download/sample320/sample320_01.jpg"};
        String[] mPicPath = {"/sdcard/Download/sample320/sample320_01.jpg"};

        myPlayListRight = new PlayList();
        for (String path : mPicPath) {
            SourceData data = new SourceData("pic", path);
            myPlayListRight.addToBot(data);
        }
        /*for (String path : mAniPath) {
            SourceData data = new SourceData("ani", path);
            myPlayListRight.addToBot(data);
        }*/
    }


    private void setLocalFile() {
        String[] mAniPath = {"/sdcard/Download/Asics_left.mp4"};
        String[] mPicPath = {"/sdcard/Download/sample320/sample320_01.jpg", "/sdcard/Download/sample320/sample320_04.jpg", "/sdcard/Download/sample320/sample320_02.jpg"};

        myPlayListLeft = new PlayList();
        for (String path : mPicPath) {
            SourceData data = new SourceData("pic", path);
            myPlayListLeft.addToBot(data);
        }
        for (String path : mAniPath) {
            SourceData data = new SourceData("ani", path);
            myPlayListLeft.addToBot(data);
        }
        MyPagerAdapter myAdapter = new MyPagerAdapter(this, myPlayListLeft);
        myPager.setAdapter(myAdapter);
        //myPager.startAutoScroll();
    }

    private boolean isFileExist(String strPath) {
        File file = new File(strPath);
        return file.exists();
    }

    private void fuckOff() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private boolean isDualScrren() {

        DisplayManager mDisplayManager;
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

        return mDisplays.length > 1;
    }

    private void setDualScreen(PlayList left, PlayList right) {
        DisplayManager mDisplayManager;
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
        if (mDisplays.length > 1) {
            ExtendPresentation mDemoPresentation = new ExtendPresentation(this, mDisplays[1], left, right);
            mDemoPresentation.show();
        }
    }

    private void setVideo(final VideoView mVideo, String path) {
        File file = new File(path);
        if (file.exists()) {
            myPlayListLeft.setVideo(true);
            mVideo.setVideoPath(file.getAbsolutePath());
            mVideo.start();
            mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    _mutex.lock();
                    myPager.setVisibility(View.VISIBLE);
                    myPlayListLeft.setVideo(false);
                    if (!myPlayListRight.isVideoPlay()) {
                        MainActivity.sendMessage(5, 0);
                    }
                    _mutex.unlock();
                }
            });
        } else Toast.makeText(this, path + "\nfile not exists! ", Toast.LENGTH_SHORT).show();
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

    private String getDeviceImei() {
        TelephonyManager tM = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tM.getDeviceId().trim();
        return imei;
    }

    private String getDeviceMacAddress() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null)
            return wifiInfo.getMacAddress();
        else
            return null;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (tmp_light_on)
            lightOffScreen();
        else
            lightOnScreen();
        tmp_light_on = !tmp_light_on;
        return super.onTouchEvent(event);
    }

    private void lightOffScreen() {
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock.acquire();
        mWakeLock.release();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = 0;
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void lightOnScreen() {
        //light up
        mWakeLock.acquire();
        // unlock
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        mKeyguardLock.disableKeyguard();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 80;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
            SourceData data;
            switch (msg.what) {
                case 0:// initial case
                    data = myPlayListLeft.getList().get(myPlayListLeft.getCurrentIndex());
                    if (data.isVedio()) {
                        setVideo(myVideo, data.getPath());
                        switchToVideo();
                    } else if (data.isImage()) {
                        switchToPager();
                        myPlayListRight.playNext();
                        ExtendPresentation.sendMessage(1, data.getIntPauseTime());
                        MainActivity.sendMessage(1, data.getIntPauseTime());
                    }
                    break;
                case 1:// change view
                    if (!isViewChange)
                        return;
                    data = myPlayListLeft.getList().get(myPlayListLeft.getCurrentIndex());
                    if (myPlayListLeft.isVideoPlay())
                        break;

                    if (myPlayListRight.isVideoPlay() && myPlayListLeft.getNextIndex() > myPlayListRight.getCurrentIndex()) {
                        /*if (myPlayListLeft.getNextIndex() == 0 && myPlayListRight.getCurrentIndex() == 0)
                            ;
                        else
                        */
                        break;
                    }
                    if (myPlayListLeft.getNextIndex() == 0 && myPlayListRight.getCurrentIndex() != 0) {
                        if (!myPlayListRight.isVideoPlay()) {
                            myPlayListRight.playNext();
                            ExtendPresentation.sendMessage(1, data.getIntPauseTime());
                        }
                        if (myPlayListRight.getNextIndex() != 0) {
                            MainActivity.sendMessage(1, data.getIntPauseTime());
                            break;
                        }
                    }
                    myPlayListLeft.playNext();
                    data = myPlayListLeft.getList().get(myPlayListLeft.getCurrentIndex());
                    if (!myPlayListLeft.getList().get(myPlayListLeft.getCurrentIndex()).hasFile()) {
                        myPlayListLeft.setPause(true);
                        myPlayListRight.playNext();
                        ExtendPresentation.sendMessage(1, data.getIntPauseTime());
                        MainActivity.sendMessage(1, data.getIntPauseTime());
                        break;
                    }
                    myPager.getScroller().setScrollDurationFactor(1);
                    myPager.scrollOnce();
                    myPager.getScroller().setScrollDurationFactor(1);
                    if (data.isVedio()) {
                        setVideo(myVideo, data.getPath());
                        switchToVideo();
                    } else if (data.isImage()) {
                        switchToPager();
                        myPlayListRight.playNext();
                        ExtendPresentation.sendMessage(1, data.getIntPauseTime());
                        MainActivity.sendMessage(1, data.getIntPauseTime());
                    }
                    break;
                case 2:// download succ
                    isViewChange = false;
                    setPlayListFile();
                    MyPagerAdapter myAdapter = new MyPagerAdapter(MainActivity.this, myPlayListLeft);
                    myPager.setAdapter(myAdapter);
                    //myPager.setOffscreenPageLimit(3);
                    setDualScreen(myPlayListLeft, myPlayListRight);
                    startWebService("QueryPlaylist");
                    if (isDualScrren())
                        ExtendPresentation.sendMessage(0, 0);
                    MainActivity.sendMessage(0, 0);
                    break;
                case 3:// download succ
                    setTextView(myTextView, "FILE Downloading, " + downloadNow + "/" + downloadNum + "...");
                    break;

                case 4:// download fail
                    setTextView(myTextView, "FILE Downloading Fail, retrying");
                    break;

                case 5:// extend screen change
                    if (!isViewChange)
                        return;
                    myPlayListRight.playNext();
                    ExtendPresentation.sendMessage(1, 0);
                    MainActivity.sendMessage(1, 0);
                    break;

                default:
                    break;
            }
        }
    }

    private void setTextView(TextView tv, String str) {
        tv.setText(str);
    }

    private void setPlayListFile() {
        FileIO.writeFile(strLeftListPath, myPlayListLeft.toJson());
        FileIO.writeFile(strRightListPath, myPlayListRight.toJson());
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            String method = bundle.getString("method");

            //Toast.makeText(MainActivity.this,response, Toast.LENGTH_LONG).show();
            try {
                if (response == null) {
                    Toast.makeText(MainActivity.this, "Reconnecting, PLEASE CHECK WIFI CONNECT", Toast.LENGTH_LONG).show();
                    startWebService("QueryCurrentPlaylist");
                    return;
                }
                Map<String, Object> map = null;
                Set<String> keys = null;
                assert method != null;
                if (method.equals("QueryCurrentPlaylist")) {
                    Toast.makeText(MainActivity.this,"Current",Toast.LENGTH_LONG).show();
                    File fileHistory = new File(strCurrentListHistory);
                    if (!fileHistory.exists()) {
                        FileIO.writeFile(strCurrentListHistory, response);
                    } else {
                        String str = FileIO.readFile(strCurrentListHistory).trim();
                        if (str.equals(response)) {/*
                            String strLeftJson = FileIO.readFile(strLeftListPath);
                            myPlayListLeft = new PlayList(strLeftJson);
                            String strRightJson = FileIO.readFile(strRightListPath);
                            myPlayListRight = new PlayList(strRightJson);
                            setAutoScroll();*/
                            return;
                        }
                    }
                    Toast.makeText(MainActivity.this,"Current Different",Toast.LENGTH_LONG).show();
                    isViewChange = true;
                    //deleteAllFile(new File(strDownload));
                    //createFolder(strDownload);
                    FileIO.writeFile(strCurrentListHistory, response);

                    map = new Gson().fromJson(response,
                            Map.class);
                    keys = map.keySet();
                    if (keys.contains("ReturnMessage")) {
                        String strJson = map.get("ReturnMessage").toString();
                        strJson = strJson.replace('[', ' ').replace(']', ' ').trim();
                        String[] cut = strJson.split("[}][,]");
                        downloadNow = 0;
                        downloadNum = cut.length;
                        int left = 1, right = 1;
                        PlayList tmpLeft = new PlayList();
                        PlayList tmpRight = new PlayList();
                        for (String str : cut) {
                            str = str.trim();
                            if (str == "") {
                                setTextView(myTextView, "There is no data to display\ndevice imei: " +
                                        getDeviceImei() + "\nMacAddress: " + getDeviceMacAddress());
                                //FileIO.writeFile(strListPath, myPlayListLeft.toJson());
                                return;
                            }
                            if (str.charAt(str.length() - 1) != '}')
                                str = str + "}";
                            String format = "yyyy-MM-dd'T'HH:mm:ss";
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            str = str.substring(str.indexOf("StartTime"));
                            str = str.substring(str.indexOf("="));
                            String strStartTime = str.substring(1, str.indexOf(","));
                            Date startDate = sdf.parse(strStartTime);
                            str = str.substring(str.indexOf("EndTime"));
                            str = str.substring(str.indexOf("="));
                            String strEndTime = str.substring(1, str.indexOf(","));
                            Date endDate = sdf.parse(strEndTime);
                            str = str.substring(str.indexOf("CompanyNo"));
                            str = str.substring(str.indexOf("="));
                            String strCompanyNo = str.substring(1, str.indexOf(","));
                            str = str.substring(str.indexOf("PlayCode"));
                            str = str.substring(str.indexOf("="));
                            String strPlayCode = str.substring(1, str.indexOf(","));
                            str = str.substring(str.indexOf("FileSeq"));
                            str = str.substring(str.indexOf("="));
                            String strFileSeq = str.substring(1, str.indexOf(","));
                            str = str.substring(str.indexOf("Filename"));
                            str = str.substring(str.indexOf("="));
                            String strFilename = str.substring(1, str.indexOf(","));
                            str = str.substring(str.indexOf("FileType"));
                            str = str.substring(str.indexOf("="));
                            String strFileType = str.substring(1, str.indexOf(","));
                            str = str.substring(str.indexOf("LCDNo"));
                            str = str.substring(str.indexOf("="));
                            String strLCDNo = str.substring(1, str.indexOf(","));

                            String strIneed = strCompanyNo + "/" + strPlayCode + "/" + strFilename;

                            SourceData data = new SourceData(strFileType, strDownload + strIneed, strLCDNo);
                            if (strLCDNo.equals("1.0")) {
                                while (right++ < (int) Double.parseDouble(strFileSeq))
                                    tmpRight.addToBot(new SourceData());

                                tmpRight.addToBot(data);
                            } else {
                                while (left++ < (int) Double.parseDouble(strFileSeq))
                                    tmpLeft.addToBot(new SourceData());

                                tmpLeft.addToBot(data);
                            }
                            createFolder(strDownload + "/" + strCompanyNo + "/");
                            createFolder(strDownload + "/" + strCompanyNo + "/" + strPlayCode + "/");
                            FTPdownLoadCurrent(strIneed, strFilename);
                            Toast.makeText(MainActivity.this, startDate + "\n" + endDate, Toast.LENGTH_SHORT).show();
                        }

                        myPlayListLeft = new PlayList(tmpLeft);
                        myPlayListRight = new PlayList(tmpRight);
                    }
                } else if (method.equals("QueryPlaylist")) {
                    map = new Gson().fromJson(response, Map.class);
                    keys = map.keySet();
                    if (keys.contains("ReturnMessage")) {
                        String strJson = map.get("ReturnMessage").toString();

                        strJson = strJson.replace('[', ' ').replace(']', ' ').trim();
                        String[] cut = strJson.split("[}][,]");

                        for (String str : cut) {
                            str = str.trim();
                            if (str == "") {
                                return;
                            }
                            if (str.charAt(str.length() - 1) != '}')
                                str = str + "}";

                            str = str.substring(str.indexOf("CompanyNo"));
                            str = str.substring(str.indexOf("="));
                            String strCompanyNo = str.substring(1, str.indexOf(","));
                            str = str.substring(str.indexOf("PlayCode"));
                            str = str.substring(str.indexOf("="));
                            String strPlayCode = str.substring(1, str.indexOf(","));
                            str = str.substring(str.indexOf("Filename"));
                            str = str.substring(str.indexOf("="));
                            String strFilename = str.substring(1, str.indexOf(","));

                            String strIneed = strCompanyNo + "/" + strPlayCode + "/" + strFilename;

                            createFolder(strDownload + "/" + strCompanyNo + "/");
                            createFolder(strDownload + "/" + strCompanyNo + "/" + strPlayCode + "/");
                            FTPdownLoadAll(strIneed, strFilename);
                        }
                    }
                } else if (method.equals("QueryPlaylistLastUpdate")) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startWebService("QueryPlaylistLastUpdate");
                            startWebService("QueryCurrentPlaylist");
                            //Do after 30s
                        }
                    }, 5000);
                    File fileHistory = new File(strUpdateTime);
                    if (!fileHistory.exists()) {
                        FileIO.writeFile(strUpdateTime, response);
                    } else {
                        String str = FileIO.readFile(strUpdateTime).trim();
                        if (str.equals(response))
                            return;
                        isViewChange = true;
                        deleteAllFile(new File(strDownload));
                        createFolder(strDownload);
                        FileIO.writeFile(strCurrentListHistory, response);

                    }

                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                startWebService("QueryPlaylist");
                Toast.makeText(MainActivity.this,
                        "Reconned Web service", Toast.LENGTH_LONG).show();
            }
        }
    };


    private void FTPdownLoadCurrent(final String remotefile, final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String remotefilename = "/" + remotefile;
                    File dirFile = new File(strDownload + remotefile);
                    if (!dirFile.exists()) {
                        if (FTPDownload.downloadAndSaveFile(remotefilename, strDownload + remotefile)) {
                            _mutex.lock();
                            downloadNow++;
                            MainActivity.sendMessage(3, 0);
                            if (downloadNum == downloadNow) {
                                setAutoScroll();
                            }
                            _mutex.unlock();
                        } else {
                            MainActivity.sendMessage(4, 0);
                            FTPdownLoadCurrent(remotefile, fileName);
                        }
                    } else {
                        _mutex.lock();
                        downloadNow++;
                        MainActivity.sendMessage(3, 0);
                        if (downloadNum == downloadNow) {
                            setAutoScroll();
                        }
                        _mutex.unlock();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void FTPdownLoadAll(final String remotefile, final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String remotefilename = "/" + remotefile;
                    File dirFile = new File(strDownload + remotefile);
                    if (!dirFile.exists()) {
                        if (FTPDownload.downloadAndSaveFile(remotefilename, strDownload + remotefile)) {
                        } else {
                            MainActivity.sendMessage(4, 0);
                            FTPdownLoadCurrent(remotefile, fileName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deleteAllFile(File path) {
        if (!path.exists()) {
            return;
        }
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFile(files[i]);
        }
        path.delete();
    }

    private void createFolder(String path) {
        //make sure U can SDcard read/write
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return;
        File dirFile = new File(path);
        if (!dirFile.exists()) {  //if folder not exitst
            dirFile.mkdir();    //create folder
        }
    }
}
