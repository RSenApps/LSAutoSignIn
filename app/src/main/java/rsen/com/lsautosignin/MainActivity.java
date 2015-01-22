package rsen.com.lsautosignin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the login form.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final EditText mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(prefs.getString("email", ""));

        final EditText mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText(prefs.getString("password", ""));

       findViewById(R.id.force_sign_in).setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               if(isConnectedToWifi()) {
                   startService(new Intent(MainActivity.this, SignInService.class));
               }
               else {
                   Toast.makeText(MainActivity.this, "Not connected to wifi...", Toast.LENGTH_SHORT).show();
               }
           }
       });
        findViewById(R.id.update_info).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });
        findViewById(R.id.save_credentials).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                boolean cancel = false;
                View focusView = null;


                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password)) {
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                    focusView = mPasswordView;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    mEmailView.setError(getString(R.string.error_field_required));
                    focusView = mEmailView;
                    cancel = true;
                } else if (!isEmailValid(email)) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    focusView = mEmailView;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("email", mEmailView.getText().toString())
                            .putString("password", mPasswordView.getText().toString()).commit();
                    updateInfo();
                }


            }
        });

    }

    @Override
    protected void onResume() {
        updateInfo();
        super.onResume();
    }

    private void updateInfo()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ((TextView) findViewById(R.id.log)).setText(MyLog.getLog(this));
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd");
        Date date = new Date();
        TextView status = (TextView) findViewById(R.id.status);
        if (prefs.getString("lastSignIn", "").equals(dateFormat.format(date)))
        {
            status.setText(prefs.getString("lastMessage", "Signed In:" + dateFormat.format(date)));
        }
        else if (prefs.getString("email", "").equals(""))
        {
            status.setText("Not Configured");
        }
        else if (!isConnectedToWifi())
        {
            status.setText("Not connected to wifi");
        }
        else {
            status.setText("Not signed in yet...");
        }
    }
    private boolean isConnectedToWifi()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
        if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
            String ssid = wifiInfo.getSSID();
            if (ssid.contains("erehwon") || ssid.toLowerCase().contains("us student"))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}



