package rsen.com.lsautosignin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SignInService extends Service {
    NotificationManager notificationManager;
    public SignInService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, new Notification.Builder(this)
                .setContentTitle("Signing in...")
                .build());
        Toast.makeText(this, "Signing in...", Toast.LENGTH_LONG).show();
        signIn.run();
        return super.onStartCommand(intent, flags, startId);
    }
    Handler handler = new Handler();
    Runnable signIn = new Runnable() {
        @Override
        public void run() {
            if (SignInHelper.signIn(SignInService.this))
            {
                notificationManager.notify(2, new Notification.Builder(SignInService.this)
                        .setContentTitle("Sign In Successful")
                        .setContentText(PreferenceManager.getDefaultSharedPreferences(SignInService.this).getString("lastMessage", ""))
                        .build());
                stopSelf();
            }
            else {
                handler.postDelayed(signIn, 10000);
            }
        }
    };

    @Override
    public void onDestroy() {
        notificationManager.cancel(1);
        handler.removeCallbacks(signIn);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
