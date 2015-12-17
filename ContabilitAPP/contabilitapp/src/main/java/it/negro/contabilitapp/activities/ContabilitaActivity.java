package it.negro.contabilitapp.activities;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import it.negro.contabilitapp.*;
import it.negro.contabilitapp.fragments.HomeFragment;
import it.negro.contabilitapp.fragments.MovimentiFragment;
import it.negro.contabilitapp.fragments.SaldoFragment;

public class ContabilitaActivity extends BaseContabilitaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_contabilita);
    }

    @Override
    protected ListAdapter getDrawerListViewAdapter() {
        String[] titles = getResources().getStringArray(R.array.menu_items);
        return new ArrayAdapter<String>(this, R.layout.menu_item, titles);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(it.negro.contabilitapp.R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Fragment homeFragment = new HomeFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.isOptionItemSelected(item))
            return true;
        switch (item.getItemId()) {
            case R.id.action_search:
//                Do something
                return true;
            case R.id.action_settings:
//                Do something
                return true;
            case R.id.action_add_movimento:
//                Do something
                return true;
            case R.id.action_add_movimento_mic:
                MovimentiFragment fragment = (MovimentiFragment) getFragmentManager().findFragmentByTag(WhereWeAre.MOVIMENTI);
                fragment.promptSpeech();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setMenuItemVisible(R.id.action_settings, true);
        setMenuItemVisible(R.id.action_search, true);
        if (WhereWeAre.isIn(WhereWeAre.MOVIMENTI)){
            setMenuItemVisible(R.id.action_add_movimento, true);
            setMenuItemVisible(R.id.action_add_movimento_mic, true);
        }else if (WhereWeAre.isIn(WhereWeAre.DETTAGLIO_MOVIMENTO)){
            setMenuItemVisible(R.id.action_modify, true);
        }
        return true;
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                Fragment homeFragment = new HomeFragment();
                getFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                break;
            case 1:
                Fragment saldoFragment = new SaldoFragment();
                getFragmentManager().beginTransaction().replace(R.id.container, saldoFragment).commit();
                break;
            case 2:
                Fragment fragment = MovimentiFragment.newInstance();
                fragment.setRetainInstance(true);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, WhereWeAre.MOVIMENTI).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (WhereWeAre.isIn(WhereWeAre.HOME)){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Chiusura");
            alertBuilder.setMessage("Sicuro di uscire?");
            alertBuilder.setCancelable(false);
            alertBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    closeApp();
                }
            });
            alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }else if (WhereWeAre.isIn(WhereWeAre.DETTAGLIO_MOVIMENTO)) {
            Fragment dettaglioMovimento = getFragmentManager().findFragmentByTag(WhereWeAre.DETTAGLIO_MOVIMENTO);
            Fragment movimenti = getFragmentManager().findFragmentByTag(WhereWeAre.MOVIMENTI);
            getFragmentManager().beginTransaction().remove(dettaglioMovimento).commit();
            getFragmentManager().beginTransaction().show(movimenti).commit();
            where(WhereWeAre.MOVIMENTI, movimenti);
        } else{
            Fragment fragment = new HomeFragment();
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    private void closeApp(){
        super.onBackPressed();
    }

}
