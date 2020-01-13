package com.example.system1_42;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminSzczegolyWynagrodzen extends AppCompatActivity {

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
        setContentView(R.layout.admin_szczegoly_wynagrodzen);

        final String username = (String)getIntent().getStringExtra("username");
        final String password = (String)getIntent().getStringExtra("password");
        setTitle(username);

        final float brutto = Float.parseFloat(getIntent().getStringExtra("brutto"));
        final float dodatki = Float.parseFloat(getIntent().getStringExtra("dodatki"));
        final int dni_na_zwolnieniu = Integer.parseInt(getIntent().getStringExtra("dni_na_zwolnieniu"));
        final double premia = Double.parseDouble(getIntent().getStringExtra("premia"));
        final String data = (String)getIntent().getStringExtra("data");
        final String nazwisko = (String)getIntent().getStringExtra("nazwisko");
        final int dni_nieobecne = Integer.parseInt(getIntent().getStringExtra("dni_nieobecne"));

        Podatki podatki = new Podatki(brutto, false, dodatki, dni_na_zwolnieniu, premia, dni_nieobecne);

        TextView spoleczne_emerytalne = ((TextView)findViewById(R.id.admin_spoleczne_emerytalne));
        TextView spoleczne_rentowe = ((TextView)findViewById(R.id.admin_spoleczne_rentowe));
        TextView spoleczne_wypadkowe = ((TextView)findViewById(R.id.admin_spoleczne_wypadkowe));
        TextView fundusz_pracy = ((TextView)findViewById(R.id.admin_fundusz_pracy));
        TextView fundusz_gwarantowanych_swiadczen_pracowniczych = ((TextView)findViewById
                (R.id.admin_fundusz_gwarantowanych_swiadczen_pracowniczych));
        TextView suma_kosztow_zatrudnienia = ((TextView)findViewById(R.id.admin_suma_kosztow_zatrudnienia));
        TextView koszt_calkowity_zatrudnienia = ((TextView)findViewById(R.id.admin_koszt_calkowity_zatrudnienia));
        TextView view_brutto = ((TextView)findViewById(R.id.admin_szczegoly_brutto));
        TextView view_premia = ((TextView)findViewById(R.id.admin_szczegoly_premia));
        TextView view_data = ((TextView)findViewById(R.id.admin_szczegoly_data));

        view_data.setText(data);
        view_brutto.setText(String.valueOf(brutto));
        view_premia.setText(String.valueOf(premia * 100) + "%");
        spoleczne_emerytalne.setText(String.valueOf(podatki.getSpoleczne_emerytalne()));
        spoleczne_rentowe.setText(String.valueOf(podatki.getSpoleczne_rentowe_pracowdawca()));
        spoleczne_wypadkowe.setText(String.valueOf(podatki.getSpoleczne_wypadkowe()));
        fundusz_pracy.setText(String.valueOf(podatki.getFundusz_pracy()));
        fundusz_gwarantowanych_swiadczen_pracowniczych.setText(String.valueOf(podatki.getFundusz_gwarantowanych_swiadczen_pracowniczych()));
        suma_kosztow_zatrudnienia.setText(String.valueOf(podatki.getSuma_kosztow_zatrudnienia()));
        koszt_calkowity_zatrudnienia.setText(String.valueOf(podatki.getKoszt_calkowity_zatrudnienia()));
    }
}
