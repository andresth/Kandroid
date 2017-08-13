/*
 * Copyright 2017 Thomas Andres
 *
 * This file is part of Kandroid.
 *
 * Kandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kandroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.andres.kandroid.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import in.andres.kandroid.ArrayPagerAdapter;
import in.andres.kandroid.BuildConfig;
import in.andres.kandroid.Constants;
import in.andres.kandroid.R;
import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardActivity;
import in.andres.kandroid.kanboard.KanboardCategory;
import in.andres.kandroid.kanboard.KanboardColor;
import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardDashboard;
import in.andres.kandroid.kanboard.KanboardError;
import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardSwimlane;
import in.andres.kandroid.kanboard.KanboardTask;
import in.andres.kandroid.kanboard.KanboardUserInfo;
import in.andres.kandroid.kanboard.events.OnErrorListener;
import in.andres.kandroid.kanboard.events.OnGetActiveSwimlanesListener;
import in.andres.kandroid.kanboard.events.OnGetAllCategoriesListener;
import in.andres.kandroid.kanboard.events.OnGetAllTasksListener;
import in.andres.kandroid.kanboard.events.OnGetColumnsListener;
import in.andres.kandroid.kanboard.events.OnGetDefaultColorsListener;
import in.andres.kandroid.kanboard.events.OnGetMeListener;
import in.andres.kandroid.kanboard.events.OnGetMyActivityStreamListener;
import in.andres.kandroid.kanboard.events.OnGetMyDashboardListener;
import in.andres.kandroid.kanboard.events.OnGetMyOverdueTasksListener;
import in.andres.kandroid.kanboard.events.OnGetMyProjectsListener;
import in.andres.kandroid.kanboard.events.OnGetOverdueTasksByProjectListener;
import in.andres.kandroid.kanboard.events.OnGetProjectByIdListener;
import in.andres.kandroid.kanboard.events.OnGetProjectUsersListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String serverURL;
    private String username;
    private String password;

    private Context self;
    private ViewPager mViewPager;
    private PagerTitleStrip mTitleStrip;
    private ArrayPagerAdapter mArrayPager;
    private View mMainView;
    private View mProgress;
    private MenuItem refreshAction;
    private boolean progressVisible = false;
    private int progressBarCount = 0;

    private int mode = 0;

    private KanboardAPI kanboardAPI;
    private KanboardUserInfo Me;
//    private List<KanboardProjectInfo> mProjects;
    private KanboardProject mProject = null;
    private KanboardDashboard mDashboard = null;
    private Dictionary<String, KanboardColor> mColors = null;
    private List<KanboardProject> mProjectList = null;

    private List<KanboardActivity> mMyActivities = null;
    private List<KanboardTask> mMyOverduetasks = null;
    private List<KanboardColumn> mColumns = null;
    private List<KanboardSwimlane> mSwimlanes = null;
    private List<KanboardCategory> mCategories = null;
    private List<KanboardTask> mActiveTasks = null;
    private List<KanboardTask> mInactiveTasks = null;
    private List<KanboardTask> mOverdueTasks = null;
    private Dictionary<Integer, String> mProjectUsers = null;

    private OnErrorListener errorListener = new OnErrorListener() {
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
    };
    private OnGetMeListener getMeListener = new OnGetMeListener() {
        @Override
        public void onGetMe(boolean success, KanboardUserInfo result) {
            boolean prog = !showProgress(false);
            if (success) {
                Me = result;
                ((TextView) findViewById(R.id.nav_serverurl)).setText(Me.getName());
                if (prog) {
                    if (mode == 0)
                        combineDashboard();
                    else
                        combineProject();
                }
            }
        }
    };
    private OnGetMyDashboardListener getMyDashboardListener = new OnGetMyDashboardListener() {
        @Override
        public void onGetMyDashboard(boolean success, KanboardDashboard result) {
            if (success) {
                mDashboard = result;
                if (!showProgress(false)) {
                    combineDashboard();
                }
            }
        }
    };
    private OnGetMyActivityStreamListener getMyActivityStreamListener = new OnGetMyActivityStreamListener() {
        @Override
        public void onGetMyActivityStream(boolean success, List<KanboardActivity> result) {
            if (success) {
                mMyActivities = result;
                if (!showProgress(false)) {
                    combineDashboard();
                }
            }
        }
    };
    private OnGetMyOverdueTasksListener getMyOverdueTasksListener = new OnGetMyOverdueTasksListener() {
        @Override
        public void onGetMyOverdueTasks(boolean success, List<KanboardTask> result) {
            if (success) {
                mMyOverduetasks = result;
                if (!showProgress(false)) {
                    combineDashboard();
                }
            }
        }
    };
    private OnGetProjectByIdListener getProjectByIdListener = new OnGetProjectByIdListener() {
        @Override
        public void onGetProjectById(boolean success, KanboardProject result) {
            if (success) {
                mProject = result;
                if (!showProgress(false)) {
                    combineProject();
                }
            }
        }
    };
    private OnGetColumnsListener getColumnsListener = new OnGetColumnsListener() {
        @Override
        public void onGetColumns(boolean success, List<KanboardColumn> result) {
            if (success) {
                mColumns = result;
                if (!showProgress(false)) {
                    combineProject();
                }
            }
        }
    };
    private OnGetActiveSwimlanesListener getActiveSwimlanesListener = new OnGetActiveSwimlanesListener() {
        @Override
        public void onGetActiveSwimlanes(boolean success, List<KanboardSwimlane> result) {
            if (success) {
                mSwimlanes = result;
                if (!showProgress(false)) {
                    combineProject();
                }
            }
        }
    };
    private OnGetAllCategoriesListener getAllCategoriesListener = new OnGetAllCategoriesListener() {
        @Override
        public void onGetAllCategories(boolean success, List<KanboardCategory> result) {
            if (success) {
                mCategories = result;
                if (!showProgress(false)) {
                    combineProject();
                }
            }
        }
    };
    private OnGetAllTasksListener getAllTasksListener = new OnGetAllTasksListener() {
        @Override
        public void onGetAllTasks(boolean success, int status, List<KanboardTask> result) {
            if (success) {
                if (status == 0) {
                    mInactiveTasks = result;
                    if (!showProgress(false)) {
                        combineProject();
                    }
                } else if (status == 1) {
                    mActiveTasks = result;
                    if (!showProgress(false)) {
                        combineProject();
                    }
                }
            }
        }
    };
    private OnGetOverdueTasksByProjectListener getOverdueTasksByProjectListener = new OnGetOverdueTasksByProjectListener() {
        @Override
        public void onGetOverdueTasksByProject(boolean success, List<KanboardTask> result) {
            if (success) {
                mOverdueTasks = result;
                if (!showProgress(false)) {
                    combineProject();
                }
            }
        }
    };
    private OnGetProjectUsersListener getProjectUsersListener = new OnGetProjectUsersListener() {
        @Override
        public void onGetProjectUsers(boolean success, Dictionary<Integer, String> result) {
            if (success) {
                mProjectUsers = result;
                if (!showProgress(false)) {
                    combineProject();
                }
            }
        }
    };
    private OnGetDefaultColorsListener getDefaultColorsListener = new OnGetDefaultColorsListener() {
        @Override
        public void onGetDefaultColors(boolean success, Dictionary<String, KanboardColor> colors) {
            if (success) {
                mColors = colors;
                if (!showProgress(false)) {
                    combineDashboard();
                }
            }
        }
    };
    private OnGetMyProjectsListener getMyProjectsListener = new OnGetMyProjectsListener() {
        @Override
        public void onGetMyProjects(boolean success, List<KanboardProject> result) {
            if (success) {
                mProjectList = result;
                if (!showProgress(false)) {
                    combineDashboard();
                }
            }
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

        mViewPager = (ViewPager) mMainView;
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

        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
//                TextView mServerUrl = (TextView) findViewById(R.id.nav_serverurl);
//                if ((Me != null) && (mServerUrl != null))
//                    mServerUrl.setText(Me.getName());
            }
        };
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mDashboard != null)
            savedInstanceState.putSerializable("dashboard", mDashboard);
        if (mProject != null)
            savedInstanceState.putSerializable("project", mProject);
        if (mProjectList != null)
            savedInstanceState.putSerializable("projectList", (ArrayList<KanboardProject>) mProjectList);
        if (Me != null)
            savedInstanceState.putSerializable("me", Me);
        if (mColors != null)
            savedInstanceState.putSerializable("colors", (Hashtable<String, KanboardColor>) mColors);

        savedInstanceState.putInt("ViewPagerItem", mViewPager.getCurrentItem());

        savedInstanceState.putInt("mode", mode);

        if (BuildConfig.DEBUG) Log.v(Constants.TAG, "MainActivity: saved savedInstanceState");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreSavedState(savedInstanceState);

        if (mDashboard != null && (progressBarCount <= 0) && (mode == 0))
            showDashboard();
        if (mProject != null && progressBarCount <= 0 && mode > 0)
            showProject();

        mViewPager.setCurrentItem(savedInstanceState.getInt("ViewPagerItem"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ((mDashboard == null && mode == 0) || (mProject == null && mode > 0))
            refresh();
        else
            createKandoardAPI();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mDashboard != null && (progressBarCount <= 0) && (mode == 0))
            showDashboard();
        if (mProject != null && progressBarCount <= 0 && mode > 0)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        refreshAction = menu.findItem(R.id.action_refresh);
        if (progressBarCount > 0) {
            ProgressBar prog = new ProgressBar(self);
            prog.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
            refreshAction.setActionView(prog);
            refreshAction.expandActionView();
            progressVisible = true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Log.i(Constants.TAG, "Select dashboard");
            if (mode != 0)
                mViewPager.setCurrentItem(0);
            mode = 0;
            showDashboard();
        } else if (id == R.id.nav_sign_in) {
            Intent iSetting = new Intent(this, SettingsActivity.class);
            startActivity(iSetting);
        } else if (id == R.id.nav_refresh) {
            refresh();
        } else if (id == R.id.nav_about) {
            Intent iAboutScreen = new Intent(this, AboutActivity.class);
            startActivity(iAboutScreen);
        } else {
            Log.i(Constants.TAG, "Select project");
            if (mode != id)
                mViewPager.setCurrentItem(0);
            mode = id;
            refresh();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region private methods

    private void restoreSavedState(Bundle savedInstanceState) {
        // User rotated the screen or something
        if (BuildConfig.DEBUG) Log.v(Constants.TAG, "MainActivity: restore savedInstanceState");

        if (savedInstanceState.containsKey("me")) {
            Me = (KanboardUserInfo) savedInstanceState.getSerializable("me");
        }
        if (savedInstanceState.containsKey("dashboard")) {
            mDashboard = (KanboardDashboard) savedInstanceState.getSerializable("dashboard");
        }
        if (savedInstanceState.containsKey("projectList")) {
            mProjectList = (ArrayList<KanboardProject>) savedInstanceState.getSerializable("projectList");
            populateProjectsMenu();
        }
        if (savedInstanceState.containsKey("project"))
            mProject = (KanboardProject) savedInstanceState.getSerializable("project");
        mode = savedInstanceState.getInt("mode");
        if (savedInstanceState.containsKey("colors")) {
            Object o = savedInstanceState.getSerializable("colors");
            if (o instanceof HashMap)
                mColors = new Hashtable<>((HashMap<String, KanboardColor>) o);
            else
                mColors = (Hashtable<String, KanboardColor>) o;
        }
    }

    private void combineDashboard() {
        if (mDashboard != null && mMyOverduetasks != null && mMyActivities != null && mProjectList != null) {
            mDashboard.setExtra(mMyOverduetasks, mMyActivities, mProjectList);
            populateProjectsMenu();
            showDashboard();
            NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
            nav.getMenu().findItem(R.id.nav_dashboard).setEnabled(true);
        } else {
            Log.w(Constants.TAG, "Something happened while assembling mDashboard.");
        }
    }

    private void combineProject() {
        if (mProject != null && mColumns != null && mSwimlanes != null && mCategories != null &&
                mActiveTasks != null && mInactiveTasks != null && mOverdueTasks != null && mProjectUsers != null) {
            mProject.setExtra(mColumns, mSwimlanes, mCategories, mActiveTasks, mInactiveTasks, mOverdueTasks, mProjectUsers);
            showProject();
        } else {
            Log.w(Constants.TAG, "Something happened while assembling mProject.");
        }
    }

    private void populateProjectsMenu() {
        if (mProjectList == null) {
            if (BuildConfig.DEBUG) Log.d("Kandroid", "Tried to populate drawer, but mDashboard was null");
            return;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        List<KanboardProject> projList = mProjectList;
        if (preferences.getBoolean("projects_sort_alphabetic", false))
            Collections.sort(projList);
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        SubMenu projMenu = nav.getMenu().findItem(R.id.projects).getSubMenu();
        projMenu.clear();
        for (KanboardProject item: projList)
            projMenu.add(Menu.NONE, item.getId(), Menu.NONE, item.getName())
                    .setIcon(R.drawable.project);
    }

    private void showDashboard() {
        if (mDashboard == null) {
            if (BuildConfig.DEBUG) Log.d("Kandroid", "Tried to show dashboard, but mDashboard was null");
            return;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.action_dashboard));

        mArrayPager.removeAllFragments();
        mArrayPager.addFragment(DashProjectsFragment.newInstance(), getString(R.string.tab_projects));
        mArrayPager.addFragment(DashOverdueFragment.newInstance(),getString(R.string.tab_overdue_tasks));
        mArrayPager.addFragment(DashActivitiesFragment.newInstance(), getString(R.string.tab_activity));
        mArrayPager.notifyDataSetChanged();
    }

    private void showProject() {
        if (mProject == null) {
            if (BuildConfig.DEBUG) Log.d(Constants.TAG, "Tried to show project, but mProject was null");
            return;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(mProject.getName());

        mArrayPager.removeAllFragments();
        mArrayPager.addFragment(ProjectOverviewFragment.newInstance(), getString(R.string.tab_overview));
        if (mProject.getSwimlanes().size() > 0) {
            for (KanboardColumn column : mProject.getColumns()) {
                mArrayPager.addFragment(ProjectTasksFragment.newInstance(column), column.getTitle());
            }
            mArrayPager.addFragment(ProjectOverdueTasksFragment.newInstance(), getString(R.string.tab_overdue_tasks));
            mArrayPager.addFragment(ProjectInactiveTasksFragment.newInstance(), getString(R.string.tab_inactive_tasks));

            if (mProject.hasHiddenSwimlanes()) {
                Toast.makeText(this, getString(R.string.hint_swimlane_deactivated), Toast.LENGTH_LONG).show();
            }
        } else {
            // ask user to activate at least one swimlane
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(android.R.string.dialog_alert_title);
            builder.setMessage(R.string.error_swimlanes_deactivated);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
        mArrayPager.notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private boolean showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (show)
            progressBarCount++;
        else
            progressBarCount = progressBarCount > 0 ? --progressBarCount : 0;

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

        if (progressBarCount > 0 && refreshAction != null && !progressVisible) {
            ProgressBar prog = new ProgressBar(self);
            prog.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
            refreshAction.setActionView(prog);
            refreshAction.expandActionView();
            progressVisible = true;
        }
        if (progressBarCount == 0 && refreshAction != null && progressVisible) {
            refreshAction.collapseActionView();
            refreshAction.setActionView(null);
            progressVisible = false;
        }

        return progressBarCount != 0;
    }

    private boolean createKandoardAPI() {
        // Check if API object already exists
        if (kanboardAPI != null)
            return true;

        Log.d(Constants.TAG, "Creating API object");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        boolean showLoginScreen = false;
        if (!preferences.contains("serverurl"))
            showLoginScreen = true;
        serverURL = preferences.getString("serverurl", "");

        if (!preferences.contains("username"))
            showLoginScreen = true;
        username = preferences.getString("username", "");

        if (!preferences.contains("password"))
            showLoginScreen = true;
        password = preferences.getString("password", "");

        if (showLoginScreen) {
            Log.i("Kandroid", "No credential found. Launching login activity.");
            Intent iLoginScreen = new Intent(this, LoginActivity.class);
            startActivity(iLoginScreen);
            return false;
        } else {
            try {
                kanboardAPI = new KanboardAPI(serverURL, username, password);
                kanboardAPI.addErrorListener(errorListener);
                kanboardAPI.addOnGetMeListener(getMeListener);
                kanboardAPI.addOnGetMyDashboardListener(getMyDashboardListener);
                kanboardAPI.addOnGetMyActivityStreamListener(getMyActivityStreamListener);
                kanboardAPI.addOnGetMyOverdueTasksListener(getMyOverdueTasksListener);
                kanboardAPI.addOnGetProjectByIdListener(getProjectByIdListener);
                kanboardAPI.addOnGetColumnsListener(getColumnsListener);
                kanboardAPI.addOnGetActiveSwimlanesListener(getActiveSwimlanesListener);
                kanboardAPI.addOnGetAllCategoriesListener(getAllCategoriesListener);
                kanboardAPI.addOnGetAllTasksListener(getAllTasksListener);
                kanboardAPI.addOnGetOverdueTasksByProjectListener(getOverdueTasksByProjectListener);
                kanboardAPI.addOnGetProjectUsersListener(getProjectUsersListener);
                kanboardAPI.addOnGetDefaultColorsListener(getDefaultColorsListener);
                kanboardAPI.addOnGetMyProjectsListener(getMyProjectsListener);
                return true;
            } catch (IOException e) {
                Log.e(Constants.TAG, "Failed to create API object.");
                e.printStackTrace();
            }
        }
        return false;
    }

    protected void refresh() {
        if (!createKandoardAPI())
            return;


        showProgress(true);
        kanboardAPI.getMe();

        if (mode == 0) {
            NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
            nav.getMenu().findItem(R.id.nav_dashboard).setEnabled(false);
            Log.i(Constants.TAG, "Loading dashboard data.");
            showProgress(true);
            kanboardAPI.getDefaultTaskColors();
            showProgress(true);
            kanboardAPI.getMyProjects();
            showProgress(true);
            kanboardAPI.getMyDashboard();
            showProgress(true);
            kanboardAPI.getMyActivityStream();
            showProgress(true);
            kanboardAPI.getMyOverdueTasks();
        } else {
            Log.i(Constants.TAG, "Loading project date.");
            showProgress(true);
            kanboardAPI.getProjectById(mode);
            showProgress(true);
            kanboardAPI.getColumns(mode);
            showProgress(true);
            kanboardAPI.getActiveSwimlanes(mode);
            showProgress(true);
            kanboardAPI.getAllCategories(mode);
            showProgress(true);
            kanboardAPI.getAllTasks(mode, 1);
            showProgress(true);
            kanboardAPI.getAllTasks(mode, 0);
            showProgress(true);
            kanboardAPI.getOverdueTasksByProject(mode);
            showProgress(true);
            kanboardAPI.getProjectUsers(mode);
        }
    }
    //endregion

    //region public methods
    public KanboardDashboard getDashboard() {
        return mDashboard;
    }

    public KanboardProject getProject() {
        return mProject;
    }

    public KanboardUserInfo getMe() {
        return Me;
    }

    public Dictionary<String, KanboardColor> getColors() {
        return mColors;
    }

    public List<KanboardProject> getProjectList() {
        return mProjectList;
    }

    //endregion

}
