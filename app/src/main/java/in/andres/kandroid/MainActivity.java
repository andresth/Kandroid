package in.andres.kandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardDashboard;
import in.andres.kandroid.kanboard.KanboardError;
import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardProjectInfo;
import in.andres.kandroid.kanboard.KanboardUserInfo;
import in.andres.kandroid.kanboard.KanbordEvents;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String serverURL;
    String apiKey;
    String username;
    String password;
//    TextView mInfotext;
    KanboardAPI kanboardAPI;
    KanboardUserInfo Me;
    List<KanboardProjectInfo> mProjects;
    KanboardDashboard mDashboard;
    Context self;
    ViewPager mViewPager;
    DashPagerAdapter mDashPager;

    KanbordEvents eventHandler = new KanbordEvents() {
        @Override
        public void onGetMe(boolean success, KanboardUserInfo userInfo) {
            Me = userInfo;
        }

        @Override
        public void onGetMyProjectsList(boolean success, List<KanboardProjectInfo> projects) {
            mProjects = projects;
            NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
            SubMenu proj = nav.getMenu().findItem(R.id.projects).getSubMenu();
            proj.clear();
            for (KanboardProjectInfo item: mProjects) {
                MenuItem m =proj.add(Menu.NONE, item.ID, Menu.NONE, item.Name);
                m.setIcon(R.drawable.project);
            }
        }

        @Override
        public void onGetMyDashboard(boolean success, KanboardDashboard dash) {
            mDashboard = dash;
            populateProjectsMenu();
            showDashboard();
        }

        @Override
        public void onError(KanboardError error) {
            new AlertDialog.Builder(self)
                    .setTitle("Error")
                    .setMessage("Code: " + Integer.toString(error.Code) + "\n" +
                                "Message: " + error.Message + "\n" +
                                "HTTP Response: " + Integer.toString(error.HTTPReturnCode))
                    .setNeutralButton("Dismiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        @Override
        public void onDebug(boolean success, String message) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        self = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                TextView mServerUrl = (TextView) findViewById(R.id.nav_serverurl);
                if ((Me != null) && (mServerUrl != null))
                    mServerUrl.setText(Me.Name);
            }
        };
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        boolean showLoginScreen = false;
        if (!preferences.contains("serverurl"))
            showLoginScreen = true;
        serverURL = preferences.getString("serverurl", "");

        if (!preferences.contains("apikey"))
            showLoginScreen = true;
        apiKey = preferences.getString("apikey", "");

        if (!preferences.contains("serverurl"))
            showLoginScreen = true;
        username = preferences.getString("username", "");

        if (!preferences.contains("password"))
            showLoginScreen = true;
        password = preferences.getString("password", "");

        if (showLoginScreen) {
            Intent iLoginScreen = new Intent(this, LoginActivity.class);
            startActivity(iLoginScreen);
        }

//        mInfotext = (TextView) findViewById(R.id.infotext);

        try {
            kanboardAPI = new KanboardAPI(serverURL, username, password);
            kanboardAPI.addListener(eventHandler);
            kanboardAPI.getMe();
//            kanboardAPI.getMyProjectsList();
            kanboardAPI.getMyDashboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateProjectsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateProjectsMenu();
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            showDashboard();
        } else if (id == R.id.nav_overdue) {

        } else if (id == R.id.nav_sign_in) {
            Intent iSetting = new Intent(this, SettingsActivity.class);
            startActivity(iSetting);
        } else if (id == R.id.nav_refresh) {
        } else if (id == R.id.nav_about) {
            Intent iAboutScreen = new Intent(this, AboutActivity.class);
            startActivity(iAboutScreen);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void populateProjectsMenu() {
        if (mDashboard == null) {
            return;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        List<KanboardProject> projList = new ArrayList<>(mDashboard.Projects);
        if (preferences.getBoolean("projects_sort_alphabetic", false))
            Collections.sort(projList);
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        SubMenu projMenu = nav.getMenu().findItem(R.id.projects).getSubMenu();
        projMenu.clear();
        for (KanboardProject item: projList)
            projMenu.add(Menu.NONE, item.ID, Menu.NONE, item.Name)
                    .setIcon(R.drawable.project);
    }

    private void showDashboard() {
        getSupportActionBar().setTitle("Dashboard");
        mDashPager = new DashPagerAdapter(getSupportFragmentManager(), mDashboard, this);
        mViewPager.setAdapter(mDashPager);
    }
}
