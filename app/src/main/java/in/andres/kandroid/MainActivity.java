package in.andres.kandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardProjectInfo;
import in.andres.kandroid.kanboard.KanboardTask;
import in.andres.kandroid.kanboard.KanboardUserInfo;
import in.andres.kandroid.kanboard.KanbordEvents;

class ThreadPerTaskExecutor implements Executor {
    public void execute(Runnable r) {
        new Thread(r).start();
    }
}

class SerialExecutor implements Executor {
    final Queue<Runnable> tasks = new ArrayDeque<>();
    final Executor executor;
    Runnable active;

    SerialExecutor(Executor executor) {
        this.executor = executor;
    }

    public synchronized void execute(final Runnable r) {
        tasks.add(new Runnable() {
            public void run() {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            }
        });
        if (active == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        if ((active = tasks.poll()) != null) {
            executor.execute(active);
        }
    }
}

class DirectExecutor implements Executor {
    public void execute(Runnable r) {
        r.run();
    }
}

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String serverURL;
    String apiKey;
    String username;
    String password;
    TextView mInfotext;
    KanboardAPI kanboardAPI;
    KanboardUserInfo Me;
    List<KanboardProjectInfo> Projects;
    Executor mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
//                if (Projects != null) {
//                    NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
//                    SubMenu proj = nav.getMenu().findItem(R.id.projects).getSubMenu();
//                    proj.clear();
//                    for (KanboardProjectInfo item: Projects)
//                        proj.add(Menu.NONE, item.ID, Menu.NONE, item.Name);
//                    mInfotext.setText(Integer.toString(nav.getMenu().findItem(R.id.projects).getSubMenu().size()));
//                }
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

        mInfotext = (TextView) findViewById(R.id.infotext);

        try {
            kanboardAPI = new KanboardAPI(serverURL, username, password);
            kanboardAPI.addListener(new KanbordEvents() {
                @Override
                public void onGetMe(boolean success, KanboardUserInfo userInfo) {
                    Me = userInfo;
                }

                @Override
                public void onGetMyProjectsList(boolean success, List<KanboardProjectInfo> projects) {
                    Projects = projects;
                    NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
                    SubMenu proj = nav.getMenu().findItem(R.id.projects).getSubMenu();
                    proj.clear();
                    for (KanboardProjectInfo item: Projects) {
                        MenuItem m =proj.add(Menu.NONE, item.ID, Menu.NONE, item.Name);
                        m.setIcon(R.drawable.project);
                    }
                    mInfotext.setText(Integer.toString(nav.getMenu().findItem(R.id.projects).getSubMenu().size()));
                }

                @Override
                public void onDebug(boolean success, String message) {
                    mInfotext.setText(message);
                }
            });
            kanboardAPI.getMe();
            kanboardAPI.getMyProjectsList();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else
        if (id == R.id.nav_dashboard) {

        } else if (id == R.id.nav_overdue) {

        } else if (id == R.id.nav_sign_in) {
            Intent iLoginScreen = new Intent(this, LoginActivity.class);
            startActivity(iLoginScreen);
        } else if (id == R.id.nav_refresh) {

        } else if (id == R.id.nav_about) {
            Intent iAboutScreen = new Intent(this, AboutActivity.class);
            startActivity(iAboutScreen);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
