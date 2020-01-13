package com.example.system1_42;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class PracownikHistoriaPrzychodow extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pracownik_historia_przychodow);

        final ListView listView_wynagrodzenia_pracownik = (ListView) findViewById(R.id.listaView_wynagrodzenia_pracownik);
        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        String exit = "";
        String query = "SELECT w.miesiac, w.rok, kwota, dodatek, IFNULL(SUM(liczba_dni), 0) " +
                "FROM wynagrodzenie w JOIN pracownik p ON w.id_pracownik = p.id_pracownik " +
                "LEFT JOIN zwolnienia_lekarskie z ON w.miesiac = z.miesiac AND w.rok = z.rok " +
                "AND w.id_pracownik = z.id_pracownik " +
                "WHERE p.id_user LIKE " +
                "(SELECT id FROM users WHERE name LIKE '" + username + "' AND password LIKE '" + password + "') " +
                "GROUP BY w.miesiac, w.rok ORDER BY w.id_wynagrodzenie";

        try {
            exit = new DatabaseSelect(this).execute("select", username, password, query).get();

            if (!exit.equals("")) {
                String[] strings = exit.split("\\|");
                String[][] strings1 = new String[strings.length][];
                int i = 0;
                for (String string : strings)
                    strings1[i++] = string.split("-");

                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

                for (i = 0; i < strings.length; i++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("miesiac", strings1[i][0]);
                    hashMap.put("rok", strings1[i][1]);
                    hashMap.put("kwota", strings1[i][2]);
                    hashMap.put("dodatek", strings1[i][3]);
                    hashMap.put("dni_zwolnienia", strings1[i][4]);
                    arrayList.add(hashMap);
                }

                String[] from = {"miesiac", "rok", "kwota", "dodatek", "dni_zwolnienia"};
                int[] to = {R.id.row_lista_wynagrodzenia_miesiac, R.id.row_lista_wynagrodzenia_rok,
                        R.id.row_lista_wynagrodzenia_brutto, R.id.row_lista_wynagrodzenia_dodatek,
                        R.id.row_lista_wynagrodzenia_dni_na_zwolnieniu};
                SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList,
                        R.layout.row_historia_wynagrodzen, from, to);
                listView_wynagrodzenia_pracownik.setAdapter(simpleAdapter);
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listView_wynagrodzenia_pracownik.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView viewById_brutto = view.findViewById(R.id.row_lista_wynagrodzenia_brutto);
                TextView viewById_miesiac = view.findViewById(R.id.row_lista_wynagrodzenia_miesiac);
                TextView viewById_rok = view.findViewById(R.id.row_lista_wynagrodzenia_rok);
                TextView viewById_dni_zwolnienia = view.findViewById(R.id.row_lista_wynagrodzenia_dni_na_zwolnieniu);

                String kwota = (String) viewById_brutto.getText();
                String data = (String) viewById_miesiac.getText() + " " + (String) viewById_rok.getText();
                String dni_zwolnienia = (String) viewById_dni_zwolnienia.getText();
                ;

                Intent intent = new Intent(PracownikHistoriaPrzychodow.this, PracownikPredykcjaSzczegoly.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("brutto", kwota);
                intent.putExtra("data", data);
                intent.putExtra("dni_zwolnienia", dni_zwolnienia);
                startActivity(intent);
            }
        });
    }
}
