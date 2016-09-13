package com.fans656.bridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

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
            cur_id = id;
            loadContent(id);
        } else {
            cur_id = null;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences pref = getSharedPreferences("com.fans656.bridge.state", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("cur_id", cur_id);
        editor.apply();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (cur_id == null) {
            SharedPreferences pref = getSharedPreferences("com.fans656.bridge.state", MODE_PRIVATE);
            cur_id = pref.getString("cur_id", cur_id);
            loadContent(cur_id);
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
        Pair<String, String> snippet = DbHelper.getInstance(this).getRandomSnippet(cur_id);
        setContent(snippet);
    }
    public void nextSnippet(View view) {
        Pair<String, String> snippet = DbHelper.getInstance(this).getNextSnippet(cur_id);
        setContent(snippet);
    }
    public void prevSnippet(View view) {
        Pair<String, String> snippet = DbHelper.getInstance(this).getPrevContent(cur_id);
        setContent(snippet);
    }
    public void loadContent(String id) {
        String content = DbHelper.getInstance(this).getContent(id);
        setContent(new Pair<>(id, content));
    }
    public void setContent(Pair<String, String> snippet) {
        cur_id = snippet.first;
        String content = snippet.second;
        String html = "<html>"
                + "<head><meta charset=\"utf-8\" />"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />"
                + "</head>"
                + "<body>"
                + content.replace("\n", "<br>")
                + "</body>"
                + "</html>";
        contentView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
    }
    public void sync(View view) {
        final MainActivity that = this;
        DbHelper.getInstance(this).sync(server_url, new DbHelper.Action() {
            @Override
            public void take() {
                String content = DbHelper.getInstance(that).getContent(cur_id);
                setContent(new Pair<>(cur_id, content));
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
