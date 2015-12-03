package it.negro.contabilitapp;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private String[] titles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = getResources().getStringArray(R.array.menu_items);
        title = titles[0];
        setTitle(title);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_item, titles));
        drawerList.setOnItemClickListener(new MenuItemClickListener());
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
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
                ((MovimentiFragment)WhereWeAre.fragment()).promptSpeech();
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
            if (WhereWeAre.is(WhereWeAre.MOVIMENTI)) {
                menu.findItem(R.id.action_add_movimento).setVisible(true);
                menu.findItem(R.id.action_add_movimento_mic).setVisible(true);
            }
        }
        return true;
    }

    private class MenuItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 2){
                Fragment fragment = MovimentiFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                WhereWeAre.in(WhereWeAre.MOVIMENTI, fragment);
            }else {
                Fragment fragment = new MenuItemFragment();
                Bundle args = new Bundle();
                args.putInt(MenuItemFragment.ARG_MENU_ITEM_POSITION, position);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                if (position == 0)
                    WhereWeAre.in(WhereWeAre.HOME, fragment);
                else
                    WhereWeAre.in(WhereWeAre.SALDO, fragment);
            }
            drawerList.setItemChecked(position, true);
            setTitle(titles[position]);
            title = titles[position];
            drawerLayout.closeDrawer(drawerList);
        }
    }

    public static class MenuItemFragment extends Fragment {

        public static final String ARG_MENU_ITEM_POSITION = "arg_menu_item_position";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.menu_item, container, false);
            String title = getResources().getStringArray(R.array.menu_items)[getArguments().getInt(ARG_MENU_ITEM_POSITION)];
            getActivity().setTitle(title);
            return rootView;
        }
    }

}
