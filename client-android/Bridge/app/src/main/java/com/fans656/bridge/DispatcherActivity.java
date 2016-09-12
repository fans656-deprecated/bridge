package com.fans656.bridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher);

        // save last view
        Class<?> activityClass;
        SharedPreferences pref = getSharedPreferences(
                getString(R.string.pref_last_activity), MODE_PRIVATE);
        String lastActivity = pref.getString("lastActivity", null);
        boolean ok = false;
        if (lastActivity != null) {
            try {
                activityClass = Class.forName(lastActivity);
            } catch (ClassNotFoundException e) {
                activityClass = null;
            }
            if (activityClass != null) {
                startActivity(new Intent(this, activityClass));
                ok = true;
            }
        }
        if (!ok) {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
