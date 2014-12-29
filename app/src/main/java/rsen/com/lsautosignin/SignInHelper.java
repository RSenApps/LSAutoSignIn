package rsen.com.lsautosignin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ryan on 12/28/2014.
 */
public class SignInHelper {
    public static boolean signIn(Context context) {
        try {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            URL url = new URL("https://maroon.lakesideschool.org/attendancesignin/signinform.aspx");
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(prefs.getString("email", ""), prefs.getString("password", "").toCharArray());

                }

            });
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try
            {
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    String result = readStream(url.openStream());
                    Document doc = Jsoup.parse(result);
                    String response = doc.getElementById("lblResults").text();
                    if (response.contains("Signed In"))
                    {
                        MyLog.l(response, context);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd");
                        Date date = new Date();
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("lastSignIn", dateFormat.format(date)).putString("lastMessage", response).commit();
                        urlConnection.disconnect();
                        return true;
                    }
                    else {
                        MyLog.l("Sign In Failed. Received: " + response, context);
                    }

                }
                else {
                    MyLog.l("Sign In Failed because request returned a " + urlConnection.getResponseCode(), context);
                }
            }

            finally

            {
                urlConnection.disconnect();
            }
        }
        catch (Exception e)
        {}
        return false;
    }
    private static String readStream(InputStream in) {
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
