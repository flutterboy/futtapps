package it.negro.contabilitapp;

import android.app.Fragment;

public class WhereWeAre {

    public static final String HOME = "riepilogo";
    public static final String MOVIMENTI = "movimenti";
    public static final String DETTAGLIO_MOVIMENTO = "dettaglioMovimento";
    public static final String SALDO = "saldo";
    public static final String NOWHERE = "nowhere";

    private String place = NOWHERE;
    private Fragment fragment;

    private static WhereWeAre instance = new WhereWeAre();

    private WhereWeAre (){}

    public static void in(String place, Fragment fragment){
        instance.fragment = fragment;
        instance.place = place;
    }

    public static boolean isIn(String place){
        return place.equals(instance.place);
    }

    public static String where (){
        return instance.place;
    }

    public static Fragment fragment (){
        return instance.fragment;
    }
}
