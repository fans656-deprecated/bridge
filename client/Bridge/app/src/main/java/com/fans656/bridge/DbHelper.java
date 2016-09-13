package com.fans656.bridge;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/13.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "snippet.db";
    private static DbHelper instance = null;

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.SQL_CREATE);
        Log.d("bridge db", "create table");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contract.SQL_DESTROY);
        onCreate(db);
    }

    public interface Action {
        void take();
    }

    public String getContent(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select content from snippet where id = ?",
                new String[] {id});
        if (c.moveToFirst()) {
            return c.getString(0);
        } else {
            return "";
        }
    }
    public Pair<String, String> getRandomSnippet(String cur_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select id, content from "
                        + "(select * from snippet where id != ?) order by random() limit 1",
                new String[]{cur_id});
        c.moveToFirst();
        return new Pair<>(c.getString(0), c.getString(1));
    }
    public Pair<String, String> getPrevContent(String cur_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from "
                + "(select id, content from snippet where id < ? order by id desc limit 1)"
                + "union"
                + " select * from "
                + "(select id, content from snippet order by id desc limit 1)"
                + "order by id limit 1",
                new String[]{cur_id});
        c.moveToFirst();
        return new Pair<>(c.getString(0), c.getString(1));
    }
    public Pair<String, String> getNextSnippet(String cur_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from "
                        + "(select id, content from snippet where id > ? order by id limit 1)"
                        + "union"
                        + " select * from "
                        + "(select id, content from snippet order by id limit 1)"
                        + "order by id desc limit 1",
                new String[]{cur_id});
        c.moveToFirst();
        return new Pair<>(c.getString(0), c.getString(1));
    }

    public void sync(String server_url, final Action action) {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from snippet");
        new NetworkTask(server_url)
                .addParameter("stmt", "db.all()")
                .done(new NetworkTask.Action() {
                    @Override
                    public void take(String result) {
                        db.beginTransaction();
                        try {
                            JSONArray snippets = new JSONArray(result);
                            for (int i = 0; i < snippets.length(); ++i) {
                                JSONObject snippet = snippets.getJSONObject(i);
                                String id = snippet.getString("id");
                                String abstract_ = snippet.getString("abstract");
                                String content = snippet.getString("content");
                                Cursor c = db.rawQuery("select id from snippet where id = ?",
                                        new String[] {id}, null);
                                if (c.getCount() == 0) {
                                    db.execSQL("insert into snippet (id, abstract, content)"
                                                    + " values (?, ?, ?)",
                                            new String[] {id, abstract_, content});
                                } else {
                                    db.execSQL("update snippet set abstract = ?, content = ?"
                                            + "where id = ?", new String[] {
                                            abstract_, content, id
                                    });
                                }
                                c.close();
                            }
                            db.setTransactionSuccessful();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("bridge", "sync failed");
                        } finally {
                            db.endTransaction();
                        }
                        Log.d("bridge", "synced");
                        action.take();
                    }
                })
                .run();
    }
}
