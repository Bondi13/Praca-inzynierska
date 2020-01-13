package com.example.system1_42;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDodajFunkcje extends AppCompatActivity {

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
        setContentView(R.layout.admin_dodaj_funkcje);

        final String[] exit = {""};

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        final EditText edit_nazwa = (EditText) findViewById(R.id.edit_funkcja);
        final EditText edit_stawka = (EditText) findViewById(R.id.edit_stawka);

        ((Button) findViewById(R.id.button_funkcja_dodaj)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String nazwa, stawka;
                    nazwa = String.valueOf(edit_nazwa.getText());
                    stawka = String.valueOf(edit_stawka.getText());

                    if (!nazwa.equals("") && !stawka.equals("")) {
                        String query = "INSERT INTO `funkcja` (`id_funkcja`, `nazwa`, `podstawa`) " +
                                "VALUES (NULL, '" + nazwa + "', '" + stawka + "')";

                        exit[0] = new DatabaseSelect(AdminDodajFunkcje.this).
                                execute("insert", username, password, query).get();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Pomyślnie dodano funkcję!", Toast.LENGTH_LONG);
                        toast.show();
                        finish();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Błędnie wypełniony formularz!", Toast.LENGTH_LONG);
                        toast.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
