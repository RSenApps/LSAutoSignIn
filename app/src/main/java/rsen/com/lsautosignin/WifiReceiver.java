package rsen.com.lsautosignin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WifiReceiver extends BroadcastReceiver {
    public WifiReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd");
        Date date = new Date();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getString("email", "").equals("") && !prefs.getString("lastSignIn", "").equals(dateFormat.format(date))) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            boolean connected = false;
            if (info != null) {
                if (info.isConnectedOrConnecting()) {
                    // e.g. To check the Network Name or other info:
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    if (ssid.contains("erehwon") || ssid.toLowerCase().contains("us student")) {
                        context.startService(new Intent(context, SignInService.class));
                        connected = true;
                    }
                }
            }
            if (!connected) {
                context.stopService(new Intent(context, SignInService.class));
            }
        }
    }
}
