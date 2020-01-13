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

public class AdminEdytujUzytkownika extends AppCompatActivity {

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
                String query = "DELETE FROM users WHERE id LIKE " + id;
                final String username = (String) getIntent().getStringExtra("username");
                final String password = (String) getIntent().getStringExtra("password");
                try {
                    String exit = new DatabaseSelect(AdminEdytujUzytkownika.this).
                            execute("insert", username, password, query).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Pomyślnie usunięto użytkownika!", Toast.LENGTH_LONG);
                toast.show();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dodaj_uzytkownika);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);
        id = (String) getIntent().getStringExtra("id");

        final EditText edit_login = ((EditText) findViewById(R.id.edit_login));
        final EditText edit_pass = ((EditText) findViewById(R.id.edit_password));
        final EditText edit_rep_pass = ((EditText) findViewById(R.id.edit_rep_password));
        final EditText edit_access = ((EditText) findViewById(R.id.edit_access));
        final EditText edit_email = ((EditText) findViewById(R.id.edit_email));
        final TextView dodaj_haslo = ((TextView) findViewById(R.id.dodaj_haslo));
        final Button button_user_zatwierdz = ((Button) findViewById(R.id.button_user_zatwierdz));

        dodaj_haslo.setText("nowe hasło: ");
        edit_pass.setHint("zostaw puste, aby nie zmienić");
        button_user_zatwierdz.setText("ZAPISZ ZMIANY");

        String query = "SELECT * FROM users WHERE id LIKE '" + id + "'";

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

        edit_login.setText(kolumny[0][1]);
        edit_access.setText(kolumny[0][4]);
        edit_email.setText(kolumny[0][2]);

        button_user_zatwierdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, password, email, dostep;
                name = String.valueOf(edit_login.getText());
                password = String.valueOf(edit_pass.getText());
                email = String.valueOf(edit_email.getText());
                dostep = String.valueOf(edit_access.getText());

                if (password.equals(""))
                    password = kolumny[0][3];
                else {
                    String pass_rep = String.valueOf(edit_rep_pass.getText());
                    if (!pass_rep.equals(password)) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Niepoprawnie uzupełniony formularz!", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                }

                try {
                    String query = "UPDATE `users` SET `name` = '" + name + "', `email` = '" +
                            email + "', `password` = '" + password + "', `dostep` = '" +
                            dostep + "' WHERE `users`.`id` = '" + id + "'";

                    String exit = new DatabaseSelect(AdminEdytujUzytkownika.this).
                            execute("insert", username, password, query).get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Pomyślnie zaktualizowano użytkownika!", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        });
    }
}
