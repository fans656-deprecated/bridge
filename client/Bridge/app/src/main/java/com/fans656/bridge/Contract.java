package com.fans656.bridge;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/9/13.
 */
public class Contract {
    public static class Snippet implements BaseColumns {
        public static final String TABLE_NAME = "snippet";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ABSTRACT = "abstract";
        public static final String COLUMN_NAME_CONTENT = "content";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE =
            "create table " + Snippet.TABLE_NAME + " ("
            + Snippet.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP
            + Snippet.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP
            + Snippet.COLUMN_NAME_ABSTRACT + TEXT_TYPE + ")";
    public static final String SQL_DESTROY =
            "drop table if exists " + Snippet.TABLE_NAME;
}
