package com.example.anisimov.vlad.RandomCompany2task1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ListView listView = (ListView) findViewById(R.id.list_view);
        List cities = new ArrayList<>(3);
        cities.add("Днепропетровск");
        cities.add("Киев");
        cities.add("Львов");
        MyArrayAdapter adapter = new MyArrayAdapter(this,android.R.layout.simple_list_item_1,cities);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.putExtra("CITY",item);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.b_settings_mask, menu);
        return true;
    }

    private class MyArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public MyArrayAdapter(Context context, int textViewResourceId,
                              List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
