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

public class AdminWypiszPracownicy extends AppCompatActivity {

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
                intent = new Intent(getApplicationContext(), AdminDodajPracownika.class);
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
        setContentView(R.layout.admin_pracownicy);

        ListView listView = (ListView) findViewById(R.id.list_view);
        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        String exit = "";
        String query = "select nazwisko, imie, nazwa, id_pracownik from pracownik p " +
                "join funkcja f " +
                "on p.id_funkcja = f.id_funkcja " +
                "order by nazwisko";

        try {
            exit = new DatabaseSelect(this).execute("select", username, password, query).get();

            String[] strings = exit.split("\\|");
            String[][] strings1 = new String[strings.length][];
            int i = 0;
            for (String string : strings)
                strings1[i++] = string.split("-");

            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

            for (i = 0; i < strings.length; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("nazwisko", strings1[i][0] + " " + strings1[i][1]);
                hashMap.put("funkcja", strings1[i][2]);
                hashMap.put("id", strings1[i][3]);
                arrayList.add(hashMap);
            }

            String[] from = {"nazwisko", "funkcja", "id"};
            int[] to = {R.id.row_nazwisko, R.id.row_funkcja, R.id.row_id_pracownik};
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.row_pracownicy, from, to);
            listView.setAdapter(simpleAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView viewById = view.findViewById(R.id.row_id_pracownik);
                    String id_pracownik = (String) viewById.getText();

                    Intent intent = new Intent(AdminWypiszPracownicy.this, AdminEdytujPracownika.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("id", id_pracownik);
                    startActivity(intent);
                }
            });

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
