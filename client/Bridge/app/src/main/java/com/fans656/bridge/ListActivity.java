package com.fans656.bridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class ListActivity extends LastActivity {
    ListView listView;
    ArrayAdapter adapter;
    String server_url;
    ArrayList<Snippet> arr = new ArrayList<>();

    class Snippet {
        public String ctime;
        public String id;
        @Override
        public String toString() {
            return ctime;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        server_url = pref.getString("pref_server_url", null);

        listView = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, R.layout.textview, arr);
        listView.setAdapter(adapter);
        final ListActivity that = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snippet snippet = (Snippet)parent.getAdapter().getItem(position);
                Intent intent = new Intent(that, MainActivity.class);
                intent.putExtra("id", snippet.id);
                startActivity(intent);
            }
        });
        updateList();
    }

    public void updateList(View view) {
        updateList();
    }

    public void updateList() {
        arr.clear();
        adapter.notifyDataSetChanged();
        new NetworkTask(server_url)
                .addParameter("stmt", "[{'ctime': t['ctime'], 'id': t['id']} for t in db.all()]")
                .done(new NetworkTask.Action() {
                    @Override
                    public void take(String result) {
                        JSONArray titles = null;
                        try {
                            titles = new JSONArray(result);
                            for (int i = 0; i < titles.length(); ++i) {
                                JSONObject snippet_json = titles.getJSONObject(i);
                                Snippet snippet = new Snippet();
                                snippet.ctime = snippet_json.getString("ctime");
                                snippet.id = snippet_json.getString("id");
                                arr.add(snippet);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).run();
    }
}
