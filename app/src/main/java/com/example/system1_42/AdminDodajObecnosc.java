package com.example.system1_42;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDodajObecnosc extends AppCompatActivity {
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
        setContentView(R.layout.admin_dodaj_obecnosc);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        final Spinner edit_nazwisko_dodaj_obecnosc = ((Spinner) findViewById(R.id.edit_nazwisko_dodaj_obecnosc));
        final EditText edit_liczba_dni_dodaj_obecnosc = ((EditText) findViewById(R.id.edit_liczba_dni_dodaj_obecnosc));
        final TextView obecnosc_liczba_dni = ((TextView) findViewById(R.id.obecnosc_liczba_dni));
        final String miesiac, rok;
        final DataGetter dataGetter = new DataGetter();
        miesiac = dataGetter.getMiesiac();
        rok = dataGetter.getRok();

        TextView predict_data = ((TextView) findViewById(R.id.edit_data_dodaj_obecnosc));
        final String dataString = miesiac + " " + rok;
        predict_data.setText(dataString);

        String query = "SELECT id_pracownik, nazwisko, imie FROM pracownik";

        try {
            String exit = new DatabaseSelect(this).execute("select_three_array", username, password, query).get();
            String[] strings = exit.split("\\|");
            List<String> list = new ArrayList<String>();

            for (String string : strings) {
                list.add(string);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.
                    support_simple_spinner_dropdown_item, list);
            arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            edit_nazwisko_dodaj_obecnosc.setAdapter(arrayAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        obecnosc_liczba_dni.setText("/" + dataGetter.getLiczba_dni());

        ((Button) findViewById(R.id.button_dodaj_obecnosc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id;
                id = edit_nazwisko_dodaj_obecnosc.getSelectedItem().toString();
                id = "'" + id.replaceAll("\\D+", "") + "'";

                try {
                    String query = "DELETE FROM `lista_obecnosci` WHERE miesiac like '" + miesiac + "' " +
                            "and rok like '" + rok + "' and id_pracownik like " + id;

                    String exit = new DatabaseSelect(AdminDodajObecnosc.this).
                            execute("insert", username, password, query).get();

                    String liczba_dni = String.valueOf(edit_liczba_dni_dodaj_obecnosc.getText());

                    query = "INSERT INTO `lista_obecnosci` (`id_lista`, `miesiac`, `rok`, `id_pracownik`, " +
                            "`liczba_dni`) VALUES (NULL, '" + miesiac + "', '" + rok + "', " + id + ", '" + liczba_dni + "')";

                    exit = new DatabaseSelect(AdminDodajObecnosc.this).
                            execute("insert", username, password, query).get();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Zaktualizowano obecność pracownika!", Toast.LENGTH_LONG);
                    toast.show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ((Button) findViewById(R.id.button_wszyscy_obecni)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String query = "select id_pracownik from pracownik";

                    String exit = new DatabaseSelect(AdminDodajObecnosc.this).
                            execute("select", username, password, query).get();

                    String[] strings = exit.split("\\|");
                    String[][] strings1 = new String[strings.length][];
                    int i = 0;
                    for (String string : strings) {
                        strings1[i++] = string.split("-");
                    }

                    for (i = 0; i < strings.length; i++) {
                        query = "DELETE FROM `lista_obecnosci` WHERE miesiac like '" + miesiac + "' " +
                                "and rok like '" + rok + "' and id_pracownik like '" + strings1[i][0] + "'";
                        System.out.println(query);//////////////////////////////////////////////////////////
                        exit = new DatabaseSelect(AdminDodajObecnosc.this).
                                execute("insert", username, password, query).get();

                        query = "INSERT INTO `lista_obecnosci` (`id_lista`, `miesiac`, `rok`, `id_pracownik`, " +
                                "`liczba_dni`) VALUES (NULL, '" + miesiac + "', '" + rok + "', '" +
                                strings1[i][0] + "', '" + dataGetter.getLiczba_dni() + "')";
                        System.out.println(query);//////////////////////////////////////////////////////////
                        exit = new DatabaseSelect(AdminDodajObecnosc.this).
                                execute("insert", username, password, query).get();
                    }

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Wszyscy byli obecni codziennie!", Toast.LENGTH_LONG);
                    toast.show();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
