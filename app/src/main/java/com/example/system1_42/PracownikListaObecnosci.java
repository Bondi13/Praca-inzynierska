package com.example.system1_42;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class PracownikListaObecnosci extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_wyloguj:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pracownik_lista_obecnosci);

        ListView listView = (ListView) findViewById(R.id.list_view);
        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        TextView pozostaly_urlop = (TextView) findViewById(R.id.pozostaly_urlop);
        try {
            DataGetter dataGetter = new DataGetter();

            String query = "select sum(liczba_dni) from urlopy ur " +
                    "join pracownik p on p.id_pracownik = ur.id_pracownik " +
                    "join users us on us.id = p.id_user " +
                    "where name like '" + username + "' and password like '" + password + "' " +
                    "and rok like '" + dataGetter.getRok() + "'";

            String exit = new DatabaseSelect(PracownikListaObecnosci.this).
                    execute("select_one_thing", username, password, query).get();

            int pozostaly = 20;

            if (!exit.equals(""))
                pozostaly = 20 - Integer.parseInt(exit);
            pozostaly_urlop.setText("pozostały urlop: " + pozostaly);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            String query = "select id_lista, miesiac, rok, l.id_pracownik, liczba_dni " +
                    "from lista_obecnosci l join pracownik p on p.id_pracownik = l.id_pracownik " +
                    "where id_user like " +
                    "(select id from users where name like '" + username + "' " +
                    "and password like '" + password + "')";

            String exit = new DatabaseSelect(this).execute("select", username, password, query).get();

            String[] strings = exit.split("\\|");
            String[][] strings1 = new String[strings.length][];
            int i = 0;
            for (String string : strings)
                strings1[i++] = string.split("-");

            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

            for (i = 0; i < strings.length; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("miesiac", strings1[i][1]);
                hashMap.put("rok", strings1[i][2]);
                hashMap.put("dni_obecne", strings1[i][4]);
                arrayList.add(hashMap);
            }

            String[] from = {"miesiac", "rok", "dni_obecne"};
            int[] to = {R.id.row_obecnosci_miesiac, R.id.row_obecnosci_rok,
                    R.id.row_obecnosci_obecnosc_dni};
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList,
                    R.layout.row_obecnosci, from, to);
            listView.setAdapter(simpleAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
