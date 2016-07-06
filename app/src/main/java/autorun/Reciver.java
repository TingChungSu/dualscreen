package autorun;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.android.dualdisplaydemo.MainActivity;

/**
 * Created by 鼎鈞 on 2016/6/26.
 */
public class Reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);

            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            //Light on screen
            wl.acquire();

            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
            keyguardLock.disableKeyguard();
            // release screen listener
            wl.release();
        /*
        // start service
            Intent serviceIntent = new Intent(context,BootService.class);
            context.startService(serviceIntent);
        */
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);

        }
    }
}
