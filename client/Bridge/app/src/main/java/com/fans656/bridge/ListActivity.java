package com.fans656.bridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

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
        public String abstract_;
        public String id;
        public Snippet(String id, String abstract_) {
            this.id = id;
            this.abstract_ = abstract_;
        }
        @Override
        public String toString() {
            return abstract_;
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
                finish();
            }
        });
        updateList();
    }

    public void updateList(View view) {
        DbHelper.getInstance(this).sync(server_url, new DbHelper.Action() {
            @Override
            public void take() {
                updateList();
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateList() {
        arr.clear();
        SQLiteDatabase db = DbHelper.getInstance(this).getReadableDatabase();
        Cursor c = db.rawQuery("select id, abstract from snippet", null);
        while (c.moveToNext()) {
            arr.add(new Snippet(
                    c.getString(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("abstract"))));
        }
        c.close();
        adapter.notifyDataSetChanged();
    }
}
