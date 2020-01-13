package com.example.system1_42;

public class Podatki {
    boolean czy_dojezdza;
    float brutto;
    float stawka_dzienna;
    float stawka_dzienna_na_L4;
    float ogolny_przychod;
    float dodatki;
    float spoleczne_emerytalne;
    float spoleczne_rentowe_pracownik;
    float spoleczne_rentowe_pracowdawca;
    float spoleczne_chorobowe;
    float spoleczne_wypadkowe;
    float koszty_uzyskania_przychodu;
    float podstawa_naliczania_ub_zdrowotnego;
    float ub_zdrowotne;
    float przychod_pomniejszony_o_koszty;
    float zaliczka_na_podatek_dochodowy_przed_skladka;
    float zaliczka_na_podatek_dochodowy;
    float netto;
    float fundusz_pracy;
    float fundusz_gwarantowanych_swiadczen_pracowniczych;
    float suma_kosztow_zatrudnienia;
    float koszt_calkowity_zatrudnienia;
    float podstawa_wymiaru_skladek;
    double premia;
    int dni_na_zwolnieniu;
    int dni_nieobecne;

    Podatki(float brutto, boolean czy_dojezdza, float dodatki,
            int dni_na_zwolnieniu, double premia, int dni_nieobecne) {
        this.brutto = (float) (brutto * (premia + 1));
        this.czy_dojezdza = czy_dojezdza;
        this.dodatki = dodatki;
        this.dni_na_zwolnieniu = dni_na_zwolnieniu;
        this.premia = premia;
        this.dni_nieobecne = dni_nieobecne;
        policz();
    }

    void policz() {
        stawka_dzienna = brutto / 30;
        stawka_dzienna_na_L4 = (float) (stawka_dzienna * 0.8);
        ogolny_przychod = dni_na_zwolnieniu * stawka_dzienna_na_L4 +
                (30 - dni_na_zwolnieniu - dni_nieobecne) * stawka_dzienna;
        System.out.println(ogolny_przychod);
        podstawa_wymiaru_skladek = (30 - dni_na_zwolnieniu - dni_nieobecne) * stawka_dzienna;

        spoleczne_emerytalne = (float) (podstawa_wymiaru_skladek * 0.0976);
        spoleczne_rentowe_pracownik = (float) (podstawa_wymiaru_skladek * 0.015);
        spoleczne_rentowe_pracowdawca = (float) (podstawa_wymiaru_skladek * 0.065);
        spoleczne_wypadkowe = (float) (podstawa_wymiaru_skladek * 0.0084);
        spoleczne_chorobowe = (float) (podstawa_wymiaru_skladek * 0.0245);
        fundusz_pracy = (float) (podstawa_wymiaru_skladek * 0.0245);
        fundusz_gwarantowanych_swiadczen_pracowniczych = (float) (podstawa_wymiaru_skladek * 0.001);
        suma_kosztow_zatrudnienia = spoleczne_emerytalne + spoleczne_rentowe_pracowdawca +
                spoleczne_wypadkowe + fundusz_pracy + fundusz_gwarantowanych_swiadczen_pracowniczych;
        koszt_calkowity_zatrudnienia = brutto + suma_kosztow_zatrudnienia + dodatki;

        if (czy_dojezdza == true)
            koszty_uzyskania_przychodu = (float) 139.06;
        else
            koszty_uzyskania_przychodu = (float) 111.25;
        podstawa_naliczania_ub_zdrowotnego = ogolny_przychod - spoleczne_emerytalne -
                spoleczne_rentowe_pracownik - spoleczne_chorobowe;
        ub_zdrowotne = (float) (podstawa_naliczania_ub_zdrowotnego * 0.09);
        przychod_pomniejszony_o_koszty = (float) Math.ceil(podstawa_naliczania_ub_zdrowotnego -
                koszty_uzyskania_przychodu);
        zaliczka_na_podatek_dochodowy_przed_skladka = (float) (przychod_pomniejszony_o_koszty * 0.18 - 46.33);
        zaliczka_na_podatek_dochodowy = (float) Math.floor(zaliczka_na_podatek_dochodowy_przed_skladka -
                podstawa_naliczania_ub_zdrowotnego * 0.0775);
        netto = podstawa_naliczania_ub_zdrowotnego - ub_zdrowotne - zaliczka_na_podatek_dochodowy + dodatki;
    }

    public float getNetto() {
        return (float) (Math.round(netto * 100)) / 100;
    }

    public float getBrutto() {
        return (float) (Math.floor((ogolny_przychod) * 100) / 100);
    }

    public float getPodatki() {
        return (float) Math.floor((brutto - getNetto()) * 100) / 100;
    }

    public float getSpoleczne_emerytalne() {
        return (float) (Math.floor((spoleczne_emerytalne) * 100) / 100);
    }

    public float getSpoleczne_rentowe_pracownik() {
        return spoleczne_rentowe_pracownik;
    }

    public float getSpoleczne_chorobowe() {
        return (float) (Math.floor((spoleczne_chorobowe) * 100) / 100);
    }

    public float getUb_zdrowotne() {
        return (float) (Math.floor((ub_zdrowotne) * 100) / 100);
    }

    public float getZaliczka_na_podatek_dochodowy() {
        return zaliczka_na_podatek_dochodowy;
    }

    public float getSpoleczne_rentowe_pracowdawca() {
        return (float) (Math.floor((spoleczne_rentowe_pracowdawca) * 100) / 100);
    }

    public float getSpoleczne_wypadkowe() {
        return (float) (Math.floor((spoleczne_wypadkowe) * 100) / 100);
    }

    public float getFundusz_pracy() {
        return (float) (Math.floor((fundusz_pracy) * 100) / 100);
    }

    public float getFundusz_gwarantowanych_swiadczen_pracowniczych() {
        return (float) (Math.floor((fundusz_gwarantowanych_swiadczen_pracowniczych) * 100) / 100);
    }

    public float getSuma_kosztow_zatrudnienia() {
        return (float) (Math.floor((suma_kosztow_zatrudnienia) * 100) / 100);
    }

    public float getKoszt_calkowity_zatrudnienia() {
        return (float) (Math.floor((koszt_calkowity_zatrudnienia) * 100) / 100);
    }
}
