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

public class AdminEdytujPracownika extends AppCompatActivity {

    String id;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usun, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_wyloguj_usun:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_usun:
                String query = "DELETE FROM pracownik WHERE id_pracownik LIKE " + id;
                final String username = (String) getIntent().getStringExtra("username");
                final String password = (String) getIntent().getStringExtra("password");
                try {
                    String exit = new DatabaseSelect(AdminEdytujPracownika.this).
                            execute("insert", username, password, query).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Pomyślnie usunięto pracownika!", Toast.LENGTH_LONG);
                toast.show();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_szczegoly_pracownika);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);
        id = (String) getIntent().getStringExtra("id");

        final CheckBox edit_dojazd = ((CheckBox) findViewById(R.id.edit_dojazd));
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

        String query = "SELECT * FROM pracownik WHERE id_pracownik LIKE '" + id + "'";

        final String[] exit = {null};
        try {
            exit[0] = new DatabaseSelect(this).execute("select_", username, password, query).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] wiersze = exit[0].split("\\|");
        String[][] kolumny = new String[wiersze.length][];
        int j = 0;
        for (String string : wiersze)
            kolumny[j++] = string.split("&");

        edit_imie.setText(kolumny[0][1]);
        edit_drugie_imie.setText(kolumny[0][2]);
        edit_nazwisko.setText(kolumny[0][3]);
        edit_pesel.setText(kolumny[0][4]);
        edit_data_ur.setText(kolumny[0][6]);
        edit_miasto.setText(kolumny[0][7]);
        edit_ulica.setText(kolumny[0][8]);
        edit_nr_domu.setText(kolumny[0][9]);
        edit_nr_mieszkania.setText(kolumny[0][10]);
        edit_kod_pocztowy.setText(kolumny[0][11]);

        if (String.valueOf(kolumny[0][13]).equals("0")) {
            edit_dojazd.setChecked(false);
        } else {
            edit_dojazd.setChecked(true);
        }

        if (edit_dojazd.isChecked())
            edit_dojazd.setText("Tak");

        query = "select id, name from users u join pracownik p on p.id_user = u.id where p.id_pracownik like " + id;
        String[] one_strings = null;

        try {//Dodanie obecnego loginu do spinnera
            exit[0] = new DatabaseSelect(this).execute("select_user_spinner", username, password, query).get();
            one_strings = exit[0].split("\\|");

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        query = "select id, name from users where not exists (select * from pracownik where id = id_user)";

        try {//Wypisywanie loginów do spinnera
            exit[0] = new DatabaseSelect(this).execute("select_user_spinner", username, password, query).get();
            String[] strings = exit[0].split("\\|");

            List<String> list = new ArrayList<String>();

            if (!one_strings[0].equals(""))
                list.add(one_strings[0]);

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
            exit[0] = new DatabaseSelect(this).execute("select_user_spinner", username, password, query).get();
            String[] strings = exit[0].split("\\|");

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

        edit_funkcja.setSelection(Integer.parseInt(kolumny[0][5]) - 1);

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

        ((Button) findViewById(R.id.button_szczegoly_pracownika_zatwierdz)).setOnClickListener(
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

                            String query = "UPDATE `pracownik` SET `imie` = " + imie + ", `drugie_imie` = " + drugie_imie + ", `nazwisko` = " + nazwisko + ", `PESEL` = " + pesel +
                                    ", `id_funkcja` = " + id_funkcja + ", `data_ur` = " + data + ", `miasto` = " + miasto + ", `ulica` = " + ulica + ", `nr_domu` = " + nrDomu +
                                    ", `nr_lokalu` = " + nrMieszkania + ", `kod_pocztowy` = " + kod + ", `id_user` = " + id_user + ", `czy_dojezdza` = " + czy_dojezdza +
                                    " WHERE `pracownik`.`id_pracownik` = 4;";

                            if (imie.equals("") || nazwisko.equals("") || pesel.length() != 13 || data.length() != 12 ||
                                    miasto.equals("") || ulica.equals("") || nrDomu.equals("") || kod.length() != 8) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Niepoprawnie uzupełniony formularz!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                exit[0] = new DatabaseSelect(AdminEdytujPracownika.this).
                                        execute("insert", username, password, query).get();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Pomyślnie zaktualizowano dane pracownika!", Toast.LENGTH_LONG);
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
