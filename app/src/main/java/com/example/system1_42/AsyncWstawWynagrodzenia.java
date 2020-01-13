package com.example.system1_42;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import androidx.annotation.RequiresApi;

public class AsyncWstawWynagrodzenia extends AsyncTask {

    String username, password;
    Context context;
    ProgressBar progressBar;

    AsyncWstawWynagrodzenia(String username, String password, Context context, ProgressBar progressBar) {
        this.username = username;
        this.password = password;
        this.context = context;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast toast = Toast.makeText(context, "Pomyślnie uzupełniono wynagrodzenia!", Toast.LENGTH_LONG);
        toast.show();
        super.onPostExecute(o);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Object doInBackground(Object[] objects) {

        String query = "select id_pracownik, podstawa from pracownik p join funkcja f " +
                "on p.id_funkcja = f.id_funkcja order by id_pracownik";

        DataGetter dataGetter = new DataGetter();
        String miesiac, rok;
        miesiac = dataGetter.getMiesiac();
        rok = dataGetter.getRok();

        final String[] exit = {""};
        try {
            exit[0] = new DatabaseSelect(context).
                    execute("select_user_spinner", username, password, query).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] strings = exit[0].split("\\|");

        for (String string : strings) {
            int iend = string.indexOf(" ");
            String id_pracownik = "";
            if (iend != -1) {
                id_pracownik = string.substring(0, iend);
            }
            query = "select suma from podsumowanie_zaang where id_pracownik like '" +
                    id_pracownik + "' and miesiac like '" +
                    miesiac + "' and rok like '" + rok + "'";
            try {
                exit[0] = new DatabaseSelect(context).
                        execute("select_one_thing", username, password, query).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String suma_zaang = exit[0];

            String podstawa_dzienna = "";
            if (iend != -1) {
                podstawa_dzienna = string.substring(iend + 2, string.length());
                podstawa_dzienna = podstawa_dzienna.replaceAll("^\\d", "");
                podstawa_dzienna = podstawa_dzienna.substring(1);
                //id_pracownik, suma_zaang, podstawa_dzienna
                if (String.valueOf(suma_zaang).equals(""))
                    suma_zaang = "0";

                String id_premia = "1";
                if (suma_zaang != null) {
                    Premie premie = new Premie(Float.parseFloat(suma_zaang), username, password, context);
                    id_premia = String.valueOf(premie.getId());
                }

                String stawka_miesieczna = String.valueOf(dataGetter.
                        getLiczba_dni() * Float.parseFloat(podstawa_dzienna));

                query = "DELETE FROM `wynagrodzenie` WHERE id_pracownik LIKE '" + id_pracownik +
                        "' AND miesiac LIKE '" + miesiac + "' AND rok LIKE '" + rok + "'";
                System.out.println(query);/////////////////////////////////////////////////////////
                try {
                    exit[0] = new DatabaseSelect(context).
                            execute("insert", username, password, query).get();
                    SystemClock.sleep(200);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                query = "INSERT INTO `wynagrodzenie` (`id_wynagrodzenie`, `id_pracownik`, " +
                        "`id_premia`, `miesiac`, `rok`, `kwota`, `dodatek`) VALUES " +
                        "(NULL, '" + id_pracownik + "', '" + id_premia + "', '" + miesiac +
                        "', '" + rok + "', '" + stawka_miesieczna + "', '10')";

                try {
                    exit[0] = new DatabaseSelect(context).
                            execute("insert", username, password, query).get();
                    System.out.println(query);////////////////////////////////////////////
                    SystemClock.sleep(200);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            SystemClock.sleep(100);
        }

        return null;
    }
}
