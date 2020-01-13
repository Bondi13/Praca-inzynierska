package com.example.system1_42;

import android.os.Build;

import java.time.LocalDate;

import androidx.annotation.RequiresApi;

public class DataGetter {
    String miesiac, rok;
    int miesiacLiczba;
    Integer liczba_dni;

    @RequiresApi(api = Build.VERSION_CODES.O)
    DataGetter() {
        LocalDate localDate = LocalDate.now();
        String data = String.valueOf(localDate);
        String[] string = data.split("-");

        if (string[1].equals("01"))
            miesiac = "styczeń";
        if (string[1].equals("02"))
            miesiac = "luty";
        if (string[1].equals("03"))
            miesiac = "marzec";
        if (string[1].equals("04"))
            miesiac = "kwiecień";
        if (string[1].equals("05"))
            miesiac = "maj";
        if (string[1].equals("06"))
            miesiac = "czerwiec";
        if (string[1].equals("07"))
            miesiac = "lipiec";
        if (string[1].equals("08"))
            miesiac = "sierpień";
        if (string[1].equals("09"))
            miesiac = "wrzesień";
        if (string[1].equals("10"))
            miesiac = "paździeernik";
        if (string[1].equals("11"))
            miesiac = "listopad";
        else
            miesiac = "grudzień";

        rok = string[0];
        miesiacLiczba = Integer.parseInt(string[1]);
        liczba_dni = 31;

        Integer m = miesiacLiczba;
        if ((m % 2 == 0 && m < 7) || (m % 2 == 1 && m > 7))
            liczba_dni = 30;
        if (m == 2)
            liczba_dni = 28;
        // DODAć DNI PRZESTĘPNE;
    }

    public String getMiesiac() {
        return miesiac;
    }

    public String getRok() {
        return rok;
    }

    public int getMiesiacLiczba() {
        return miesiacLiczba;
    }

    public Integer getLiczba_dni() {
        return liczba_dni;
    }
}
