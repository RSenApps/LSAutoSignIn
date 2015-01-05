package rsen.com.lsautosignin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
                .setSmallIcon(R.drawable.ic_launcher)
                .build());
        Toast.makeText(this, "Signing in...", Toast.LENGTH_LONG).show();
        new Thread(signIn).start();
        return super.onStartCommand(intent, flags, startId);
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            new Thread(signIn).start();
            return true;
        }
    });
    Runnable signIn = new Runnable() {
        @Override
        public void run() {
            if (SignInHelper.signIn(SignInService.this))
            {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        notificationManager.notify(2, new Notification.Builder(SignInService.this)
                                .setContentTitle("Sign In Successful")
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentText(PreferenceManager.getDefaultSharedPreferences(SignInService.this).getString("lastMessage", ""))
                                .build());
                        stopSelf();
                    }
                };
                handler.post(runnable);
            }
            else {
                handler.sendEmptyMessageDelayed(0, 10000);
            }
        }
    };

    @Override
    public void onDestroy() {
        notificationManager.cancel(1);
        handler.removeMessages(0);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
