package com.example.system1_42;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PracownikPredykcjaSzczegoly extends AppCompatActivity {

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
        setContentView(R.layout.pracownik_predykcja_szczegoly);

        final String username = (String) getIntent().getStringExtra("username");
        final String password = (String) getIntent().getStringExtra("password");
        setTitle(username);
        final String data = (String) getIntent().getStringExtra("data");
        final String brutto = (String) getIntent().getStringExtra("brutto");
        final String dni_zwolnienia = (String) getIntent().getStringExtra("dni_zwolnienia");
        final boolean czy_dojezdza = getIntent().getBooleanExtra("czy_dojezdza", true);
        final double premia = getIntent().getDoubleExtra("premia", 0);

        Integer dni_zwolnienia_integer = 0;
        if (!dni_zwolnienia.equals(""))
            dni_zwolnienia_integer = Integer.valueOf(dni_zwolnienia);

        Podatki podatki = new Podatki(Float.valueOf(brutto), czy_dojezdza, 10,
                dni_zwolnienia_integer, premia, 0);

        TextView predict_szczegoly_brutto = ((TextView) findViewById(R.id.predict_szczegoly_brutto));
        TextView predict_szczegoly_netto = ((TextView) findViewById(R.id.predict_szczegoly_netto));
        TextView predict_szczegoly_emerytalne = ((TextView) findViewById(R.id.predict_szczegoly_emerytalne));
        TextView predict_szczegoly_rentowe = ((TextView) findViewById(R.id.predict_szczegoly_rentowe));
        TextView predict_szczegoly_chorobowe = ((TextView) findViewById(R.id.predict_szczegoly_chorobowe));
        TextView predict_szczegoly_zdrowotne = ((TextView) findViewById(R.id.predict_szczegoly_zdrowotne));
        TextView predict_szczegoly_dochodowy = ((TextView) findViewById(R.id.predict_szczegoly_dochodowy));
        TextView predict_szczegoly_data = ((TextView) findViewById(R.id.predict_szczegoly_data));
        TextView predict_szczegoly_podatki = ((TextView) findViewById(R.id.predict_szczegoly_podatki));
        predict_szczegoly_data.setText(data);
        predict_szczegoly_brutto.setText(String.valueOf(podatki.getBrutto()));
        predict_szczegoly_netto.setText(String.valueOf(podatki.getNetto()));
        predict_szczegoly_emerytalne.setText(String.valueOf(podatki.getSpoleczne_emerytalne()));
        predict_szczegoly_rentowe.setText(String.valueOf(podatki.getSpoleczne_rentowe_pracownik()));
        predict_szczegoly_chorobowe.setText(String.valueOf(podatki.getSpoleczne_chorobowe()));
        predict_szczegoly_zdrowotne.setText(String.valueOf(podatki.getUb_zdrowotne()));
        predict_szczegoly_dochodowy.setText(String.valueOf(podatki.getZaliczka_na_podatek_dochodowy()));
        predict_szczegoly_podatki.setText(String.valueOf(podatki.getPodatki()));
    }
}
