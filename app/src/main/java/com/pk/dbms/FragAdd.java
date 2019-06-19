package com.pk.dbms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.pk.dbms.MainActivity.adapterA;
import static com.pk.dbms.MainActivity.adapterV;
import static com.pk.dbms.MainActivity.db;
import static com.pk.dbms.MainActivity.populateList;
import static com.pk.dbms.MainActivity.temp;

class FragAdd extends Fragment {
    ListView addList;
    EditText editF1, editF2, editF3;
//    SQLiteDatabase temp;


    public FragAdd() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View lay = inflater.inflate(R.layout.frag_new, container, false);
        addList = lay.findViewById(R.id.list3);
        addList.setAdapter(adapterA);


        editF1 = lay.findViewById(R.id.addField1);
        editF2 = lay.findViewById(R.id.addField2);
        editF3 = lay.findViewById(R.id.addField3);
        editF3.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String newId = editF1.getText().toString().trim();
                if (newId.length() <= 0 ||
                        editF2.getText().toString().trim().length() <= 0 ||
                        editF3.getText().toString().trim().length() <= 0) {
                    Toast.makeText(getActivity(), "Empty fields not allowed!", Toast.LENGTH_SHORT).show();
                    if (editF1.getText().toString().trim().length() == 0)
                        editF1.requestFocus();
                    else if (editF2.getText().toString().trim().length() == 0)
                        editF2.requestFocus();
                    else if (editF3.getText().toString().trim().length() == 0)
                        editF3.requestFocus();

                    return false;
                }
                if (db.rawQuery("SELECT * FROM store WHERE id=" + newId, null).moveToFirst() || temp.rawQuery("SELECT * FROM tempStock WHERE id=" + newId, null).moveToFirst()) {
                    Toast.makeText(getActivity(), "DB entry with that ID\nalready exists", Toast.LENGTH_SHORT).show();
                    editF1.requestFocus();
                    return false;
                }
                temp.execSQL("INSERT INTO tempStock VALUES('" + newId + "','" + editF2.getText() +
                        "','" + editF3.getText() + "');");
                Toast.makeText(getActivity(), "Record staged for\naddition to main DB", Toast.LENGTH_SHORT).show();
                populateList(adapterA, false);
                editF1.setText("");
                editF2.setText("");
                editF3.setText("");
                editF1.requestFocus();
                return true;
            }
        });

        lay.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAll();
                populateList(adapterA, false);
            }
        });
        populateList(adapterA, false);
        return lay;
    }

    private void saveAll() {
        Cursor i = temp.rawQuery("SELECT * FROM tempStock", null);
        if (!i.moveToFirst()) return;
        db.execSQL("INSERT INTO store VALUES('" + i.getString(0) + "','" + i.getString(1) +
                "','" + i.getString(2) + "');");
        while (i.moveToNext()) {
            db.execSQL("INSERT INTO store VALUES('" + i.getString(0) + "','" + i.getString(1) +
                    "','" + i.getString(2) + "');");
        }
        adapterA.clear();
        temp.execSQL("DELETE FROM tempStock;");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (temp.rawQuery("SELECT * FROM tempStock;", null).moveToFirst()) {
            AlertDialog.Builder savePrompt = new AlertDialog.Builder(getContext());
            savePrompt.setTitle("Save Records?");
            savePrompt.setMessage("There are staged records yet to be saved\n You can save them later as well");
            savePrompt.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveAll();
                    populateList(adapterV, true);
                }
            });
            savePrompt.setCancelable(true);
            savePrompt.setIcon(R.drawable.ic_save_black_24dp);
            savePrompt.show();
        }
    }
}
