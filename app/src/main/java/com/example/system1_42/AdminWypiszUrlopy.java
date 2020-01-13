package com.example.system1_42;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminWypiszUrlopy extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dodaj, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_wyloguj_dodaj:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_dodaj:
                intent = new Intent(getApplicationContext(), AdminDodajUrlop.class);
                final String username = (String) getIntent().getStringExtra("username");
                final String password = (String) getIntent().getStringExtra("password");
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_urlopy);

        ListView listView = (ListView) findViewById(R.id.list_view);
        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        String exit = "";
        String query = "SELECT id_urlop, nazwisko, imie, nazwa, miesiac, rok, liczba_dni " +
                "FROM urlopy u JOIN pracownik p ON p.id_pracownik = u.id_pracownik";

        try {
            exit = new DatabaseSelect(this).execute("select", username, password, query).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] strings = exit.split("\\|");
        String[][] strings1 = new String[strings.length][];
        int i = 0;
        for (String string : strings)
            strings1[i++] = string.split("-");

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        for (i = 0; i < strings.length; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", strings1[i][0]);
            hashMap.put("miesiac", strings1[i][4]);
            hashMap.put("rok", strings1[i][5]);
            hashMap.put("nazwisko", strings1[i][1]);
            hashMap.put("imie", strings1[i][2]);
            hashMap.put("nazwa", strings1[i][3]);
            hashMap.put("liczba_dni", strings1[i][6]);
            arrayList.add(hashMap);
        }

        String[] from = {"id", "nazwisko", "imie", "nazwa", "miesiac", "rok", "liczba_dni"};
        int[] to = {R.id.row_id_urlopy, R.id.row_nazwisko_urlopy, R.id.row_imie_urlopy,
                R.id.row_nazwa_urlopy, R.id.row_miesiac_urlopy, R.id.row_rok_urlopy,
                R.id.row_liczba_dni_urlopy};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList,
                R.layout.row_urlopy, from, to);
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView viewById = view.findViewById(R.id.row_id_urlopy);
                String id_urlopu = (String) viewById.getText();

                Intent intent = new Intent(AdminWypiszUrlopy.this, AdminEdytujUrlop.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("id", id_urlopu);
                startActivity(intent);
            }
        });
    }
}
