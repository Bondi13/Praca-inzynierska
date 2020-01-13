package com.example.system1_42;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class PracownikPredykcja extends AppCompatActivity {

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
        setContentView(R.layout.pracownik_predykcja);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);

        final String miesiac, rok;
        DataGetter dataGetter = new DataGetter();
        miesiac = dataGetter.getMiesiac();
        rok = dataGetter.getRok();

        TextView predict_data = ((TextView) findViewById(R.id.predict_data));
        final String dataString = miesiac + " " + rok;
        predict_data.setText(dataString);

        TextView predict_dni = ((TextView) findViewById(R.id.predict_dni));
        predict_dni.setText("/" + dataGetter.getLiczba_dni());

        String query = "select podstawa from funkcja f join pracownik p " +
                "on p.id_funkcja = f.id_funkcja join users u " +
                "on p.id_user = u.id " +
                "where u.name = '" + username + "' and u.password = '" + password + "'";
        String exit = "";
        Float stawka = (float) 0.0;

        try {
            exit = new DatabaseSelect(this).execute("select_one_thing", username, password, query).get();
            stawka = Float.parseFloat(exit);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final EditText edit_predict = ((EditText) findViewById(R.id.edit_predict));
        final EditText edit_predict_dni_zwolnienia = ((EditText) findViewById(R.id.edit_predict_dni_zwolnienia));
        final float finalStawka = stawka;

        ((Button) findViewById(R.id.button_policz)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                policz(edit_predict, finalStawka, edit_predict_dni_zwolnienia, miesiac, rok, username, password);
            }
        });

        ((Button) findViewById(R.id.button_predykcja_szczegoly)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = String.valueOf(edit_predict.getText());
                String y = String.valueOf(edit_predict_dni_zwolnienia.getText());
                if (!x.equals("")) {
                    Intent intent = new Intent(PracownikPredykcja.this, PracownikPredykcjaSzczegoly.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    String value = String.valueOf(finalStawka * Float.parseFloat(x));
                    intent.putExtra("brutto", value);
                    intent.putExtra("data", dataString);
                    if (y.equals(""))
                        y = "0";
                    intent.putExtra("dni_zwolnienia", y);
                    boolean czy_dojezdza = czy_dojezdza(username, password);
                    intent.putExtra("czy_dojezdza", czy_dojezdza);
                    value = premia(username, password, miesiac, rok);
                    Premie premie = new Premie(Double.parseDouble(value), username, password, getApplicationContext());
                    Double premia = premie.getPremia();
                    intent.putExtra("premia", premia);
                    startActivity(intent);
                }
            }
        });
    }

    protected boolean czy_dojezdza(String username, String password) {
        String query = "SELECT czy_dojezdza, id_pracownik FROM pracownik p " +
                "JOIN users u ON p.id_user = u.id " +
                "WHERE name LIKE '" + username + "' AND password LIKE '" + password + "'";

        String exit = null;

        try {
            exit = new DatabaseSelect(this).execute("select", username, password, query).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] wiersze = exit.split("\\|");
        String[][] kolumny = new String[wiersze.length][];
        int j = 0;
        for (String string : wiersze)
            kolumny[j++] = string.split("-");
        if (kolumny[0][0].equals("0"))
            return false;
        else
            return true;
    }

    protected String premia(String username, String password, String miesiac, String rok) {
        String query = "SELECT suma FROM podsumowanie_zaang z WHERE id_pracownik LIKE " +
                "(SELECT id_pracownik FROM pracownik p JOIN users u ON p.id_user = u.id " +
                "WHERE name LIKE '" + username + "' AND password LIKE '" + password + "') " +
                "AND miesiac LIKE '" + miesiac + "' AND rok LIKE '" + rok + "'";

        String exit = null;

        try {
            exit = new DatabaseSelect(this).execute("select_", username, password, query).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] wiersze = exit.split("\\|");
        String[][] kolumny = new String[wiersze.length][];
        int j = 0;
        for (String string : wiersze)
            kolumny[j++] = string.split("&");
        if (!String.valueOf(kolumny[0][0]).equals(""))
            return kolumny[0][0];
        else
            return "0";
    }

    protected void policz(EditText editText, float finalStawka, EditText editText1,
                          String miesiac, String rok, String username, String password) {
        String x = String.valueOf(editText.getText());
        float przelicznik = 0;
        if (!x.equals("")) {
            przelicznik = Float.parseFloat(x);
            String dni_string = String.valueOf(editText1.getText());
            int dni_na_zwolnieniu = 0;
            if (!dni_string.equals(""))
                dni_na_zwolnieniu = Integer.parseInt(dni_string);

            boolean czy_dojezdza = czy_dojezdza(username, password);
            String value = premia(username, password, miesiac, rok);

            Premie premie = new Premie(Double.parseDouble(value), username, password, getApplicationContext());
            Double premia = premie.getPremia();

            Podatki podatki = new Podatki(finalStawka * przelicznik, czy_dojezdza,
                    10, dni_na_zwolnieniu, premia, 0);

            TextView predict_brutto = ((TextView) findViewById(R.id.predict_brutto));
            TextView predict_netto = ((TextView) findViewById(R.id.predict_netto));
            TextView predict_podatki = ((TextView) findViewById(R.id.predict_podatki));
            predict_brutto.setText(String.valueOf(podatki.getBrutto()));
            predict_netto.setText(String.valueOf(podatki.getNetto()));
            predict_podatki.setText(String.valueOf(podatki.getPodatki()));

        }
    }
}
