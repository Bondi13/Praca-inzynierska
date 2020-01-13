package com.example.system1_42;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdminWypiszUzytkownicy extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dodaj, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_wyloguj_dodaj:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_dodaj:
                intent = new Intent(getApplicationContext(), AdminDodajUzytkownika.class);
                final String username = (String) getIntent().getStringExtra("username");
                final String password = (String) getIntent().getStringExtra("password");
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_uzytkownicy);

        final ListView listView = (ListView)findViewById(R.id.list_view);
        final String username = (String)getIntent().getStringExtra("username");
        final String password = (String)getIntent().getStringExtra("password");
        setTitle(username);

        String exit = "";
        String query = "select name, password, dostep, id from users order by id";

        try {
            exit = new DatabaseSelect(this).execute("select", username, password, query).get();

            String[] strings = exit.split("\\|");
            String[][] strings1 = new String[strings.length][];
            int i = 0;
            for(String string : strings)
                strings1[i++] = string.split("-");

            final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

            for(i = 0; i < strings.length; i++){
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("login", strings1[i][0]);
                hashMap.put("dostep", strings1[i][2]);
                hashMap.put("id", strings1[i][3]);
                arrayList.add(hashMap);
            }

            String[] from = {"login", "dostep", "id"};
            int[] to = {R.id.row_login, R.id.row_access, R.id.row_id_user};
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.row_uzytkownicy, from, to);
            listView.setAdapter(simpleAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView viewById = view.findViewById(R.id.row_id_user);
                    String id_user = (String) viewById.getText();

                    Intent intent = new Intent(AdminWypiszUzytkownicy.this, AdminEdytujUzytkownika.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("id", id_user);
                    startActivity(intent);
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
