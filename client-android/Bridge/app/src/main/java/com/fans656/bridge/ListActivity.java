package com.fans656.bridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

public class ListActivity extends LastActivity {
    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        for (int i = 0; i < 30; ++i) {
            arr.add("foo " + i);
        }
        listView = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, R.layout.textview, arr);
        listView.setAdapter(adapter);
        final ListActivity that = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String)parent.getAdapter().getItem(position);
                Log.d("bridge", s);

                Intent intent = new Intent(that, MainActivity.class);
                intent.putExtra("show_text", s);
                startActivity(intent);
            }
        });
    }

    public void updateList(View view) {
        arr.clear();
        Random rand = new Random();
        int beg = rand.nextInt();
        for (int i = 0; i < 30; ++i) {
            arr.add("foo " + i + " - " + beg);
        }
        adapter.notifyDataSetChanged();
    }
}
