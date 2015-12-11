package it.negro.contabilitapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import static it.negro.contabilitapp.WhereWeAre.*;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getClass().equals(MainActivity.class)) {
            super.setContentView(R.layout.activity_main);
            onCreateDrawer();
        }
    }

    protected void onCreateDrawer(){
        String[] titles = getResources().getStringArray(R.array.menu_items);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_item, titles));
        drawerList.setOnItemClickListener(new MenuItemClickListener(this));
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public void setContentView(int layoutResID){
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.container);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        onCreateDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        switch (item.getItemId()) {
            case R.id.action_add_movimento_mic:
//                ((MovimentiFragment) fragment()).promptSpeech();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(false);
        if (!drawerOpen) {
            menu.findItem(R.id.action_search).setVisible(true);
            menu.findItem(R.id.action_settings).setVisible(true);
            if (isIn(MOVIMENTI)) {
                menu.findItem(R.id.action_add_movimento).setVisible(true);
                menu.findItem(R.id.action_add_movimento_mic).setVisible(true);
            }
        }
        return true;
    }

    private class MenuItemClickListener implements ListView.OnItemClickListener {

        private Activity activity;

        public MenuItemClickListener (Activity activity){
            this.activity = activity;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Activity act = activity;
//            if (!act.getClass().equals(MainActivity.class))
//                act = activity.getParent();
            switch (position){
                case 0:
                    Intent intentHome = new Intent(act, HomeActivity.class);
                    act.startActivity(intentHome);
                    break;
                case 1:
                    Intent intentSaldo = new Intent(act, SaldoActivity.class);
                    act.startActivity(intentSaldo);
                    break;
                case 2:
                    Intent intentMovimenti = new Intent(act, MovimentiActivity.class);
                    act.startActivity(intentMovimenti);
                    break;
                default:
                    break;
            }
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);
//            if (!act.getClass().equals(MainActivity.class))
//                act.finish();
        }
    }

}
