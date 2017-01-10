package io.stoh.hackcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import io.stoh.hackcompanion.io.stoh.hackcompanion.data.MyMLHUser;
import io.stoh.hackcompanion.io.stoh.hackcompanion.navigation.HomeFragment;
import io.stoh.hackcompanion.io.stoh.hackcompanion.navigation.UserFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Observer {
    private String myMLHToken;
    private MyMLHUser myMLHUser = MyMLHUser.getInstance();
    private boolean isHackathonSelectorOpen = false;

    private NavigationView navigationView;
    private View navHeaderView;
    private TextView hackathonSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Activity will not load unless there is a token
        myMLHToken = getIntent().getStringExtra("myMLHToken");

        if (myMLHToken == null) {
            finishAffinity();
        }


        //////////////////////////////////////////////////////////////////////////////////////////
        //Load Views and etc.

        //Base Content
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeaderView = navigationView.getHeaderView(0);

        TextView userName = (TextView) navHeaderView.findViewById(R.id.nav_header_name);
        String fullName = myMLHUser.getUser().getData().getFirstName() + " "
                + myMLHUser.getUser().getData().getLastName();
        userName.setText(fullName);

        TextView schoolName = (TextView) navHeaderView.findViewById(R.id.nav_header_school);
        String school = myMLHUser.getUser().getData().getSchool().getName();
        schoolName.setText(school);

        hackathonSelector = (TextView) navHeaderView.findViewById(R.id.nav_header_hackathon);
        hackathonSelector.setText("HAkron");
        hackathonSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHackathonSelectorOpen) {
                    closeHackathonSelector();
                }
                else {
                    Menu menu = navigationView.getMenu();
                    menu.clear();
                    menu.add("HAkron");
                    menu.add("MHacks");
                    menu.add("Uncommon Hacks");
                    isHackathonSelectorOpen = true;
                }

            }
        });

        LinearLayout navHeaderUser = (LinearLayout) navHeaderView.findViewById(R.id.nav_header_user);
        navHeaderUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set View to UserView
                UserFragment userFragment = new UserFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,userFragment, "USER_FRAGMENT");
                fragmentTransaction.commit();

            //Close Drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            }
        });




        //Extra Notifications on Load
        if (getIntent().getBooleanExtra("myMLHUserDataLoadedFromLocal", false)) {
            Snackbar.make(drawer, "App Offline", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        myMLHUser.addObserver(this);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        if (isHackathonSelectorOpen) {
            int id = -1;
            CharSequence title = item.getTitle();
            Menu menu = navigationView.getMenu();
            for(int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getTitle() == title)
                    id = i;
            }
            if (id != -1)
            closeHackathonSelector(id);
            return false;
        }
        else {
            switch(item.getItemId()) {
                case R.id.nav_home:
                    HomeFragment fragment = new HomeFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame,fragment, "HOME_FRAGMENT");
                    fragmentTransaction.commit();
                    break;
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    public void removeToken(View view) {
        SharedPreferences settings = getSharedPreferences("HackCompanion", 0);
        settings.edit().remove("myMLHToken").apply();
        finishAffinity();
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d("MainActivity", "Observer > Notified");
        if (o.getClass().equals(MyMLHUser.class)) {
            updateUserData();

        }
        else {
            Log.d("MainActivity", "Observer > Something Went Wrong");
        }
    }

    public void updateUserData() {

    }

    private void closeHackathonSelector() {
        isHackathonSelectorOpen = false;
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_main_drawer);
    }

    private void closeHackathonSelector(int id) {
        hackathonSelector.setText(navigationView.getMenu().getItem(id).getTitle());
        closeHackathonSelector();
    }
}
