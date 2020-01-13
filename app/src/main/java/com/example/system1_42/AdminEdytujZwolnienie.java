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

public class AdminEdytujZwolnienie extends AppCompatActivity {

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
                String query = "DELETE FROM zwolnienia_lekarskie WHERE id_zwolnienia LIKE " + id;
                final String username = (String) getIntent().getStringExtra("username");
                final String password = (String) getIntent().getStringExtra("password");
                try {
                    String exit = new DatabaseSelect(AdminEdytujZwolnienie.this).
                            execute("insert", username, password, query).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Pomyślnie usunięto zwolnienie!", Toast.LENGTH_LONG);
                toast.show();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dodaj_urlop);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);
        id = (String) getIntent().getStringExtra("id");
        final Spinner edit_nazwisko_dodaj_urlop = ((Spinner) findViewById(R.id.edit_nazwisko_dodaj_urlop));
        final EditText edit_nazwa_dodaj_urlop = ((EditText) findViewById(R.id.edit_nazwa_dodaj_urlop));
        final EditText edit_liczba_dni_dodaj_urlop = ((EditText) findViewById(R.id.edit_liczba_dni_dodaj_urlop));
        Button button_zapisz = ((Button)findViewById(R.id.button_dodaj_urlop));
        button_zapisz.setText("ZAPISZ ZMIANY");

        DataGetter dataGetter = new DataGetter();
        final String miesiac = dataGetter.getMiesiac(), rok = dataGetter.getRok();
        TextView data = ((TextView) findViewById(R.id.edit_data_dodaj_urlop));
        final String dataString = miesiac + " " + rok;
        data.setText(dataString);

        try {
            String query = "SELECT p.id_pracownik, nazwisko, imie FROM pracownik p " +
                    "JOIN zwolnienia_lekarskie z ON p.id_pracownik = z.id_pracownik WHERE id_zwolnienia LIKE '" + id + "'";
            String exit = new DatabaseSelect(this).execute("select_three_array", username, password, query).get();
            String[] strings = exit.split("\\|");
            List<String> list = new ArrayList<String>();
            String id_pracownik = strings[0];
            list.add(id_pracownik);

            query = "SELECT id_pracownik, nazwisko, imie FROM pracownik";
            exit = new DatabaseSelect(this).execute("select_three_array", username, password, query).get();
            strings = exit.split("\\|");

            for (String string : strings) {
                if (!string.equals(id_pracownik))
                    list.add(string);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.
                    support_simple_spinner_dropdown_item, list);
            arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            edit_nazwisko_dodaj_urlop.setAdapter(arrayAdapter);

            query = "SELECT * FROM zwolnienia_lekarskie WHERE id_zwolnienia LIKE '" + id + "'";
            exit = new DatabaseSelect(this).execute("select_", username, password, query).get();
            String[] wiersze = exit.split("\\|");
            final String[][] kolumny = new String[wiersze.length][];
            int j = 0;
            for (String string : wiersze)
                kolumny[j++] = string.split("&");

            edit_nazwa_dodaj_urlop.setText(kolumny[0][2]);
            edit_liczba_dni_dodaj_urlop.setText(kolumny[0][5]);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ((Button) findViewById(R.id.button_dodaj_urlop)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String query = "DELETE FROM zwolnienia_lekarskie WHERE id_zwolnienia LIKE " + id;
                            String exit = new DatabaseSelect(AdminEdytujZwolnienie.this).
                                    execute("insert", username, password, query).get();
                            String id_pracownik, nazwa, liczba_dni;
                            id_pracownik = edit_nazwisko_dodaj_urlop.getSelectedItem().toString();
                            id_pracownik = id_pracownik.replaceAll("\\D+", "");
                            nazwa = "'" + edit_nazwa_dodaj_urlop.getText() + "'";
                            liczba_dni = "'" + edit_liczba_dni_dodaj_urlop.getText() + "'";

                            query = "INSERT INTO `zwolnienia_lekarskie` (`id_zwolnienia`, `id_pracownik`, " +
                                    "`nazwa`, `miesiac`, `rok`, `liczba_dni`) " +
                                    "VALUES ('" + id + "', '" + id_pracownik + "', " + nazwa + ", '" + miesiac + "', '" +
                                    rok + "', " + liczba_dni + ")";

                            if (nazwa.equals("''") || liczba_dni.equals("''")) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Niepoprawnie uzupełniony formularz!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                exit = new DatabaseSelect(AdminEdytujZwolnienie.this).
                                        execute("insert", username, password, query).get();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Pomyślnie zaktualizowano zwolnienie!", Toast.LENGTH_LONG);
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
