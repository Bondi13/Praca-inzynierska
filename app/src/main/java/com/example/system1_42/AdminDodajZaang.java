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

public class AdminDodajZaang extends AppCompatActivity {

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dodaj_premie);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        DataGetter dataGetter = new DataGetter();
        final String miesiac = dataGetter.getMiesiac(), rok = dataGetter.getRok();

        TextView premie_data = ((TextView) findViewById(R.id.premie_data));
        final String dataString = miesiac + " " + rok;
        premie_data.setText(dataString);

        final String[] exit = {""};
        final String[] query = {"SELECT id_pracownik, imie, nazwisko FROM pracownik"};
        final Spinner edit_funkcje_pracownik = ((Spinner) findViewById(R.id.edit_funkcje_pracownik));

        try {//Wypisywanie pracowników do spinnera
            exit[0] = new DatabaseSelect(this).execute("select_three_array", username, password, query[0]).get();
            String[] strings = exit[0].split("\\|");
            String[][] strings1 = new String[strings.length][];
            int i = 0;
            for (String string : strings)
                strings1[i++] = string.split("-");

            List<String> list = new ArrayList<String>();
            for (String string : strings) {
                list.add(string);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.
                    support_simple_spinner_dropdown_item, list);
            arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            edit_funkcje_pracownik.setAdapter(arrayAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ((Button) findViewById(R.id.button_premie_dodaj)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String premia, id;
                EditText editText = ((EditText) findViewById(R.id.edit_premie_zaang));
                premia = "'" + editText.getText() + "'";
                id = edit_funkcje_pracownik.getSelectedItem().toString();
                id = "'" + id.replaceAll("\\D+", "") + "'";
                if (id.equals("''")) id = "'0'";

                query[0] = "DELETE FROM `podsumowanie_zaang` WHERE miesiac like '" + miesiac + "' " +
                        "and rok like '" + rok + "' and id_pracownik like " + id;
                try {
                    exit[0] = new DatabaseSelect(AdminDodajZaang.this).
                            execute("insert", username, password, query[0]).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                query[0] = "INSERT INTO `podsumowanie_zaang` (`id_zaang`, `id_pracownik`, `suma`, `miesiac`, " +
                        "`rok`) VALUES (NULL, " + id + ", " + premia + ", '" + miesiac + "', '" + rok + "');";
                try {
                    exit[0] = new DatabaseSelect(AdminDodajZaang.this).
                            execute("insert", username, password, query[0]).get();
                    editText.setText("");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Zaktualizowano premię pracownika!", Toast.LENGTH_LONG);
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
