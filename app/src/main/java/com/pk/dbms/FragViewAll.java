package com.pk.dbms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import static com.pk.dbms.MainActivity.adapterV;
import static com.pk.dbms.MainActivity.populateList;

class FragViewAll extends Fragment {
    ListView viewList;
//    SQLiteDatabase db;

    public FragViewAll() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View lay = inflater.inflate(R.layout.frag_view_all, container, false);

        viewList = lay.findViewById(R.id.list1);
        viewList.setAdapter(adapterV);
        populateList(adapterV,true);
        return lay;
    }

}
