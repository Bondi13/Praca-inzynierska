package com.example.system1_42;

import android.content.Context;

import java.util.concurrent.ExecutionException;

public class Premie {
    String username, password;
    double suma;
    Context context;
    double premia;
    int id;

    Premie(double suma, String username, String password, Context context) {
        this.username = username;
        this.password = password;
        this.suma = suma;
        this.context = context;
        policz();
    }

    void policz() {
        double premia = 0.;

        try {
            String query = "select id_premia, procent, suma from premie order by suma";

            String exit = new DatabaseSelect(context).
                    execute("select_", username, password, query).get();

            String wiersze[] = exit.split("\\|");
            String kolumny[][] = new String[wiersze.length][];
            int i = 0;
            for (String string : wiersze)
                kolumny[i++] = string.split("&");

            double pamiec = 0.;

            for (i = 0; i < wiersze.length; i++) {
                if (Double.parseDouble(kolumny[i][2]) >= 0 && suma >= Double.parseDouble(kolumny[i][2])) {
                    this.premia = Double.parseDouble(kolumny[i][1]);
                    this.id = Integer.parseInt(kolumny[i][0]);
                } else if (Double.parseDouble(kolumny[i][2]) < 0 && suma <= Double.parseDouble(kolumny[i][2])
                        && !(pamiec <= Double.parseDouble(kolumny[i][2]))) {
                    this.premia = Double.parseDouble(kolumny[i][1]);
                    this.id = Integer.parseInt(kolumny[i][0]);
                    pamiec = Double.parseDouble(kolumny[i][2]);
                }
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getPremia() {
        return premia;
    }

    public int getId() {
        return id;
    }
}
