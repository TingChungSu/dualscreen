package autorun;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;



/**
 * Created by 鼎鈞 on 2016/6/26.
 */
public class BootService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
