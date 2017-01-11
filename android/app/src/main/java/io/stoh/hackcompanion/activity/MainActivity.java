package io.stoh.hackcompanion.activity;

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

import io.stoh.hackcompanion.R;
import io.stoh.hackcompanion.data.Constants;
import io.stoh.hackcompanion.data.Hackathon;
import io.stoh.hackcompanion.data.MyMLHUser;
import io.stoh.hackcompanion.activity.navigation.HomeFragment;
import io.stoh.hackcompanion.activity.navigation.UserFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Observer {
    private String myMLHToken;
    private MyMLHUser myMLHUser = MyMLHUser.getInstance();
    private boolean isHackathonSelectorOpen = false;

    private NavigationView navigationView;
    private View navHeaderView;
    private TextView hackathonSelector;
    private Constants.Modes appMode = Constants.Modes.EMPTY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");

        //Activity will not load unless there is a token
        myMLHToken = getIntent().getStringExtra(Constants.Keys.MYMLH_TOKEN);

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


        //Navigtation View
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


        LinearLayout navHeaderUser = (LinearLayout) navHeaderView.findViewById(R.id.nav_header_user);
        navHeaderUser.setOnClickListener(navHeaderUserListener);

        hackathonSelector = (TextView) navHeaderView.findViewById(R.id.nav_header_hackathon);
        hackathonSelector.setOnClickListener(hackathonSelectorListener);

        //Extra Notifications on Load
        if (getIntent().getBooleanExtra("myMLHUserDataLoadedFromLocal", false)) {
            Snackbar.make(drawer, "App Offline", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        //TODO maybe move this to beginning of onCreate?  But potential issues of Null Pointers
        myMLHUser.addObserver(this);
        myMLHUser.updateUser();
        myMLHUser.updateHackathons();


    }

    private View.OnClickListener navHeaderUserListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Set View to UserView
            UserFragment userFragment = new UserFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, userFragment, "USER_FRAGMENT");
            fragmentTransaction.commit();

            //Close Drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    };

    private View.OnClickListener hackathonSelectorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("MainActivity", "hackathonSelectorListener Click");
            if (isHackathonSelectorOpen) {
                Log.d("MainActivity", "HackathonSelector is Open");
                closeHackathonSelector();
            }
            else {
                Log.d("MainActivity", "HackathonSelector is Closed");
                setNavigationMenu(false);
            }
        }
    };
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
        Log.d("MainActivity", "NavItemSelected");
        // Handle navigation view item clicks here.

        if (isHackathonSelectorOpen) {
            Log.d("MainActivity", "Hackathon Selector is Open");
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
            switch(appMode) {
                case USER:
                    switch(item.getItemId()) {
                        case R.id.nav_home:
                            HomeFragment fragment = new HomeFragment();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame,fragment, "HOME_FRAGMENT");
                            fragmentTransaction.commit();
                            break;
                    }
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
        Log.d("MainActivity", "Update");
        Bundle update = (Bundle) arg;

        if (update.containsKey(Constants.Updates.HACKATHON)) {
            Log.d("MainActivity", "Hackathon Update");
            if (update.getBoolean(Constants.Updates.HACKATHON)) {
                Log.d("MainActivity", "Update True");
                setMode(myMLHUser.getMode());
            }
        }
    }


    private void closeHackathonSelector() {
        Log.d("MainActivity", "closeHackathonSelector");
        isHackathonSelectorOpen = false;
        setNavigationMenu(true);
    }

    private void closeHackathonSelector(int id) {
        Log.d("MainActivity", "Changing Selected Hackathon");
        hackathonSelector.setText(navigationView.getMenu().getItem(id).getTitle());
        closeHackathonSelector();
    }


    private void setMode(Constants.Modes mode) {
        Log.d("MainActivity", "Setting New Mode");
        Log.d("MainActivity", "Current Mode: " + appMode.toString());
        Log.d("MainActivity", "New Mode: " + mode.toString());
        if (mode != appMode) {
            Log.d("MainActivity", "New Mode is Different than Current Mode");
            appMode = mode;
            switch (appMode) {
                case USER:
                    Log.d("MainActivity", "Setting up USER Mode");
                    setupHackathonSelector(true);
                    break;
            }
        }

        setNavigationMenu(true);
    }

    private void setNavigationMenu(Boolean isNav) {
        Log.d("MainActivity", "Setting Navigation Menu");
        navigationView.getMenu().clear();
        //Navigation Menus
        if (isNav) {
            Log.d("MainActivity", "Setting Navigation Menu to Normal Navigation");
            Log.d("MainActivity", "App Mode: " + appMode.toString());
            switch(appMode) {
                case USER:
                    Log.d("MainActivity", "Setting Navigation Menu to User");
                    navigationView.inflateMenu(R.menu.activity_main_drawer);
                    break;
            }

        }
        //Hackathon Select Menu
        else {
            isHackathonSelectorOpen = true;
            Log.d("MainActivity", "Setting Navigation Menu to Hackathon Menu");
            for(Hackathon hackathon: myMLHUser.getHackathons()) {
                Log.d("MainActivity", "Adding Hackathon " + hackathon.getName());
                navigationView.getMenu().add(hackathon.getName());
            }

        }
    }

    private void setupHackathonSelector(Boolean notEmpty) {
        Log.d("MainActivity", "setupHackathonSelector");
        Log.d("MainActivity", "not Empty?" + notEmpty.toString());

        if(notEmpty) {
            Log.d("MainActivity", "Setting Selector Text to " + myMLHUser.getCurrentHackathon().getName());
            hackathonSelector.setText(myMLHUser.getCurrentHackathon().getName());
        }
        else {
            Log.d("MainActivity", "Setting Selector Text to No Hackathons");
            hackathonSelector.setText("No Hackathons");
        }
    }
}
