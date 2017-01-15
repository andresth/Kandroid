package in.andres.kandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardColumn;
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

    Context self;
    ViewPager mViewPager;
    PagerTitleStrip mTitleStrip;
    DashPagerAdapter mDashPager;
    ProjectPagerAdapter mProjectPager;
    private ArrayPagerAdapter mArrayPager;
    View mMainView;
    View mProgress;
    int progressBarCount = 0;

    private int mode = 0;

    KanboardAPI kanboardAPI;
    KanboardUserInfo Me;
    List<KanboardProjectInfo> mProjects;
    private KanboardProject mProject = null;
    private KanboardDashboard mDashboard = null;

    KanbordEvents eventHandler = new KanbordEvents() {
        @Override
        public void onGetMe(boolean success, KanboardUserInfo userInfo) {
            showProgress(false);
            Me = userInfo;
        }

        @Override
        public void onGetMyProjectsList(boolean success, List<KanboardProjectInfo> projects) {
            showProgress(false);
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
            showProgress(false);
            mDashboard = dash;
            populateProjectsMenu();
            showDashboard();
        }

        public void onGetProjectById(boolean success, KanboardProject project) {
            showProgress(false);
            mProject = project;
            Log.d("Event", String.format("Received Project %s", mProject.Name));
            showProject();
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

    //region overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMainView = findViewById(R.id.pager);
        mProgress = findViewById(R.id.main_progress);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTitleStrip = (PagerTitleStrip) mViewPager.findViewById(R.id.pager_title_strip);
        mArrayPager = new ArrayPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mArrayPager);

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
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if ((savedInstanceState != null) && savedInstanceState.containsKey("dashboard")) {
            // App was restarted by System, load saved instance
            mDashboard = (KanboardDashboard) savedInstanceState.getSerializable("dashboard");
        if ((savedInstanceState != null) && savedInstanceState.containsKey("project"))
            mProject = (KanboardProject) savedInstanceState.getSerializable("project");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mDashboard != null)
            savedInstanceState.putSerializable("dashboard", mDashboard);
        if (mProject != null)
            savedInstanceState.putSerializable("project", mProject);

        savedInstanceState.putInt("mode", mode);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // User rotated the screen or something
        Log.d("Lifecycle", "onRestoreInstance");
        if (savedInstanceState.containsKey("dashboard")) {
            mDashboard = (KanboardDashboard) savedInstanceState.getSerializable("dashboard");
            populateProjectsMenu();
        }
        if (savedInstanceState.containsKey("project"))
            mProject = (KanboardProject) savedInstanceState.getSerializable("project");
        mode = savedInstanceState.getInt("mode");

        if (mDashboard != null && (progressBarCount <= 0) && (mode == 0))
            showDashboard();
        if (mProject != null && progressBarCount <= 0 && mode == 1)
            showProject();
//        showDashboard();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("Lifecycle", "onResume");
        super.onResume();
        createKandoardAPI();
        if (mDashboard == null && (progressBarCount <= 0) && (mode == 0))
            refresh();
        if (mProject != null && progressBarCount <= 0 && mode == 1)
            showProject();
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
            mode = 0;
            showDashboard();
//        } else if (id == R.id.nav_overdue) {
//
        } else if (id == R.id.nav_sign_in) {
            Intent iSetting = new Intent(this, SettingsActivity.class);
            startActivity(iSetting);
        } else if (id == R.id.nav_refresh) {
            refresh();
        } else if (id == R.id.nav_about) {
            Intent iAboutScreen = new Intent(this, AboutActivity.class);
            startActivity(iAboutScreen);
        } else {
            showProgress(true);
            Log.d("Drawer Menu", String.format("Project %d selected", id));
            mode = 1;
            kanboardAPI.KB_getProjectById(id);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region private methods
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
        if (mDashboard == null)
            return;

        try {
            getSupportActionBar().setTitle(getString(R.string.action_dashboard));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (mProjectPager != null) {
            mProjectPager.clearAll();
            mProjectPager = null;
        }

//        mViewPager.removeAllViews();
//        mViewPager.addView(mTitleStrip);
//        for (int i = mViewPager.getChildCount(); i > 0; i--) {
//            if (mViewPager.getChildAt(i - 1).getId() != R.id.pager_title_strip)
//                mViewPager.removeView(mViewPager.getChildAt(i - 1));
//        }
//        mViewPager.setAdapter(null);
//
//        mDashPager = new DashPagerAdapter(getSupportFragmentManager(), mDashboard, this);
//        mViewPager.setAdapter(mDashPager);
//        mDashPager.notifyDataSetChanged();
        mArrayPager.removeAllFragments();
        mArrayPager.addFragment(new DashProjectsFragment(), getString(R.string.tab_projects));
        mArrayPager.addFragment(new DashOverdueFragment(),getString(R.string.tab_overdue_tasks));
        mArrayPager.addFragment(new DashActivitiesFragment(), getString(R.string.tab_activity));
        mArrayPager.notifyDataSetChanged();
//        mViewPager.setCurrentItem(0);
    }

    private void showProject() {
        if (mProject == null)
            return;

        try {
            getSupportActionBar().setTitle(mProject.Name);
        } catch (NullPointerException e) {
            e.printStackTrace();;
        }

        if (mDashPager != null) {
            mDashPager.clearAll();
            mDashPager = null;
        }

//        mViewPager.removeAllViews();
//        mViewPager.addView(mTitleStrip);
//        for (int i = mViewPager.getChildCount(); i > 0; i--) {
//            if (mViewPager.getChildAt(i - 1).getId() != R.id.pager_title_strip)
//                mViewPager.removeView(mViewPager.getChildAt(i - 1));
//        }
//        mViewPager.setAdapter(null);
//
//        mProjectPager = new ProjectPagerAdapter(getSupportFragmentManager(), mProject);
//        mViewPager.setAdapter(mProjectPager);
//        mProjectPager.notifyDataSetChanged();
//        Log.d("showProject", Integer.toString(mProjectPager.getCount()));
        mArrayPager.removeAllFragments();
        mArrayPager.addFragment(ProjectOverviewFragment.newInstance(), getString(R.string.tab_overview));
        for (KanboardColumn column: mProject.Columns) {
            mArrayPager.addFragment(ProjectTasksFragment.newInstance(column), column.Title);
        }
        mArrayPager.addFragment(TextFragment.newInstance(String.format(Locale.getDefault(), "%d Overdue Tasks", mProject.OverdueTasks.size())), getString(R.string.tab_overdue_tasks));
        mArrayPager.addFragment(TextFragment.newInstance(String.format(Locale.getDefault(), "%d Inactive Tasks", mProject.InactiveTasks.size())), getString(R.string.tab_inactive_tasks));
        mArrayPager.notifyDataSetChanged();
//        mViewPager.setCurrentItem(0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (show)
            progressBarCount++;
        else
            progressBarCount -= progressBarCount > 0 ? 1 : 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mMainView.setVisibility(progressBarCount > 0 ? View.GONE : View.VISIBLE);
            mMainView.animate().setDuration(shortAnimTime).alpha(progressBarCount > 0 ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mMainView.setVisibility(progressBarCount > 0 ? View.GONE : View.VISIBLE);
                        }
                    });

            mProgress.setVisibility(progressBarCount > 0 ? View.VISIBLE: View.GONE);
            mProgress.animate().setDuration(shortAnimTime).alpha(progressBarCount > 0 ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgress.setVisibility(progressBarCount > 0 ? View.VISIBLE : View.GONE);
                        }
                    });

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgress.setVisibility(progressBarCount > 0 ? View.VISIBLE : View.GONE);
            mMainView.setVisibility(progressBarCount > 0 ? View.GONE : View.VISIBLE);
        }
    }

    private boolean createKandoardAPI() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        boolean showLoginScreen = false;
        if (!preferences.contains("serverurl"))
            showLoginScreen = true;
        serverURL = preferences.getString("serverurl", "");

//        if (!preferences.contains("apikey"))
//            showLoginScreen = true;
//        apiKey = preferences.getString("apikey", "");

        if (!preferences.contains("username"))
            showLoginScreen = true;
        username = preferences.getString("username", "");

        if (!preferences.contains("password"))
            showLoginScreen = true;
        password = preferences.getString("password", "");

        if (showLoginScreen) {
            Intent iLoginScreen = new Intent(this, LoginActivity.class);
            startActivity(iLoginScreen);
            return false;
        } else {
            try {
                kanboardAPI = new KanboardAPI(serverURL, username, password);
                kanboardAPI.addListener(eventHandler);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void refresh() {
        if (!createKandoardAPI())
            return;

        showProgress(true);
        kanboardAPI.getMe();

//        kanboardAPI.getMyProjectsList();

        showProgress(true);
        kanboardAPI.KB_getDashboard();
//        kanboardAPI.getMyDashboard();
    }
    //endregion

    //region public methods
    public KanboardDashboard getDashboard() {
        return mDashboard;
    }

    public KanboardProject getProject() {
        return mProject;
    }
    //endregion

}
