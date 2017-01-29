package in.andres.kandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
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
import in.andres.kandroid.kanboard.events.OnCloseTaskListener;
import in.andres.kandroid.kanboard.events.OnCreateCommentListener;
import in.andres.kandroid.kanboard.events.OnCreateSubtaskListener;
import in.andres.kandroid.kanboard.events.OnGetAllCommentsListener;
import in.andres.kandroid.kanboard.events.OnGetAllSubtasksListener;
import in.andres.kandroid.kanboard.events.OnGetCategoryListener;
import in.andres.kandroid.kanboard.events.OnGetDefaultSwimlaneListener;
import in.andres.kandroid.kanboard.events.OnGetProjectUsersListener;
import in.andres.kandroid.kanboard.events.OnGetSwimlaneListener;
import in.andres.kandroid.kanboard.events.OnGetTaskListener;
import in.andres.kandroid.kanboard.events.OnOpenTaskListener;
import in.andres.kandroid.kanboard.events.OnRemoveCommentListener;
import in.andres.kandroid.kanboard.events.OnRemoveSubtaskListener;
import in.andres.kandroid.kanboard.events.OnUpdateCommentListener;
import in.andres.kandroid.kanboard.events.OnUpdateSubtaskListener;

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
            } else {
                findViewById(R.id.card_comments).setVisibility(View.GONE);
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
            } else {
                findViewById(R.id.card_subtasks).setVisibility(View.GONE);
            }
        }
    };
    private OnCreateCommentListener createCommentListener = new OnCreateCommentListener() {
        @Override
        public void onCreateComment(boolean success, Integer commentid) {
            if (success)
                kanboardAPI.getAllComments(task.getId());
        }
    };
    private OnUpdateCommentListener updateCommentListener = new OnUpdateCommentListener() {
        @Override
        public void onUpdateComment(boolean success) {
            if (success)
                kanboardAPI.getAllComments(task.getId());
        }
    };
    private OnRemoveCommentListener removeCommentListener = new OnRemoveCommentListener() {
        @Override
        public void onRemoveComment(boolean success) {
            if (success)
                kanboardAPI.getAllComments(task.getId());
        }
    };
    private OnCreateSubtaskListener createSubtaskListener = new OnCreateSubtaskListener() {
        @Override
        public void onCreateSubtask(boolean success, Integer result) {
            if (success)
                kanboardAPI.getAllSubtasks(task.getId());
        }
    };
    private OnUpdateSubtaskListener updateSubtaskListener = new OnUpdateSubtaskListener() {
        @Override
        public void onUpdateSubtask(boolean success) {
            if (success)
                kanboardAPI.getAllSubtasks(task.getId());
        }
    };
    private OnRemoveSubtaskListener removeSubtaskListener = new OnRemoveSubtaskListener() {
        @Override
        public void onRemoveSubtask(boolean success) {
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.comment_listview) {
            menu.setHeaderTitle(getString(R.string.menu_caption_comment,
                    ((KanboardComment)commentListview.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position)).getId()
            ));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_taskdetail_context_comment, menu);
        }
        if (v.getId() == R.id.subtask_listview) {
            menu.setHeaderTitle(getString(R.string.menu_caption_subtask,
                    ((KanboardSubtask)subtaskListview.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position)).getId()
            ));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_taskdetail_context_subtask, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit_comment:
                showCommentDialog((KanboardComment)commentListview.getAdapter().getItem(info.position));
                return true;
            case R.id.action_delete_comment:
                kanboardAPI.removeComment(((KanboardComment)commentListview.getAdapter().getItem(info.position)).getId());
                return true;
            case R.id.action_edit_subtask:
                showSubtaskDialog((KanboardSubtask)subtaskListview.getAdapter().getItem(info.position));
                return super.onContextItemSelected(item);
            case R.id.action_delete_subtask:
                kanboardAPI.removeSubtask(((KanboardSubtask)subtaskListview.getAdapter().getItem(info.position)).getId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

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
        registerForContextMenu(commentListview);

        subtaskListview = (ListView) findViewById(R.id.subtask_listview);
        registerForContextMenu(subtaskListview);

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
                collapseFABMenu();
                showCommentDialog(null);
            }
        });

        fabMenuButtonNewSubtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseFABMenu();
                showSubtaskDialog(null);
            }
        });

        fabMenuButtonOpenCloseTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseFABMenu();
                if (task.getIsActive()) {
                    kanboardAPI.closeTask(task.getId());
                } else {
                    kanboardAPI.openTask(task.getId());
                }
            }
        });

        fabMenuButtonEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseFABMenu();
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
            kanboardAPI.addOnUpdateCommentListener(updateCommentListener);
            kanboardAPI.addOnRemoveCommentListener(removeCommentListener);
            kanboardAPI.addOnCreateSubtaskListener(createSubtaskListener);
            kanboardAPI.addOnUpdateSubtaskListener(updateSubtaskListener);
            kanboardAPI.addOnRemoveSubtaskListener(removeSubtaskListener);
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

    private void showCommentDialog(@Nullable final KanboardComment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setTitle(getString(comment == null ? R.string.taskview_fab_new_comment : R.string.taskview_dlg_update_comment));
        final EditText input = new EditText(getApplicationContext());
        input.setText(comment == null ? "" : comment.getContent());
        input.setSingleLine(false);
        input.setMinLines(5);
        input.setMaxLines(10);
        input.setVerticalScrollBarEnabled(true);
        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!input.getText().toString().contentEquals("")) {
                    if (comment == null)
                        kanboardAPI.createComment(task.getId(), me.getId(), input.getText().toString());
                    else
                        kanboardAPI.updateComment(comment.getId(), input.getText().toString());
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

    private void showSubtaskDialog(@Nullable final KanboardSubtask subtask) {
        View dlgView = getLayoutInflater().inflate(R.layout.dialog_new_subtask, null);
        final EditText editTitle = (EditText) dlgView.findViewById(R.id.subtask_title);
        editTitle.setText(subtask == null ? "" : subtask.getTitle());
        final Spinner userSpinner = (Spinner) dlgView.findViewById(R.id.user_spinner);
        ArrayList<String> possibleOwners = Collections.list(users.elements());
        possibleOwners.add(0, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(self, android.R.layout.simple_spinner_item, possibleOwners);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);
        if (subtask != null)
            userSpinner.setSelection(possibleOwners.indexOf(users.get(subtask.getUserId())));

        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setTitle(getString(subtask == null ? R.string.taskview_fab_new_subtask : R.string.taskview_dlg_update_subtask));
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
                    if (subtask == null)
                        kanboardAPI.createSubtask(task.getId(), editTitle.getText().toString(), userid, null, null, null);
                    else
                        kanboardAPI.updateSubtask(subtask.getId(),subtask.getTaskId(), editTitle.getText().toString(), userid, null, null, null);
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
