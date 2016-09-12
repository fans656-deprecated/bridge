package com.fans656.bridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends LastActivity {
    public final static String EXTRA_MESSAGE = "com.fans656.bridge.MESSAGE";
    public final static String TAG = "bridge";
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.edit_message);
        String show_text = getIntent().getStringExtra("show_text");
        if (show_text != null) {
            editText.setText(show_text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void showList(MenuItem item) {
        showList();
    }

    public void showList(View view) {
        showList();
    }

    public void showList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    public void queryAll(View view) {
        queryAll();
    }

    public void queryAll() {
        editText.setText("");
        new QueryListTask().execute("http://23.83.243.37/bridge/query");
    }

    private class QueryListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
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
            editText.setText(result);
        }
    }
}
