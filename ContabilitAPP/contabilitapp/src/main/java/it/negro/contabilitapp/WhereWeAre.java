package it.negro.contabilitapp;

import android.app.Activity;
import android.app.Fragment;
import it.negro.contabilitapp.activities.BaseContabilitaActivity;

import java.util.HashMap;
import java.util.Map;

public class WhereWeAre {

    public static final String HOME = "riepilogo";
    public static final String MOVIMENTI = "movimenti";
    public static final String DETTAGLIO_MOVIMENTO = "dettaglioMovimento";
    public static final String SALDO = "saldo";
    public static final String NOWHERE = "nowhere";

    private String place = NOWHERE;
    private BaseContabilitaActivity activity;
    private Fragment fragment;
    private static final Map<String, Map<Integer, Boolean>> MENU_OPTIONS_VISIBILITY = new HashMap<String, Map<Integer, Boolean>>();
    {
        Map<Integer, Boolean> homeMap = new HashMap<Integer, Boolean>();
        homeMap.put(R.id.action_settings, true);
        homeMap.put(R.id.action_search, true);
        homeMap.put(R.id.action_add_movimento, false);
        homeMap.put(R.id.action_add_movimento_mic, false);
        homeMap.put(R.id.action_modify, false);

        MENU_OPTIONS_VISIBILITY.put(NOWHERE, homeMap);
        MENU_OPTIONS_VISIBILITY.put(HOME, homeMap);
        MENU_OPTIONS_VISIBILITY.put(SALDO, homeMap);

        Map<Integer, Boolean> movimentiMap = new HashMap<Integer, Boolean>();
        movimentiMap.put(R.id.action_settings, true);
        movimentiMap.put(R.id.action_search, true);
        movimentiMap.put(R.id.action_add_movimento, true);
        movimentiMap.put(R.id.action_add_movimento_mic, true);
        movimentiMap.put(R.id.action_modify, false);
        MENU_OPTIONS_VISIBILITY.put(MOVIMENTI, movimentiMap);

        Map<Integer, Boolean> dettaglioMovimentoMap = new HashMap<Integer, Boolean>();
        dettaglioMovimentoMap.put(R.id.action_settings, true);
        dettaglioMovimentoMap.put(R.id.action_search, true);
        dettaglioMovimentoMap.put(R.id.action_add_movimento, false);
        dettaglioMovimentoMap.put(R.id.action_add_movimento_mic, false);
        dettaglioMovimentoMap.put(R.id.action_modify, true);
        MENU_OPTIONS_VISIBILITY.put(DETTAGLIO_MOVIMENTO, dettaglioMovimentoMap);
    }
    private static WhereWeAre instance = new WhereWeAre();

    private WhereWeAre (){}

    public static void in(String place, BaseContabilitaActivity activity, Fragment fragment){
        instance.activity = activity;
        instance.place = place;
        instance.fragment = fragment;
        prepareOptionsMenu();
    }

    public static boolean isIn(String place){
        return place.equals(instance.place);
    }

    public static String where (){
        return instance.place;
    }

    public static Activity activity (){
        return instance.activity;
    }
    public static Fragment fragment (){
        return instance.fragment;
    }

    public static void prepareOptionsMenu(){
        Map<Integer, Boolean> mapToApply = MENU_OPTIONS_VISIBILITY.get(where());
        for (Map.Entry<Integer, Boolean> entry : mapToApply.entrySet()) {
            instance.activity.setMenuItemVisible(entry.getKey(), entry.getValue());
        }
    }
}
