package in.andres.kandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardCategory;
import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardComment;
import in.andres.kandroid.kanboard.KanboardSwimlane;
import in.andres.kandroid.kanboard.KanboardTask;
import in.andres.kandroid.kanboard.OnGetAllCommentsListener;
import in.andres.kandroid.kanboard.OnGetCategoryListener;
import in.andres.kandroid.kanboard.OnGetDefaultSwimlaneListener;
import in.andres.kandroid.kanboard.OnGetProjectUsersListener;
import in.andres.kandroid.kanboard.OnGetSwimlaneListener;
import in.andres.kandroid.kanboard.OnGetTaskListener;

public class TaskDetailActivity extends AppCompatActivity {
    private KanboardTask task;
    private KanboardCategory category;
    private KanboardSwimlane swimlane;
    private KanboardSwimlane defaultSwimlane;
    private KanboardColumn column;

    private KanboardAPI kanboardAPI;

    private OnGetAllCommentsListener commentsListener = new OnGetAllCommentsListener() {
        @Override
        public void onGetAllComments(boolean success, List<KanboardComment> comments) {
            if (success) {
                Log.v("TaskActivity", "Got Comments");
                commentListview.setAdapter(new ArrayAdapter<> (getApplicationContext(),android.R.layout.simple_list_item_1, comments));
            }
        }
    };
    private OnGetTaskListener taskListener = new OnGetTaskListener() {
        @Override
        public void onGetTask(boolean success, KanboardTask result) {
            if (success) {
                task = result;
                kanboardAPI.getCategory(task.getCategoryId());
                if (task.getSwimlaneId() == 0)
                    kanboardAPI.getDefaultSwimlane(task.getProjectId());
                else
                    kanboardAPI.getSwimlane(task.getSwimlaneId());
                setTaskDetails();
            }
        }
    };
    private OnGetProjectUsersListener usersListener = new OnGetProjectUsersListener() {
        @Override
        public void onGetProjectUsers(boolean success, Hashtable<Integer, String> result) {
            if (success) {
                textOwner.setText(Html.fromHtml(getString(R.string.taskview_owner, result.get(task.getOwnerId()))));
                textCreator.setText(Html.fromHtml(getString(R.string.taskview_creator, result.get(task.getCreatorId()))));
            }
        }
    };
    private OnGetCategoryListener categoryListener = new OnGetCategoryListener() {
        @Override
        public void onGetCategory(boolean success, KanboardCategory result) {
            if (success) {
                category = result;
                setCategoryDetails();
            }
        }
    };
    private OnGetSwimlaneListener swimlaneListener = new OnGetSwimlaneListener() {
        @Override
        public void onGetSwimlane(boolean success, KanboardSwimlane result) {
            if (success) {
                swimlane = result;
                setSwimlaneDetails(swimlane.Name);
            }
        }
    };
    private OnGetDefaultSwimlaneListener defaultSwimlaneListener = new OnGetDefaultSwimlaneListener() {
        @Override
        public void onGetDefaultSwimlane(boolean success, String name, boolean isActive) {
            if (success) {
                setSwimlaneDetails(name);
            }
        }
    };

    private TextView textCategory;
    private TextView textStatus;
    private TextView textPosition;
    private TextView textPriority;
    private TextView textSwimlane;
    private TextView textOwner;
    private TextView textCreator;
    private TextView textDateCreated;
    private TextView textDateModified;
    private TextView textDateMoved;

    private TextView textHoursEstimated;
    private TextView textHoursUsed;
    private TextView textDateStart;
    private TextView textDateDue;

    private TextView textDescription;

    private ListView commentListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        textCategory = (TextView) findViewById(R.id.text_category);
        textStatus = (TextView) findViewById(R.id.text_status);
        textPosition = (TextView) findViewById(R.id.text_position);
        textPriority = (TextView) findViewById(R.id.text_priority);
        textSwimlane = (TextView) findViewById(R.id.text_swimlane);
        textOwner = (TextView) findViewById(R.id.text_owner);
        textCreator = (TextView) findViewById(R.id.text_creator);
        textDateCreated = (TextView) findViewById(R.id.text_DateCreated);
        textDateModified = (TextView) findViewById(R.id.text_DateModified);
        textDateMoved = (TextView) findViewById(R.id.text_DateMoved);

        textHoursEstimated = (TextView) findViewById(R.id.text_HoursEstimated);
        textHoursUsed = (TextView) findViewById(R.id.text_HoursUsed);
        textDateStart = (TextView) findViewById(R.id.text_DateStart);
        textDateDue = (TextView) findViewById(R.id.text_DateDue);

        textDescription = (TextView) findViewById(R.id.text_Description);

        commentListview = (ListView) findViewById(R.id.comment_listview);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        try {
            kanboardAPI = new KanboardAPI(preferences.getString("serverurl", ""), preferences.getString("username", ""), preferences.getString("password", ""));
            kanboardAPI.addOnGetAllCommentsListener(commentsListener);
            kanboardAPI.addOnGetTaskListener(taskListener);
            kanboardAPI.addOnGetProjectUsersListener(usersListener);
            kanboardAPI.addOnGetCategoryListener(categoryListener);
            kanboardAPI.addOnGetSwimlaneListener(swimlaneListener);
            kanboardAPI.addOnGetDefaultSwimlaneListener(defaultSwimlaneListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        task = (KanboardTask) getIntent().getSerializableExtra("task");
        if (getIntent().hasExtra("column")) {
            column = (KanboardColumn) getIntent().getSerializableExtra("column");
        }

        if (getIntent().hasExtra("swimlane")) {
            swimlane = (KanboardSwimlane) getIntent().getSerializableExtra("swimlane");
            setSwimlaneDetails(swimlane.Name);
        } else {
            textSwimlane.setVisibility(View.INVISIBLE);
        }

        if (getIntent().hasExtra("category")) {
            category = (KanboardCategory) getIntent().getSerializableExtra("category");
            setCategoryDetails();
        } else {
            textCategory.setVisibility(View.INVISIBLE);
        }

        setTaskDetails();

        Log.v("TaskActivity", "Loading Comments");
        kanboardAPI.getTask(task.getId());
        kanboardAPI.getProjectUsers(task.getProjectId());
        kanboardAPI.getAllComments(task.getId());

        setupActionBar();
    }

    private void  setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(task.getTitle());
        }
    }

    private void setTaskDetails() {
        if (task.getIsActive())
            textStatus.setText(Html.fromHtml(getString(R.string.taskview_status, getString(R.string.taskview_status_open))));
        else
            textStatus.setText(Html.fromHtml(getString(R.string.taskview_status, getString(R.string.taskview_status_closed))));

        textPosition.setText(Html.fromHtml(getString(R.string.taskview_position, task.getPosition())));
        textPriority.setText(Html.fromHtml(getString(R.string.taskview_priority, task.getPriority())));
        textOwner.setText(Html.fromHtml(getString(R.string.taskview_owner, Integer.toString(task.getOwnerId()))));
        textCreator.setText(Html.fromHtml(getString(R.string.taskview_creator, Integer.toString(task.getCreatorId()))));
        textDateCreated.setText(Html.fromHtml(getString(R.string.taskview_date_created, task.getDateCreation())));
        textDateModified.setText(Html.fromHtml(getString(R.string.taskview_date_modified, task.getDateModification())));
        textDateMoved.setText(Html.fromHtml(getString(R.string.taskview_date_moved, task.getDateMoved())));

        textHoursEstimated.setText(Html.fromHtml(getString(R.string.taskview_hours_estimated, task.getTimeEstimated())));
        textHoursUsed.setText(Html.fromHtml(getString(R.string.taskview_hours_spent, task.getTimeSpent())));

        if (task.getDateStarted() != null)
            textDateStart.setText(Html.fromHtml(getString(R.string.taskview_date_start, task.getDateStarted())));
        else
            textDateStart.setVisibility(View.INVISIBLE);

        if (task.getDateDue() != null)
            textDateDue.setText(Html.fromHtml(getString(R.string.taskview_date_due, task.getDateDue())));
        else
            textDateDue.setVisibility(View.INVISIBLE);

        if (task.getDateStarted() == null && task.getDateDue() == null) {
            textDateStart.setVisibility(View.GONE);
            textDateDue.setVisibility(View.GONE);
        }

        textDescription.setText(task.getDescription());
    }

    private void setSwimlaneDetails(String swimlanename) {
        textSwimlane.setText(Html.fromHtml(getString(R.string.taskview_swimlane, swimlanename)));
        textSwimlane.setVisibility(View.VISIBLE);
    }

    private void setCategoryDetails() {
        textCategory.setText(Html.fromHtml(getString(R.string.taskview_category, category.Name)));
        textCategory.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
