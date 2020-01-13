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

public class AdminDodajUzytkownika extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
        setContentView(R.layout.admin_dodaj_uzytkownika);

        final String[] exit = {""};

        final String username = (String)getIntent().getStringExtra("username");
        final String password = (String)getIntent().getStringExtra("password");
        setTitle(username);

        final EditText edit_login = ((EditText)findViewById(R.id.edit_login));
        final EditText edit_pass = ((EditText)findViewById(R.id.edit_password));
        final EditText edit_rep_pass = ((EditText)findViewById(R.id.edit_rep_password));
        final EditText edit_access = ((EditText)findViewById(R.id.edit_access));
        final EditText edit_email = ((EditText)findViewById(R.id.edit_email));

        ((Button)findViewById(R.id.button_user_zatwierdz)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String login, email, pass, rep_pass, access;
                            login = "'" + String.valueOf(edit_login.getText()) + "'";
                            email = "'" + String.valueOf(edit_email.getText()) + "'";
                            pass = "'" + String.valueOf(edit_pass.getText()) + "'";
                            rep_pass = "'" + String.valueOf(edit_rep_pass.getText()) + "'";
                            access = "'" + String.valueOf(edit_access.getText()) + "'";

                            String query = "INSERT INTO `users` (`id`, `name`, `email`, `password`, " +
                                    "`dostep`) VALUES (NULL, " + login + ", " + email + ", " +
                                    pass + ", " + access + ")";

                            if(login.equals("") || !pass.equals(rep_pass) || access.equals("")) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Niepoprawnie uzupełniony formularz!", Toast.LENGTH_SHORT);
                                toast.show();
                            }else{
                                exit[0] = new DatabaseSelect(AdminDodajUzytkownika.this).
                                        execute("insert", username, password, query).get();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Pomyślnie dodano użytkownika!", Toast.LENGTH_LONG);
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
