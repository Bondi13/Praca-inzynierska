package com.example.system1_42;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDodajPracownika extends AppCompatActivity {

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
        setContentView(R.layout.admin_dodaj_pracownika);

        String exit;
        String query = "select id, name from users where not exists " +
                "(select * from pracownik where id = id_user)";

        final CheckBox edit_dojazd = ((CheckBox) findViewById(R.id.edit_dojazd));
        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        final EditText edit_imie = ((EditText) findViewById(R.id.edit_imie));
        final EditText edit_drugie_imie = ((EditText) findViewById(R.id.edit_drugie_imie));
        final EditText edit_nazwisko = ((EditText) findViewById(R.id.edit_nazwisko));
        final EditText edit_pesel = ((EditText) findViewById(R.id.edit_pesel));
        final EditText edit_data_ur = ((EditText) findViewById(R.id.edit_data_ur));
        final EditText edit_miasto = ((EditText) findViewById(R.id.edit_miasto));
        final EditText edit_ulica = ((EditText) findViewById(R.id.edit_ulica));
        final EditText edit_nr_domu = ((EditText) findViewById(R.id.edit_nr_domu));
        final EditText edit_nr_mieszkania = ((EditText) findViewById(R.id.edit_nr_mieszkania));
        final EditText edit_kod_pocztowy = ((EditText) findViewById(R.id.edit_kod_pocztowy));
        final Spinner edit_user = ((Spinner) findViewById(R.id.edit_user));
        final Spinner edit_funkcja = ((Spinner) findViewById(R.id.edit_funkcja));

        try {//Wypisywanie loginów do spinnera
            exit = new DatabaseSelect(this).execute("select_user_spinner", username, password, query).get();
            String[] strings = exit.split("\\|");

            List<String> list = new ArrayList<String>();
            for (String string : strings) {
                list.add(string);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.
                    support_simple_spinner_dropdown_item, list);
            arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            edit_user.setAdapter(arrayAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        query = "select id_funkcja, nazwa from funkcja";

        try {//Wypisywanie funkcji do spinnera
            exit = new DatabaseSelect(this).execute("select_user_spinner", username, password, query).get();
            String[] strings = exit.split("\\|");
            List<String> list = new ArrayList<String>();

            for (String string : strings) {
                list.add(string);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.
                    support_simple_spinner_dropdown_item, list);
            arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            edit_funkcja.setAdapter(arrayAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        edit_dojazd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edit_dojazd.isChecked())
                            edit_dojazd.setText("Tak");
                        else
                            edit_dojazd.setText("Nie");
                    }
                }
        );

        ((Button) findViewById(R.id.button_dodaj_zatwierdz)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String imie, drugie_imie, nazwisko, pesel, funkcja, data, miasto, ulica,
                                    nrDomu, nrMieszkania, kod, user, czy_dojezdza, id_funkcja, id_user;
                            imie = "'" + edit_imie.getText() + "'";
                            drugie_imie = "'" + edit_drugie_imie.getText() + "'";
                            nazwisko = "'" + edit_nazwisko.getText() + "'";
                            pesel = "'" + edit_pesel.getText() + "'";
                            funkcja = edit_funkcja.getSelectedItem().toString();
                            funkcja = funkcja.replaceAll("\\D+", "");
                            data = "'" + edit_data_ur.getText() + "'";
                            miasto = "'" + edit_miasto.getText() + "'";
                            ulica = "'" + edit_ulica.getText() + "'";
                            nrDomu = "'" + edit_nr_domu.getText() + "'";
                            nrMieszkania = "'" + edit_nr_mieszkania.getText() + "'";
                            kod = "'" + edit_kod_pocztowy.getText() + "'";
                            user = edit_user.getSelectedItem().toString();
                            user = user.split(" ", 2)[0];
                            id_user = "'" + user + "'";
                            id_funkcja = "'" + funkcja + "'";
                            if (drugie_imie.equals("''")) drugie_imie = "NULL";
                            if (nrMieszkania.equals("''")) nrMieszkania = "NULL";
                            if (edit_dojazd.isChecked())
                                czy_dojezdza = "1";
                            else
                                czy_dojezdza = "0";

                            String query = "INSERT INTO `pracownik` (`id_pracownik`, `imie`, `drugie_imie`, " +
                                    "`nazwisko`, " + "`PESEL`, `id_funkcja`, `data_ur`, `miasto`, `ulica`, " +
                                    "`nr_domu`, `nr_lokalu`, " + "`kod_pocztowy`, `id_user`, `czy_dojezdza`) " +
                                    "VALUES (NULL, " + imie + ", " + drugie_imie + ", " + nazwisko + ", " +
                                    pesel + ", " + id_funkcja + ", " + data + ", " + miasto + ", " + ulica +
                                    ", " + nrDomu + ", " + nrMieszkania + ", " + kod + ", " + id_user + ", " +
                                    czy_dojezdza + ");";

                            if (imie.equals("") || nazwisko.equals("") || pesel.length() != 13 || data.length() != 12 ||
                                    miasto.equals("") || ulica.equals("") || nrDomu.equals("") || kod.length() != 8) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Niepoprawnie uzupełniony formularz!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                String exit = new DatabaseSelect(AdminDodajPracownika.this).
                                        execute("insert", username, password, query).get();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Pomyślnie dodano pracownika!", Toast.LENGTH_LONG);
                                toast.show();
                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

}
