package com.pk.dbms;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.pk.dbms.MainActivity.adapterS;
import static com.pk.dbms.MainActivity.db;

class FragSearch extends Fragment {
    private ListView searchList;
    RadioGroup rG;
    EditText query;

    public FragSearch() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View lay= inflater.inflate(R.layout.frag_search, container, false);
        query=lay.findViewById(R.id.editQry);
        searchList=lay.findViewById(R.id.list2);
        searchList.setAdapter(adapterS);
        adapterS.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lay.findViewById(R.id.radio1).setFocusedByDefault(true);
        }
        rG =lay.findViewById(R.id.search_radioGrp);
        rG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(radioGroup.getCheckedRadioButtonId())
                {
                    case R.id.radio1:
                        query.setInputType(InputType.TYPE_CLASS_NUMBER);
                        query.setText("");
                        query.setHint(R.string.field1);
                        break;
                    case R.id.radio2:
                        query.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                        query.setHint(R.string.field2);
                        break;
                    case R.id.radio3:
                        query.setHint(R.string.field3);
                        query.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                        break;
                }
            }
        });

        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, int i, KeyEvent keyEvent) {
                switch(rG.getCheckedRadioButtonId()){
                    case R.id.radio1:
                        populateList(db.rawQuery("SELECT * FROM store WHERE id="+textView.getText().toString().trim()+" ORDER BY id ASC",null));
                        break;
                    case R.id.radio2:
                        populateList(db.rawQuery("SELECT * FROM store WHERE name='"+textView.getText().toString().trim()+"' ORDER BY name ASC",null));
                        break;
                    case R.id.radio3:
                        populateList(db.rawQuery("SELECT * FROM store WHERE stock='"+textView.getText().toString().trim()+"' ORDER BY stock ASC",null));
                        break;
                    default:
                        shoDialog("","Please select a search criteria first!");
                }


            return true;}
        });


        return lay;
    }
    private void shoDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    private void populateList(Cursor rawQuery) {
        adapterS.clear();
        if(!rawQuery.moveToFirst())return;
        adapterS.add(new Record(rawQuery));
        while(rawQuery.moveToNext()){
            adapterS.add(new Record(rawQuery));
        }
    }
}
