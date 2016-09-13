package com.fans656.bridge;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/12.
 */
public class NetworkTask {
    NetworkTask(String url) {
        this.url = url;
    }
    public NetworkTask addParameter(String key, String value) {
        params.put(key, value);
        return this;
    }
    public interface Action {
        void take(String result);
    }
    public NetworkTask done(Action action) {
        this.action = action;
        return this;
    }
    public void run() {
        if (!params.isEmpty()) {
            url += "?";
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry)it.next();
                String key = null;
                String val = null;
                try {
                    key = URLEncoder.encode(pair.getKey(), "utf-8");
                    val = URLEncoder.encode(pair.getValue(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url += key + "=" + val;
            }
        }
        Task task = new Task();
        task.action = action;
        task.execute(url);
    }

    private String url;
    private Action action;
    private HashMap<String, String> params = new HashMap<>();

    private class Task extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("bridge", url);
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                try (InputStream in = new BufferedInputStream(conn.getInputStream())) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return result.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                conn.disconnect();
            }
            return "error";
        }
        @Override
        protected void onPostExecute(String result) {
            if (action != null) {
                action.take(result);
            }
        }
        public Action action;
    }
}
