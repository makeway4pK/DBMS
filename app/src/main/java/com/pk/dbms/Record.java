package com.pk.dbms;

import android.database.Cursor;

import static com.pk.dbms.MainActivity.db;
import static com.pk.dbms.MainActivity.temp;

public class Record {
    String key;

    public Record(Cursor c) {
        key = c.getString(0);
    }

    public Cursor getCursor(boolean mainDB) {
        if (mainDB)
            return db.rawQuery("SELECT * FROM store WHERE id=" + key, null);
        else
            return temp.rawQuery("SELECT * FROM tempStock WHERE id=" + key , null);

    }

}
