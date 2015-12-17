package it.negro.contabilitapp.activities;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import it.negro.contabilitapp.R;
import it.negro.contabilitapp.WhereWeAre;

public class BaseContabilitaActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        where(WhereWeAre.NOWHERE, null);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        createDrawer();
    }

    protected void createDrawer(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        drawerList.setAdapter(getDrawerListViewAdapter());
        final BaseContabilitaActivity activity = this;
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.onItemClick(parent, view, position, id);
                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);
            }
        });
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

    protected ListAdapter getDrawerListViewAdapter(){
        return null;
    }

    protected void onItemClick (AdapterView<?> parent, View view, int position, long id){
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void where(String where, Fragment fragment) {
        WhereWeAre.in(where, this, fragment);
    }

    public void setMenuItemVisible(Integer itemId, boolean visible) {
        if (!isDrawerOpen() && this.menu != null)
            this.menu.findItem(itemId).setVisible(visible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public boolean isOptionItemSelected(MenuItem item){
        return actionBarDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(false);
        return true;
    }

    public boolean isDrawerOpen(){
        if (drawerLayout == null)
            return false;
        return drawerLayout.isDrawerOpen(drawerList);
    }
}
