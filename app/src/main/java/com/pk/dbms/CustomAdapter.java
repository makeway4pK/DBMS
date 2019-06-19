package com.pk.dbms;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.pk.dbms.MainActivity.adapterA;
import static com.pk.dbms.MainActivity.adapterS;
import static com.pk.dbms.MainActivity.adapterV;
import static com.pk.dbms.MainActivity.db;
import static com.pk.dbms.MainActivity.populateList;
import static com.pk.dbms.MainActivity.temp;

class CustomAdapter extends ArrayAdapter<Record> {
    boolean main;

    public CustomAdapter(Context context, ArrayList<Record> resource, boolean toAccessMainDB) {
        super(context, 0, resource);
        main = toAccessMainDB;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.record_lay, parent, false);

        convertView.findViewById(R.id.btn_del).setVisibility(View.GONE);
        convertView.findViewById(R.id.btn_mod).setVisibility(View.GONE);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button mod = view.findViewById(R.id.btn_mod);

                if (mod.getVisibility() == View.GONE) {
                    mod.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.btn_del).setVisibility(View.VISIBLE);
                } else {
                    mod.setVisibility(View.GONE);
                    view.findViewById(R.id.btn_del).setVisibility(View.GONE);
                }
            }
        });


        Cursor item = getItem(position).getCursor(main);
        if (!item.moveToFirst()) return convertView;
        TextView tv;

        final String it_id = item.getString(0);
        tv = convertView.findViewById(R.id.rec_f1);
        tv.setText(it_id);
        tv = convertView.findViewById(R.id.rec_f2);
        tv.setText(item.getString(1));
        tv = convertView.findViewById(R.id.rec_f3);
        tv.setText(item.getString(2));

        convertView.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (main) db.execSQL("DELETE FROM store WHERE id=" + it_id + ";");
                else
                    temp.execSQL("DELETE FROM tempStock WHERE id=" + it_id + ";");

                populateList(adapterV,main);
                populateList(adapterA,main);
                populateList(adapterS,main);

            }
        });
        convertView.findViewById(R.id.btn_mod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder modifier = new AlertDialog.Builder(getContext());
                modifier.setTitle("Modify Record");
                View lay = LayoutInflater.from(getContext()).inflate(R.layout.modifydialog, null, false);
                final EditText et1 = lay.findViewById(R.id.modField1),
                        et2 = lay.findViewById(R.id.modField2),
                        et3 = lay.findViewById(R.id.modField3);
                Cursor sel;
                if (main) sel = db.rawQuery("SELECT * FROM store WHERE id=" + it_id, null);
                else sel = temp.rawQuery("SELECT * FROM tempStock WHERE id=" + it_id, null);
                if (!sel.moveToFirst()) return;
                et1.setText(it_id);
                et2.setText(sel.getString(1));
                et3.setText(sel.getString(2));
                modifier.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newId = et1.getText().toString().trim();
                        if (newId.length() <= 0 ||
                                et2.getText().toString().trim().length() <= 0 ||
                                et3.getText().toString().trim().length() <= 0) {
                            Toast.makeText(getContext(), "Empty fields not allowed!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!newId.trim().equals(it_id.trim()))
                            if (db.rawQuery("SELECT * FROM store WHERE id=" + newId, null).moveToFirst() || temp.rawQuery("SELECT * FROM tempStock WHERE id=" + newId, null).moveToFirst()) {
                                Toast.makeText(getContext(), "DB entry with that ID\nalready exists", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        if (main)
                            db.execSQL("UPDATE store SET id=" + et1.getText() + ",name='" + et2.getText() +
                                    "',stock='" + et3.getText() + "' WHERE id=" + it_id + "");
                        else
                            temp.execSQL("UPDATE tempStock SET id=" + et1.getText() + ",name='" + et2.getText() +
                                    "',stock='" + et3.getText() + "' WHERE id=" + it_id + "");
                        Toast.makeText(getContext(), "Record was successfully modified", Toast.LENGTH_SHORT).show();
                        populateList(adapterV,main);
                        populateList(adapterA,main);
                        populateList(adapterS,main);
                        return;
                    }
                });
                modifier.setView(lay);
                modifier.setCancelable(true);
                modifier.show();
            }
        });

        return convertView;
    }

}
