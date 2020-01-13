package com.example.system1_42;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminEdytujFunkcje extends AppCompatActivity {

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
                String query = "DELETE FROM urlopy WHERE id LIKE " + id;
                final String username = (String) getIntent().getStringExtra("username");
                final String password = (String) getIntent().getStringExtra("password");
                try {
                    String exit = new DatabaseSelect(AdminEdytujFunkcje.this).
                            execute("insert", username, password, query).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Pomyślnie usunięto urlop!", Toast.LENGTH_LONG);
                toast.show();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dodaj_funkcje);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);
        id = (String) getIntent().getStringExtra("id");

        final EditText edit_nazwa = ((EditText) findViewById(R.id.edit_funkcja));
        final EditText edit_stawka = ((EditText) findViewById(R.id.edit_stawka));
        final Button button_funkcja_zatwierdz = ((Button) findViewById(R.id.button_funkcja_dodaj));

        button_funkcja_zatwierdz.setText("ZAPISZ ZMIANY");

        String query = "SELECT * FROM funkcja WHERE id_funkcja LIKE '" + id + "'";

        String exit = null;

        try {
            exit = new DatabaseSelect(this).execute("select_", username, password, query).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] wiersze = exit.split("\\|");
        final String[][] kolumny = new String[wiersze.length][];
        int j = 0;
        for (String string : wiersze)
            kolumny[j++] = string.split("&");

        edit_nazwa.setText(kolumny[0][1]);
        edit_stawka.setText(kolumny[0][2]);

        button_funkcja_zatwierdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nazwa, stawka;
                nazwa = String.valueOf(edit_nazwa.getText());
                stawka = String.valueOf(edit_stawka.getText());

                if (nazwa.equals("") || stawka.equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Niepoprawnie uzupełniony formularz!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                try {
                    String query = "UPDATE `funkcja` SET `nazwa` = '" + nazwa + "', `podstawa` = '" +
                            stawka + "' WHERE `funkcja`.`id_funkcja` = '" + id + "'";
                    String exit = new DatabaseSelect(AdminEdytujFunkcje.this).
                            execute("insert", username, password, query).get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Pomyślnie zaktualizowano funkcję!", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        });
    }
}
