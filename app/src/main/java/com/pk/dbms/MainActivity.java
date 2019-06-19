package com.pk.dbms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class MainActivity extends AppCompatActivity {
    TextView mTextMessage;
    FragViewAll viewAll;
    FragSearch search;
    FragAdd add;
    public static CustomAdapter adapterV, adapterA, adapterS;

    public static SQLiteDatabase db, temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewAll = new FragViewAll();
        add = new FragAdd();
        search = new FragSearch();

        adapterS = new CustomAdapter(this, new ArrayList<Record>(), true);
        adapterA = new CustomAdapter(this, new ArrayList<Record>(), false);
        adapterV = new CustomAdapter(this, new ArrayList<Record>(), true);


        db = openOrCreateDatabase("StoreDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS store(id int primary key,name VARCHAR,stock VARCHAR);");
        temp = openOrCreateDatabase("tempBuf", Context.MODE_PRIVATE, null);
        temp.execSQL("CREATE TABLE IF NOT EXISTS tempStock(id int primary key,name VARCHAR,stock VARCHAR);");
        temp.execSQL("DELETE FROM tempStock;");

        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        changeFrag(viewAll);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    changeFrag(viewAll);
                    return true;
                case R.id.navigation_dashboard:
                    changeFrag(search);
                    return true;
                case R.id.navigation_notifications:
                    changeFrag(add);
                    return true;
            }
            return false;
        }
    };

    private void changeFrag(Fragment fragment) {
        FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.fragSpace, fragment);
        fragTrans.commit();
    }


    private void shoDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.three_dotmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        switch (item.getItemId()) {
            case R.id.help:
                builder.setTitle("Help");

                builder.setMessage("Use the View All, Search and Add Records fragments " +
                        "to display, find and add new entries to your database respectively.\n\n" +
                        "Tap on any record in any list to modify/delete that record. " +
                        "Note that the list in Add Records fragment is temporary and " +
                        "must be saved to add them to the main database.\n\n" +
                        "In the Search fragment, use the radio buttons to select" +
                        " your search criteria and enter your query into the text box.\n\n" +
                        "IDs are unique(Primary Key) for each entry.");
                View creditLink = LayoutInflater.from(getBaseContext()).inflate(R.layout.credit_link, null, false);
                builder.setView(creditLink);
                builder.show();
                break;
            case R.id.mainClear:
                builder.setTitle("Delete All Records?");
                builder.setMessage("This action is IRREVERSIBLE!\n" +
                        "All the records from your Database will be deleted!");
                builder.setPositiveButton("Do it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.execSQL("DELETE FROM store");
                        adapterV.clear();
                    }
                });
                builder.setIcon(R.drawable.ic_delete_forever_black_24dp);
                builder.show();
                break;
            case R.id.tempClear:
                builder.setTitle("Clear Staged Records?");
                builder.setMessage("All the staged records (From Add Records fragment) will be deleted!");
                builder.setPositiveButton("Do it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        temp.execSQL("DELETE FROM tempStock");
                        adapterA.clear();
                    }
                });
                builder.setIcon(R.drawable.ic_clear_all_black_24dp);
                builder.show();
                break;
        }
        return true;
    }

    public static void populateList(CustomAdapter ad, boolean toAccessDB) {
        Cursor i;
        if (toAccessDB)
            i = db.rawQuery("SELECT * FROM store ORDER BY id ASC", null);
        else i = temp.rawQuery("SELECT * FROM tempStock ORDER BY id ASC", null);
        ad.clear();
        if (!i.moveToFirst()) return;
        ad.add(new Record(i));
        while ((i.moveToNext())) {
            ad.add(new Record(i));
        }
    }
}
