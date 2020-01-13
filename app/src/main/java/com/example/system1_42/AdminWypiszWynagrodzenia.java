package com.example.system1_42;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AdminWypiszWynagrodzenia extends AppCompatActivity {
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
        setContentView(R.layout.admin_wynagrodzenia_lista);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        String query = "select nazwisko, imie, miesiac, rok, kwota, dodatek, p.id_pracownik from pracownik p " +
                "join wynagrodzenie w on p.id_pracownik = w.id_pracownik";

        final String exit;
        int dni_nieobecne = 0;
        try {
            exit = new DatabaseSelect(this).
                    execute("select", username, password, query).get();

            String[] strings = exit.split("\\|");
            String[][] strings1 = new String[strings.length][];
            int i = 0;
            for (String string : strings)
                strings1[i++] = string.split("-");

            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

            for (i = 0; i < strings.length; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("data_miesiac", strings1[i][2]);
                hashMap.put("data_rok", strings1[i][3]);
                hashMap.put("nazwisko", strings1[i][0]);
                hashMap.put("imie", strings1[i][1]);
                hashMap.put("brutto", strings1[i][4]);
                hashMap.put("dodatki", strings1[i][5]);

                String exit2;

                query = "SELECT id_pracownik, ifnull(sum(liczba_dni), 0) from zwolnienia_lekarskie " +
                        "where miesiac like '" + strings1[i][2] + "' and rok like '" + strings1[i][3] + "' " +
                        "and id_pracownik like '" + strings1[i][6] + "' " +
                        "group by id_pracownik, miesiac, rok";

                exit2 = new DatabaseSelect(this).
                        execute("select", username, password, query).get();

                String[] wiersze = exit2.split("\\|");
                String[][] kolumny = new String[wiersze.length][];
                int j = 0;
                for (String string : wiersze)
                    kolumny[j++] = string.split("-");
                int dni_na_zwolnieniu = 0;
                if (kolumny[0].length > 1)
                    dni_na_zwolnieniu = Integer.parseInt(kolumny[0][1]);
                hashMap.put("dni_na_zwolnieniu", String.valueOf(dni_na_zwolnieniu));

                query = "SELECT id_pracownik, suma from podsumowanie_zaang " +
                        "where miesiac like '" + strings1[i][2] + "' and rok like '" + strings1[i][3] + "' " +
                        "and id_pracownik like '" + strings1[i][6] + "'";

                exit2 = new DatabaseSelect(this).
                        execute("select_", username, password, query).get();

                wiersze = exit2.split("\\|");
                kolumny = new String[wiersze.length][];
                j = 0;
                for (String string : wiersze)
                    kolumny[j++] = string.split("&");
                double premia = 0.;
                if (kolumny[0].length > 1) {
                    Double suma = Double.parseDouble(kolumny[0][1]);
                    Premie premie = new Premie(suma, username, password, this);
                    premia = premie.getPremia();
                }
                hashMap.put("premia", String.valueOf(premia));
///
                query = "select liczba_dni from lista_obecnosci where miesiac like '" + strings1[i][2] + "' " +
                        "and rok like '" + strings1[i][3] + "' and id_pracownik like '" + strings1[i][6] + "'";

                exit2 = new DatabaseSelect(this).
                        execute("select_", username, password, query).get();

                wiersze = exit2.split("\\|");
                kolumny = new String[wiersze.length][];
                j = 0;
                for (String string : wiersze)
                    kolumny[j++] = string.split("&");

                DataGetter dataGetter = new DataGetter();

                if (!kolumny[0][0].equals(""))
                    dni_nieobecne = dataGetter.getLiczba_dni() - Integer.parseInt(kolumny[0][0]);

                hashMap.put("dni_nieobecne", String.valueOf(dni_nieobecne));

                Podatki podatki = new Podatki(Float.parseFloat(strings1[i][4]), false,
                        Float.parseFloat(strings1[i][5]), dni_na_zwolnieniu, premia, dni_nieobecne);

                hashMap.put("koszt", String.valueOf(podatki.getKoszt_calkowity_zatrudnienia()));
                arrayList.add(hashMap);
            }

            String[] from = {"data_miesiac", "data_rok", "nazwisko", "imie", "brutto", "koszt",
                    "dodatki", "dni_na_zwolnieniu", "premia", "dni_nieobecne"};
            int[] to = {R.id.row_wynagrodzenia_data_miesiac, R.id.row_wynagrodzenia_data_rok,
                    R.id.row_wynagrodzenia_nazwisko, R.id.row_wynagrodzenia_imie,
                    R.id.row_wynagrodzenia_brutto, R.id.row_wynagrodzenia_koszt,
                    R.id.row_wynagrodzenia_dodatki, R.id.row_wynagrodzenia_dni_na_zwolnieniu,
                    R.id.row_wynagrodzenia_premia, R.id.row_wynagrodzenia_dni_nieobecne};

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList,
                    R.layout.row_wynagrodzenia_lista, from, to);
            ListView listView = (ListView) findViewById(R.id.admin_lista_wynagrodzenia);
            listView.setAdapter(simpleAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView viewById_brutto = view.findViewById(R.id.row_wynagrodzenia_brutto);
                    TextView viewById_dodatki = view.findViewById(R.id.row_wynagrodzenia_dodatki);
                    TextView viewById_dni_na_zwolnieniu = view.findViewById(R.id.row_wynagrodzenia_dni_na_zwolnieniu);
                    TextView viewById_premia = view.findViewById(R.id.row_wynagrodzenia_premia);
                    TextView viewById_data_miesiac = view.findViewById(R.id.row_wynagrodzenia_data_miesiac);
                    TextView viewById_data_rok = view.findViewById(R.id.row_wynagrodzenia_data_rok);
                    TextView viewById_nazwisko = view.findViewById(R.id.row_wynagrodzenia_nazwisko);
                    TextView viewById_imie = view.findViewById(R.id.row_wynagrodzenia_imie);
                    TextView viewById_dni_nieobecne = view.findViewById(R.id.row_wynagrodzenia_dni_nieobecne);

                    String brutto = (String) viewById_brutto.getText();
                    String dodatki = (String) viewById_dodatki.getText();
                    String dni_na_zwolnieniu = (String) viewById_dni_na_zwolnieniu.getText();
                    String premia = (String) viewById_premia.getText();
                    String data = viewById_data_miesiac.getText() + " " + viewById_data_rok.getText();
                    String nazwisko = viewById_nazwisko.getText() + " " + viewById_imie.getText();
                    String dni_nieobecne = (String) viewById_dni_nieobecne.getText();

                    Intent intent = new Intent(AdminWypiszWynagrodzenia.this, AdminSzczegolyWynagrodzen.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("brutto", brutto);
                    intent.putExtra("dodatki", dodatki);
                    intent.putExtra("dni_na_zwolnieniu", dni_na_zwolnieniu);
                    intent.putExtra("premia", premia);
                    intent.putExtra("data", data);
                    intent.putExtra("nazwisko", nazwisko);
                    intent.putExtra("dni_nieobecne", dni_nieobecne);
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
