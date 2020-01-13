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

public class AdminDodajUrlop extends AppCompatActivity {

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
        setContentView(R.layout.admin_dodaj_urlop);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        final Spinner edit_nazwisko_dodaj_urlop = ((Spinner) findViewById(R.id.edit_nazwisko_dodaj_urlop));
        final EditText edit_nazwa_dodaj_urlop = ((EditText) findViewById(R.id.edit_nazwa_dodaj_urlop));
        final EditText edit_liczba_dni_dodaj_urlop = ((EditText) findViewById(R.id.edit_liczba_dni_dodaj_urlop));

        DataGetter dataGetter = new DataGetter();
        final String miesiac = dataGetter.getMiesiac(), rok = dataGetter.getRok();
        TextView data = ((TextView) findViewById(R.id.edit_data_dodaj_urlop));
        final String dataString = miesiac + " " + rok;
        data.setText(dataString);

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
            edit_nazwisko_dodaj_urlop.setAdapter(arrayAdapter);

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
                            String id, nazwa, liczba_dni;
                            id = edit_nazwisko_dodaj_urlop.getSelectedItem().toString();
                            id = id.replaceAll("\\D+", "");
                            nazwa = "'" + edit_nazwa_dodaj_urlop.getText() + "'";
                            liczba_dni = String.valueOf(edit_liczba_dni_dodaj_urlop.getText());

                            String query = "select sum(liczba_dni) from urlopy " +
                                    "where id_pracownik like '" + id + "' and rok like '" + rok + "'";
                            String exit = new DatabaseSelect(AdminDodajUrlop.this).
                                    execute("select_one_thing", username, password, query).get();

                            String dni_dotychczas = exit;

                            query = "INSERT INTO `urlopy` (`id_urlop`, `id_pracownik`, " +
                                    "`nazwa`, `miesiac`, `rok`, `liczba_dni`) " +
                                    "VALUES (NULL, '" + id + "', " + nazwa + ", '" + miesiac + "', '" +
                                    rok + "', '" + liczba_dni + "')";

                            if (nazwa.equals("''") || liczba_dni.equals("''")) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Niepoprawnie uzupełniony formularz!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            if (Integer.parseInt(dni_dotychczas) + Integer.parseInt(liczba_dni) > 20) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Za dużo urlopu w tym roku!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                exit = new DatabaseSelect(AdminDodajUrlop.this).
                                        execute("insert", username, password, query).get();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Pomyślnie dodano urlop!", Toast.LENGTH_LONG);
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
