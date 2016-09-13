package com.fans656.bridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends LastActivity {
    public final static String EXTRA_MESSAGE = "com.fans656.bridge.MESSAGE";
    public final static String TAG = "bridge";
    private WebView contentView;
    private String server_url;
    private String cur_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        server_url = pref.getString("pref_server_url", null);

        contentView = (WebView)findViewById(R.id.content_view);
        String id = getIntent().getStringExtra("id");
        if (id != null) {
            Log.d("bridgec", "onCreate cur_id == " + cur_id);
            cur_id = id;
            updateContent();
            Log.d("bridgec", "onCreate after updateContent(), cur_id == " + cur_id);
        } else {
            cur_id = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("bridgec", "onPause save cur_id == " + cur_id);
        SharedPreferences pref = getSharedPreferences("com.fans656.bridge.state", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("cur_id", cur_id);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cur_id == null) {
            SharedPreferences pref = getSharedPreferences("com.fans656.bridge.state", MODE_PRIVATE);
            cur_id = pref.getString("cur_id", cur_id);
            updateContent();
        }
        Log.d("bridgec", "onResume restore cur_id == " + cur_id);
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

    public void option(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void showList(View view) {
        showList();
    }

    public void showList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    public void randomSnippet(View view) {
        final MainActivity that = this;
        new NetworkTask(server_url)
                .addParameter("stmt", "random.choice(db.all())['id']")
                .done(new NetworkTask.Action() {
                    @Override
                    public void take(String result) {
                        if (!result.equals("null")) {
                            String v = result.substring(1, result.length() - 1);
                            Log.d("bridgec", v);
                            cur_id = Integer.parseInt(v) + "";
                            Log.d("bridgec", "random cur_id == " + cur_id);
                            updateContent();
                        }
                    }
                }).run();
    }

    public void nextSnippet(View view) {

    }

    public void prevSnippet(View view) {

    }

    public void updateContent(View view) {
        updateContent();
    }

    public void updateContent() {
        contentView.loadData("", "text/plain", null);
        String stmt = "db.search(q.id == '" + cur_id + "')[0]";
        Log.d("bridge", stmt);
        new NetworkTask(server_url)
                .addParameter("stmt", stmt)
                .done(new NetworkTask.Action() {
                    @Override
                    public void take(String result) {
                        Log.d("bridge", "result: " + result);
                        try {
                            JSONObject snippet = new JSONObject(result);
                            String content = snippet.getString("content");
                            contentView.loadData(content, "text/plain", null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).run();
    }
}
