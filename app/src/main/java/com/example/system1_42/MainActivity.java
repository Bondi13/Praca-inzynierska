package com.example.system1_42;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText login, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Zaloguj się by zacząć");

        login = (EditText) findViewById((R.id.edit_login));
        pass = (EditText) findViewById((R.id.edit_password));
    }

    public void loginBtn(View view) throws ExecutionException, InterruptedException {

        if (isNetworkAvailable()) {
            String username = login.getText().toString();
            String password = pass.getText().toString();
            String exit = "";
            String query = "select * from users where name like '" + username +
                    "' and password like '" + password + "'";
            exit = new DatabaseSelect(this).execute("login", username, password, query).get();
            int x = 0;
            Intent intent;
            try {
                x = Integer.parseInt(exit.substring(exit.length() - 1));
                exit = exit.replaceAll("\\d", "");
                if (exit.equals("admin")) {
                    intent = new Intent(MainActivity.this, StronaStartowaAdmin.class);
                } else {
                    intent = new Intent(MainActivity.this, StronaStartowaPracownik.class);
                }
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
            } catch (NumberFormatException e) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Błąd logowania!");
                alertDialog.setMessage("Spróbuj jeszcze raz");
                alertDialog.show();
            }
        }else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Brak dostępu do internetu!", Toast.LENGTH_LONG);
            toast.show();}
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
