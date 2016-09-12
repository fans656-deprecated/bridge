package com.fans656.bridge;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2016/9/9.
 */
public class LastActivity extends AppCompatActivity {
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = getSharedPreferences(
                getString(R.string.pref_last_activity), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.commit();
    }
}
