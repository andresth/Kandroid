package in.andres.kandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardCategory;
import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardComment;
import in.andres.kandroid.kanboard.KanboardSubtask;
import in.andres.kandroid.kanboard.KanboardSwimlane;
import in.andres.kandroid.kanboard.KanboardTask;
import in.andres.kandroid.kanboard.KanboardUserInfo;
import in.andres.kandroid.kanboard.events.*;

public class TaskDetailActivity extends AppCompatActivity {
    private KanboardTask task;
    private KanboardCategory category;
    private KanboardSwimlane swimlane;
    private KanboardColumn column;
    private KanboardUserInfo me;
    private Hashtable<Integer, String> users;
    Context self;

    private KanboardAPI kanboardAPI;

    private OnGetAllCommentsListener commentsListener = new OnGetAllCommentsListener() {
        @Override
        public void onGetAllComments(boolean success, List<KanboardComment> result) {
            if (success && result.size() > 0) {
                commentListview.setAdapter(new ArrayAdapter<> (getApplicationContext(),android.R.layout.simple_list_item_1, result));
                findViewById(R.id.card_comments).setVisibility(View.VISIBLE);
            }
        }
    };
    private OnGetTaskListener taskListener = new OnGetTaskListener() {
        @Override
        public void onGetTask(boolean success, KanboardTask result) {
            if (success) {
                task = result;
                if (task.getCategoryId() > 0)
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
                users = result;
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
                setSwimlaneDetails(swimlane.getName());
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
    private OnGetAllSubtasksListener allSubtasksListener = new OnGetAllSubtasksListener() {
        @Override
        public void onGetAllSubtasks(boolean success, List<KanboardSubtask> result) {
            if (success && result.size() > 0) {
                subtaskListview.setAdapter(new SubtaskAdapter(getApplicationContext(), result));
                findViewById(R.id.card_subtasks).setVisibility(View.VISIBLE);
            }
        }
    };
    private OnCreateCommentListener createCommentListener = new OnCreateCommentListener() {
        @Override
        public void onCreateComment(boolean success, Integer commentid) {
            Log.d("createComment", Boolean.toString(success));
            if (success)
                kanboardAPI.getAllComments(task.getId());
        }
    };
    private OnCreateSubtaskListener createSubtaskListener = new OnCreateSubtaskListener() {
        @Override
        public void onCreateSubtask(boolean success, Integer result) {
            Log.d("createSubtask", Boolean.toString(success));
            if (success)
                kanboardAPI.getAllSubtasks(task.getId());
        }
    };
    private OnOpenTaskListener openTaskListener = new OnOpenTaskListener() {
        @Override
        public void onOpenTask(boolean success) {
            if (success) {
                textStatus.setText(Html.fromHtml(getString(R.string.taskview_status, getString(R.string.taskview_status_open))));
                fabMenuButtonOpenCloseTask.setImageDrawable(getDrawable(R.drawable.task_close));
                fabMenuLabelOpenCloseTask.setText(getString(R.string.taskview_fab_close_task));
                kanboardAPI.getTask(task.getId());
            }
        }
    };
    private OnCloseTaskListener closeTaskListener = new OnCloseTaskListener() {
        @Override
        public void onCloseTask(boolean success) {
            if (success) {
                textStatus.setText(Html.fromHtml(getString(R.string.taskview_status, getString(R.string.taskview_status_closed))));
                fabMenuButtonOpenCloseTask.setImageDrawable(getDrawable(R.drawable.task_open));
                fabMenuLabelOpenCloseTask.setText(getString(R.string.taskview_fab_open_task));
                kanboardAPI.getTask(task.getId());
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

    private ListView subtaskListview;

    private FloatingActionButton fabMenu;
    private FloatingActionButton fabMenuButtonOpenCloseTask;
    private FloatingActionButton fabMenuButtonNewComment;
    private FloatingActionButton fabMenuButtonNewSubtask;
    private FloatingActionButton fabMenuButtonEditTask;
    private TextView fabMenuLabelOpenCloseTask;

    private Animation fabCloseAnimation;
    private Animation fabOpenAnimation;

    private boolean isFABMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        self = this;

        findViewById(R.id.card_description).setVisibility(View.GONE);
        findViewById(R.id.card_subtasks).setVisibility(View.GONE);
        findViewById(R.id.card_comments).setVisibility(View.GONE);

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

        subtaskListview = (ListView) findViewById(R.id.subtask_listview);

        fabMenu = (FloatingActionButton) findViewById(R.id.fab);
        fabMenuButtonOpenCloseTask = (FloatingActionButton) findViewById(R.id.fab_menu_button_open_close_task);
        fabMenuButtonNewComment = (FloatingActionButton) findViewById(R.id.fab_menu_button_new_comment);
        fabMenuButtonNewSubtask = (FloatingActionButton) findViewById(R.id.fab_menu_button_new_subtask);
        fabMenuButtonEditTask = (FloatingActionButton) findViewById(R.id.fab_menu_button_edit_task_task);
        fabMenuLabelOpenCloseTask = (TextView) findViewById(R.id.fab_menu_label_open_close_task);

        fabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open);

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.fab).getVisibility() == View.VISIBLE) {
                    if (isFABMenuOpen)
                        collapseFABMenu();
                    else
                        expandFABMenu();
                }
            }
        });

        ((NestedScrollView) findViewById(R.id.activity_task_detail)).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY - scrollY > 0)
                    ((FloatingActionButton) findViewById(R.id.fab)).show();
                else if (oldScrollY - scrollY < 0) {
                    if (isFABMenuOpen)
                        collapseFABMenu();
                    ((FloatingActionButton) findViewById(R.id.fab)).hide();
                    ViewCompat.setRotation(fabMenu, 0.0F);
                }
            }
        });

        fabMenuButtonNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(self);
                builder.setTitle(getString(R.string.taskview_fab_new_comment));
                final EditText input = new EditText(getApplicationContext());
                input.setSingleLine(false);
                input.setMinLines(5);
                input.setMaxLines(10);
                input.setVerticalScrollBarEnabled(true);
                builder.setView(input);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().contentEquals("")) {
                            kanboardAPI.createComment(task.getId(), me.getId(), input.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        fabMenuButtonNewSubtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dlgView = getLayoutInflater().inflate(R.layout.dialog_new_subtask, null);
                final Spinner userSpinner = (Spinner) dlgView.findViewById(R.id.user_spinner);
                final EditText editTitle = (EditText) dlgView.findViewById(R.id.subtask_title);
                ArrayList<String> possibleOwners = Collections.list(users.elements());
                possibleOwners.add(0, "");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(self, android.R.layout.simple_spinner_item, possibleOwners);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userSpinner.setAdapter(adapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(self);
                builder.setTitle(getString(R.string.taskview_fab_new_subtask));
                builder.setView(dlgView);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer userid = null;
                        if (userSpinner.getSelectedItem() != null) {
                            for (Enumeration<Integer> iter = users.keys(); iter.hasMoreElements();) {
                                Integer key = iter.nextElement();
                                if (users.get(key).contentEquals((String) userSpinner.getSelectedItem())) {
                                    userid = key;
                                    break;
                                }
                            }
                        }
                        if (!editTitle.getText().toString().equalsIgnoreCase("")) {
                            kanboardAPI.createSubtask(task.getId(), editTitle.getText().toString(), userid, null, null, null);
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        fabMenuButtonOpenCloseTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getIsActive()) {
                    kanboardAPI.closeTask(task.getId());
                } else {
                    kanboardAPI.openTask(task.getId());
                }
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        try {
            kanboardAPI = new KanboardAPI(preferences.getString("serverurl", ""), preferences.getString("username", ""), preferences.getString("password", ""));
            kanboardAPI.addOnGetAllCommentsListener(commentsListener);
            kanboardAPI.addOnGetTaskListener(taskListener);
            kanboardAPI.addOnGetProjectUsersListener(usersListener);
            kanboardAPI.addOnGetCategoryListener(categoryListener);
            kanboardAPI.addOnGetSwimlaneListener(swimlaneListener);
            kanboardAPI.addOnGetDefaultSwimlaneListener(defaultSwimlaneListener);
            kanboardAPI.addOnGetAllSubtasksListener(allSubtasksListener);
            kanboardAPI.addOnCreateCommentListener(createCommentListener);
            kanboardAPI.addOnCreateSubtaskListener(createSubtaskListener);
            kanboardAPI.addOnOpenTaskListener(openTaskListener);
            kanboardAPI.addOnCloseTaskListener(closeTaskListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        me = (KanboardUserInfo) getIntent().getSerializableExtra("me");
        task = (KanboardTask) getIntent().getSerializableExtra("task");
        if (getIntent().hasExtra("column")) {
            column = (KanboardColumn) getIntent().getSerializableExtra("column");
        }

        if (getIntent().hasExtra("swimlane")) {
            swimlane = (KanboardSwimlane) getIntent().getSerializableExtra("swimlane");
            setSwimlaneDetails(swimlane.getName());
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

        if (task.getIsActive()) {
            fabMenuButtonOpenCloseTask.setImageDrawable(getDrawable(R.drawable.task_close));
            fabMenuLabelOpenCloseTask.setText(getString(R.string.taskview_fab_close_task));
        } else {
            fabMenuButtonOpenCloseTask.setImageDrawable(getDrawable(R.drawable.task_open));
            fabMenuLabelOpenCloseTask.setText(getString(R.string.taskview_fab_open_task));
        }

        kanboardAPI.getTask(task.getId());
        kanboardAPI.getProjectUsers(task.getProjectId());
        kanboardAPI.getAllComments(task.getId());
        kanboardAPI.getAllSubtasks(task.getId());
        setupActionBar();
    }

    private void expandFABMenu() {
        ViewCompat.animate(fabMenu).rotation(45.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        findViewById(R.id.fab_menu_item1).startAnimation(fabOpenAnimation);
        findViewById(R.id.fab_menu_item2).startAnimation(fabOpenAnimation);
        findViewById(R.id.fab_menu_item3).startAnimation(fabOpenAnimation);
        findViewById(R.id.fab_menu_item4).startAnimation(fabOpenAnimation);
        fabMenuButtonOpenCloseTask.setClickable(true);
        fabMenuButtonNewComment.setClickable(true);
        fabMenuButtonNewSubtask.setClickable(true);
        fabMenuButtonEditTask.setClickable(true);
        isFABMenuOpen = true;
    }

    private void collapseFABMenu() {
        ViewCompat.animate(fabMenu).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        findViewById(R.id.fab_menu_item1).startAnimation(fabCloseAnimation);
        findViewById(R.id.fab_menu_item2).startAnimation(fabCloseAnimation);
        findViewById(R.id.fab_menu_item3).startAnimation(fabCloseAnimation);
        findViewById(R.id.fab_menu_item4).startAnimation(fabCloseAnimation);
        fabMenuButtonOpenCloseTask.setClickable(false);
        fabMenuButtonNewComment.setClickable(false);
        fabMenuButtonNewSubtask.setClickable(false);
        fabMenuButtonEditTask.setClickable(false);
        isFABMenuOpen = false;
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

        if (task.getDescription() != null && !task.getDescription().contentEquals("")) {
            textDescription.setText(task.getDescription());
            findViewById(R.id.card_description).setVisibility(View.VISIBLE);
        }
    }

    private void setSwimlaneDetails(String swimlanename) {
        textSwimlane.setText(Html.fromHtml(getString(R.string.taskview_swimlane, swimlanename)));
        textSwimlane.setVisibility(View.VISIBLE);
    }

    private void setCategoryDetails() {
        textCategory.setText(Html.fromHtml(getString(R.string.taskview_category, category.getName())));
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

    @Override
    public void onBackPressed() {
        if (isFABMenuOpen)
            collapseFABMenu();
        else
            super.onBackPressed();
    }

    private class SubtaskAdapter extends ArrayAdapter<KanboardSubtask> {
        private Context mContext;
        private LayoutInflater mInflater;
        List<KanboardSubtask> mObjects;

        public SubtaskAdapter(Context context, List<KanboardSubtask> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mObjects = objects;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            if (mObjects.get(position).getStatus() == 2) {
                text.setText(Html.fromHtml(String.format(Locale.getDefault(), "<del>%s</del>", mObjects.get(position).getTitle())));
            } else if (mObjects.get(position).getStatus() == 1) {
                text.setText(Html.fromHtml(String.format(Locale.getDefault(), "<b>%s</b>", mObjects.get(position).getTitle())));
            } else {
                text.setText(mObjects.get(position).getTitle());
            }

            return convertView;
        }
    }
}
