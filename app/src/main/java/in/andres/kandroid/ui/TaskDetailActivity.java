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

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import in.andres.kandroid.BuildConfig;
import in.andres.kandroid.CompactHtmlRenderer;
import in.andres.kandroid.Constants;
import in.andres.kandroid.DownloadIntentService;
import in.andres.kandroid.R;
import in.andres.kandroid.Utils;
import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardCategory;
import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardComment;
import in.andres.kandroid.kanboard.KanboardRequest;
import in.andres.kandroid.kanboard.KanboardSubtask;
import in.andres.kandroid.kanboard.KanboardSwimlane;
import in.andres.kandroid.kanboard.KanboardTask;
import in.andres.kandroid.kanboard.KanboardTaskFile;
import in.andres.kandroid.kanboard.KanboardUserInfo;
import in.andres.kandroid.kanboard.events.OnCloseTaskListener;
import in.andres.kandroid.kanboard.events.OnCreateCommentListener;
import in.andres.kandroid.kanboard.events.OnCreateSubtaskListener;
import in.andres.kandroid.kanboard.events.OnDownloadTaskFileListener;
import in.andres.kandroid.kanboard.events.OnGetActiveSwimlanesListener;
import in.andres.kandroid.kanboard.events.OnGetAllCommentsListener;
import in.andres.kandroid.kanboard.events.OnGetAllSubtasksListener;
import in.andres.kandroid.kanboard.events.OnGetAllTaskFilesListener;
import in.andres.kandroid.kanboard.events.OnGetCategoryListener;
import in.andres.kandroid.kanboard.events.OnGetColumnsListener;
import in.andres.kandroid.kanboard.events.OnGetProjectUsersListener;
import in.andres.kandroid.kanboard.events.OnGetSwimlaneListener;
import in.andres.kandroid.kanboard.events.OnGetTaskListener;
import in.andres.kandroid.kanboard.events.OnMoveTaskPositionListener;
import in.andres.kandroid.kanboard.events.OnOpenTaskListener;
import in.andres.kandroid.kanboard.events.OnRemoveCommentListener;
import in.andres.kandroid.kanboard.events.OnRemoveSubtaskListener;
import in.andres.kandroid.kanboard.events.OnRemoveTaskFileListener;
import in.andres.kandroid.kanboard.events.OnRemoveTaskListener;
import in.andres.kandroid.kanboard.events.OnSubtaskTimetrackingListener;
import in.andres.kandroid.kanboard.events.OnUpdateCommentListener;
import in.andres.kandroid.kanboard.events.OnUpdateSubtaskListener;

public class TaskDetailActivity extends AppCompatActivity {
    private KanboardTask task;
    private KanboardCategory category;
    private KanboardSwimlane swimlane;
    private KanboardColumn column;
    private KanboardUserInfo me;
    private List<KanboardComment> comments = new ArrayList<>();
    private List<KanboardSubtask> subtasks = new ArrayList<>();
    private List<KanboardTaskFile> files = new ArrayList<>();
    private List<KanboardColumn> projectColumns;
    private List<KanboardSwimlane> projectSwimlanes;
    private Dictionary<Integer, String> users;
    private Hashtable<Integer, Double> hasTimer = new Hashtable<>();
    private int downloadFileId = -1;
    private String downloadFileName = null;
//    private HashSet<Integer> hasTimer = new HashSet<>();
    private MenuItem refreshAction;
    private MenuItem opencloseAction;
    private int activeRequests = 0;
    private boolean progressVisible = false;
    private Context self;
    private Parser mParser = Parser.builder().build();
    private HtmlRenderer mRenderer = HtmlRenderer.builder().nodeRendererFactory(new HtmlNodeRendererFactory() {
        @Override
        public NodeRenderer create(HtmlNodeRendererContext context) {
            return new CompactHtmlRenderer(context);
        }
    }).build();

    private KanboardAPI kanboardAPI;

    //region Event Listeners
    private OnGetAllCommentsListener commentsListener = new OnGetAllCommentsListener() {
        @Override
        public void onGetAllComments(boolean success, List<KanboardComment> result) {
            hideProgress();
            hideCommentProgress();
            if (success && result.size() >= 0) {
                comments = result;
                commentListview.setAdapter(new CommentAdapter (getBaseContext(), comments, true));
//                findViewById(R.id.card_comments).setVisibility(View.VISIBLE);
            } else {
//                findViewById(R.id.card_comments).setVisibility(View.GONE);
            }
        }
    };
    private OnGetTaskListener taskListener = new OnGetTaskListener() {
        @Override
        public void onGetTask(boolean success, KanboardTask result) {
            if (success) {
                task = result;
                if (task.getCategoryId() > 0) {
                    showProgress();
                    kanboardAPI.getCategory(task.getCategoryId());
                }
                if (task.getSwimlaneId() == 0) {
                    showProgress();
                    kanboardAPI.getDefaultSwimlane(task.getProjectId());
                } else {
                    showProgress();
                    kanboardAPI.getSwimlane(task.getSwimlaneId());
                }
                hideProgress();
                Log.d("Hide Progress", "OnGetTaskListener");
                setTaskDetails();
            }
        }
    };
    private OnRemoveTaskListener removeTaskListener = new OnRemoveTaskListener() {
        @Override
        public void onRemoveTask(boolean success) {
            if (success) {
                setResult(Constants.ResultChanged);
                finish();
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_remove_task), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnGetProjectUsersListener usersListener = new OnGetProjectUsersListener() {
        @Override
        public void onGetProjectUsers(boolean success, Dictionary<Integer, String> result) {
            hideProgress();
            if (success) {
                users = result;
                setTaskDetails();
//                textOwner.setText(Utils.fromHtml(getString(R.string.taskview_owner, result.get(task.getOwnerId()))));
//                textCreator.setText(Utils.fromHtml(getString(R.string.taskview_creator, result.get(task.getCreatorId()))));

                //Send change notification to update usernames in comments
                if (commentListview.getAdapter() != null)
                    ((CommentAdapter) commentListview.getAdapter()).notifyDataSetChanged();
                if (filesListview.getAdapter() != null)
                    ((TaskFilesAdapter) filesListview.getAdapter()).notifyDataSetChanged();
            }
        }
    };
    private OnGetCategoryListener categoryListener = new OnGetCategoryListener() {
        @Override
        public void onGetCategory(boolean success, KanboardCategory result) {
            hideProgress();
            if (success) {
                category = result;
                setCategoryDetails();
            }
        }
    };
    private OnGetSwimlaneListener swimlaneListener = new OnGetSwimlaneListener() {
        @Override
        public void onGetSwimlane(boolean success, KanboardSwimlane result) {
            hideProgress();
            if (success) {
                swimlane = result;
                setSwimlaneDetails(swimlane.getName());
            }
        }
    };
    private OnGetAllSubtasksListener allSubtasksListener = new OnGetAllSubtasksListener() {
        @Override
        public void onGetAllSubtasks(boolean success, List<KanboardSubtask> result) {
            hideProgress();
            hideSubtaskProgress();
            if (success && result.size() > 0) {
                subtasks = result;
                subtaskListview.setAdapter(new SubtaskAdapter(getBaseContext(), subtasks, true));
//                findViewById(R.id.card_subtasks).setVisibility(View.VISIBLE);
                for (final KanboardSubtask sub: subtasks) {
                    kanboardAPI.hasSubtaskTimer(sub.getId(), me.getId(), new OnSubtaskTimetrackingListener() {
                        @Override
                        public void onSubtaskTimetracking(boolean result, double time) {
                            if (result && !hasTimer.containsKey(sub.getId())) {
                                hasTimer.put(sub.getId(), 0d);
                                ((SubtaskAdapter) subtaskListview.getAdapter()).notifyDataSetChanged();
                                kanboardAPI.getSubtaskTimeSpent(sub.getId(), me.getId(), new OnSubtaskTimetrackingListener() {
                                    @Override
                                    public void onSubtaskTimetracking(boolean result, double time) {
                                        hasTimer.put(sub.getId(), time);
                                        ((SubtaskAdapter) subtaskListview.getAdapter()).notifyDataSetChanged();
                                    }
                                });
                            } else if (!result && hasTimer.containsKey(sub.getId())){
                                hasTimer.remove(sub.getId());
                                ((SubtaskAdapter) subtaskListview.getAdapter()).notifyDataSetChanged();
                            }
                        }
                    });
                }
            } else {
//                findViewById(R.id.card_subtasks).setVisibility(View.GONE);
            }
        }
    };
    private OnCreateCommentListener createCommentListener = new OnCreateCommentListener() {
        @Override
        public void onCreateComment(boolean success, Integer commentid) {
            if (success) {
                showProgress();
                kanboardAPI.getAllComments(task.getId());
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_create_comment), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnUpdateCommentListener updateCommentListener = new OnUpdateCommentListener() {
        @Override
        public void onUpdateComment(boolean success) {
            if (success) {
                showProgress();
                kanboardAPI.getAllComments(task.getId());
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_update_comment), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnRemoveCommentListener removeCommentListener = new OnRemoveCommentListener() {
        @Override
        public void onRemoveComment(boolean success) {
            if (success) {
                showProgress();
                kanboardAPI.getAllComments(task.getId());
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_remove_comment), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnCreateSubtaskListener createSubtaskListener = new OnCreateSubtaskListener() {
        @Override
        public void onCreateSubtask(boolean success, Integer result) {
            if (success) {
                showProgress();
                kanboardAPI.getAllSubtasks(task.getId());
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_create_subtask), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnUpdateSubtaskListener updateSubtaskListener = new OnUpdateSubtaskListener() {
        @Override
        public void onUpdateSubtask(boolean success) {
            if (success) {
                showProgress();
                kanboardAPI.getAllSubtasks(task.getId());
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_update_subtask), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnRemoveSubtaskListener removeSubtaskListener = new OnRemoveSubtaskListener() {
        @Override
        public void onRemoveSubtask(boolean success) {
            if (success) {
                showProgress();
                kanboardAPI.getAllSubtasks(task.getId());
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_remove_subtask), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnOpenTaskListener openTaskListener = new OnOpenTaskListener() {
        @Override
        public void onOpenTask(boolean success) {
            if (success) {
                textStatus.setText(Utils.fromHtml(getString(R.string.taskview_status, getString(R.string.taskview_status_open))));
                fabMenuButtonOpenCloseTask.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.task_close, null));
                fabMenuLabelOpenCloseTask.setText(getString(R.string.taskview_fab_close_task));
                if (opencloseAction != null) {
                    opencloseAction.setTitle(R.string.taskview_fab_close_task);
                }
                showProgress();
                kanboardAPI.getTask(task.getId());
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_open_task), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnCloseTaskListener closeTaskListener = new OnCloseTaskListener() {
        @Override
        public void onCloseTask(boolean success) {
            if (success) {
                textStatus.setText(Utils.fromHtml(getString(R.string.taskview_status, getString(R.string.taskview_status_closed))));
                fabMenuButtonOpenCloseTask.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.task_open, null));
                fabMenuLabelOpenCloseTask.setText(getString(R.string.taskview_fab_open_task));
                if (opencloseAction != null) {
                    opencloseAction.setTitle(R.string.taskview_fab_open_task);
                }
                showProgress();
                kanboardAPI.getTask(task.getId());
            } else
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_close_task), Snackbar.LENGTH_LONG).show();
        }
    };
    private OnGetAllTaskFilesListener getAllTaskFilesListener = new OnGetAllTaskFilesListener() {
        @Override
        public void onGetAllTaskFiles(boolean success, List<KanboardTaskFile> result) {
            hideProgress();
            hideFilesProgress();
            if (success && result.size() > 0) {
//                findViewById(R.id.card_files).setVisibility(View.VISIBLE);
                files = result;
                filesListview.setAdapter(new TaskFilesAdapter(getBaseContext(), files));
            } else {
//                findViewById(R.id.card_files).setVisibility(View.GONE);
            }
        }
    };
    private OnRemoveTaskFileListener removeTaskFileListener = new OnRemoveTaskFileListener() {
        @Override
        public void onRemoveTaskFile(boolean success) {
            if (success) {
                showProgress();
                kanboardAPI.getAllTaskFiles(task.getId());
            } else {
                Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_msg_remove_subtask), Snackbar.LENGTH_LONG).show();
            }
        }
    };
    private OnDownloadTaskFileListener downloadTaskFileListener = new OnDownloadTaskFileListener() {
        @Override
        public void onDownloadTaskFile(boolean success, int id, String data) {
            if (success) {
                byte[] inData = Base64.decode(data, Base64.DEFAULT);
                for (KanboardTaskFile f: files) {
                    if (f.getId() == id) {
                        try {
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), f.getName());
                            FileOutputStream outData = new FileOutputStream(file);
                            outData.write(inData);
                            outData.close();
                            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString()));
                            if (mime == null) {
                                mime = "application/octet-stream";
                            }
                            if (BuildConfig.DEBUG) {
                                Log.d(Constants.TAG, Uri.fromFile(file).toString());
                                Log.d(Constants.TAG, mime);
                            }
                            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            dm.addCompletedDownload(file.getName(), "Kandroid download", false, mime, file.getPath(), file.length(), true);
//                            Snackbar.make(findViewById(R.id.root_layout), String.format(Locale.getDefault(), "Saved file to: %s", file.getPath()), Snackbar.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Log.w(Constants.TAG, "IOError writing file");
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            } else {
                Snackbar.make(findViewById(R.id.root_layout), "Unable to download file", Snackbar.LENGTH_LONG).show();
            }
        }
    };
    private OnGetColumnsListener getColumnsListener = new OnGetColumnsListener() {
        @Override
        public void onGetColumns(boolean success, List<KanboardColumn> result) {
            hideProgress();
            if (success) {
                projectColumns = result;
            } else {
                Snackbar.make(findViewById(R.id.root_layout), "Error while receiving project columns", Snackbar.LENGTH_LONG).show();
            }
        }
    };
    private OnMoveTaskPositionListener moveTaskPositionListener = new OnMoveTaskPositionListener() {
        @Override
        public void onMoveTaskPosition(boolean success) {
            if (success)
                refresh();
            else
                Snackbar.make(findViewById(R.id.root_layout), "Error while moving task", Snackbar.LENGTH_LONG).show();
        }
    };
    private OnGetActiveSwimlanesListener getActiveSwimlanesListener = new OnGetActiveSwimlanesListener() {
        @Override
        public void onGetActiveSwimlanes(boolean success, List<KanboardSwimlane> result) {
            hideProgress();
            if (success) {
                projectSwimlanes = result;
            }
        }
    };
    //endregion

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

    private ListView filesListview;

    private FloatingActionButton fabMenu;
    private FloatingActionButton fabMenuButtonRemoveTask;
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
//        findViewById(R.id.card_subtasks).setVisibility(View.GONE);
//        findViewById(R.id.card_comments).setVisibility(View.GONE);
//        findViewById(R.id.card_files).setVisibility(View.GONE);

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
        textDescription.setMovementMethod(LinkMovementMethod.getInstance());

        commentListview = (ListView) findViewById(R.id.comment_listview);
        commentListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= comments.size() && !progressVisible) {
                    showCommentDialog(null);
                }
            }
        });
        registerForContextMenu(commentListview);

        subtaskListview = (ListView) findViewById(R.id.subtask_listview);
        subtaskListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= subtasks.size() && !progressVisible) {
                    showSubtaskDialog(null);
                }
            }
        });
        registerForContextMenu(subtaskListview);

        filesListview = (ListView) findViewById(R.id.files_listview);
        registerForContextMenu(filesListview);

        showCommentProgress();
        showSubtaskProgress();
        showFilesProgress();

        fabMenu = (FloatingActionButton) findViewById(R.id.fab);
        fabMenuButtonRemoveTask = (FloatingActionButton) findViewById(R.id.fab_menu_button_remove_task);
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
                setResult(Constants.ResultChanged);
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
                Intent intent = new Intent(getBaseContext(), TaskEditActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("projectusers", (Hashtable<Integer, String>)users);
                startActivityForResult(intent, Constants.RequestEditTask);
            }
        });

        fabMenuButtonRemoveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseFABMenu();
                showDeleteTaskDialog(task);
            }
        });

        commentListview.setAdapter(new CommentAdapter (getBaseContext(), comments, true));
        subtaskListview.setAdapter(new SubtaskAdapter(getBaseContext(), subtasks, true));
        filesListview.setAdapter((new TaskFilesAdapter(getBaseContext(), files)));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        try {
            kanboardAPI = new KanboardAPI(preferences.getString("serverurl", ""), preferences.getString("username", ""), preferences.getString("password", ""));
            kanboardAPI.addOnGetAllCommentsListener(commentsListener);
            kanboardAPI.addOnGetTaskListener(taskListener);
            kanboardAPI.addOnRemoveTaskListener(removeTaskListener);
            kanboardAPI.addOnGetProjectUsersListener(usersListener);
            kanboardAPI.addOnGetCategoryListener(categoryListener);
            kanboardAPI.addOnGetSwimlaneListener(swimlaneListener);
            kanboardAPI.addOnGetDefaultSwimlaneListener(swimlaneListener);
            kanboardAPI.addOnGetAllSubtasksListener(allSubtasksListener);
            kanboardAPI.addOnCreateCommentListener(createCommentListener);
            kanboardAPI.addOnUpdateCommentListener(updateCommentListener);
            kanboardAPI.addOnRemoveCommentListener(removeCommentListener);
            kanboardAPI.addOnCreateSubtaskListener(createSubtaskListener);
            kanboardAPI.addOnUpdateSubtaskListener(updateSubtaskListener);
            kanboardAPI.addOnRemoveSubtaskListener(removeSubtaskListener);
            kanboardAPI.addOnOpenTaskListener(openTaskListener);
            kanboardAPI.addOnCloseTaskListener(closeTaskListener);
            kanboardAPI.addOnGetAllTaskFilesListListeners(getAllTaskFilesListener);
            kanboardAPI.addOnRemoveTaskFileListeners(removeTaskFileListener);
            kanboardAPI.addOnDownloadTaskFileListeners(downloadTaskFileListener);
            kanboardAPI.addOnGetColumnsListener(getColumnsListener);
            kanboardAPI.addOnMoveTaskPositionListener(moveTaskPositionListener);
            kanboardAPI.addOnGetActiveSwimlanesListener(getActiveSwimlanesListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        } else {
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
                textCategory.setText(Utils.fromHtml(getString(R.string.taskview_category, getString(R.string.task_not_assigned))));
            }

            setTaskDetails();
            refresh();
        }
        setupActionBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestEditTask && resultCode == Constants.ResultChanged) {
            setResult(Constants.ResultChanged);
            refresh();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("task", task);
        outState.putSerializable("category", category);
        outState.putSerializable("swimlane", swimlane);
        outState.putSerializable("me", me);
        outState.putSerializable("users", (Hashtable<Integer, String>) users);
        outState.putSerializable("comments", (ArrayList<KanboardComment>) comments);
        outState.putSerializable("subtasks", (ArrayList<KanboardSubtask>) subtasks);
        outState.putSerializable("files", (ArrayList<KanboardTaskFile>) files);
        outState.putSerializable("projectColumns", (ArrayList<KanboardColumn>) projectColumns);
        outState.putSerializable("projectSwimlanes", (ArrayList<KanboardSwimlane>) projectSwimlanes);

        if (BuildConfig.DEBUG) Log.d(Constants.TAG, "TaskDetailActivity: saved savedInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreSavedState(savedInstanceState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.comment_listview) {
            if (((AdapterView.AdapterContextMenuInfo)menuInfo).position < comments.size()) {
                menu.setHeaderTitle(getString(R.string.menu_caption_comment,
                        ((KanboardComment) commentListview.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo) menuInfo).position)).getId()
                ));
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.activity_taskdetail_context_comment, menu);
            }
        }
        if (v.getId() == R.id.subtask_listview) {
            menu.setHeaderTitle(getString(R.string.menu_caption_subtask,
                    ((KanboardSubtask)subtaskListview.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position)).getId()
            ));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_taskdetail_context_subtask, menu);
        }
        if (v.getId() == R.id.files_listview) {
            menu.setHeaderTitle(getString(R.string.menu_caption_file,
                    ((KanboardTaskFile)filesListview.getAdapter().getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position)).getId()
            ));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_taskdetail_context_files, menu);
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
                showDeleteCommentDialog((KanboardComment)commentListview.getAdapter().getItem(info.position));
                return true;
            case R.id.action_edit_subtask:
                showSubtaskDialog((KanboardSubtask)subtaskListview.getAdapter().getItem(info.position));
                return super.onContextItemSelected(item);
            case R.id.action_delete_subtask:
                showDeleteSubtaskDialog((KanboardSubtask)subtaskListview.getAdapter().getItem(info.position));
                return true;
            case R.id.action_download_file:
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    downloadFileId = ((KanboardTaskFile) filesListview.getAdapter().getItem(info.position)).getId();
                    downloadFileName = ((KanboardTaskFile) filesListview.getAdapter().getItem(info.position)).getName();
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent downloadIntent = new Intent(this, DownloadIntentService.class);
                        downloadIntent.putExtra("request", KanboardRequest.downloadTaskFile(downloadFileId).JSON[0]);
                        downloadIntent.putExtra("filename", downloadFileName);
                        startService(downloadIntent);
                    } else {
                        ActivityCompat.requestPermissions((Activity) self,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constants.RequestStoragePermission);
                    }
                } else {
                    Snackbar.make(findViewById(R.id.root_layout), getString(R.string.error_no_sdcard), Snackbar.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_delete_file:
                showDeleteTaskFileDialog((KanboardTaskFile) filesListview.getAdapter().getItem(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.RequestStoragePermission:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (downloadFileId != -1) {
                        Intent downloadIntent = new Intent(this, DownloadIntentService.class);
                        downloadIntent.putExtra("request", KanboardRequest.downloadTaskFile(downloadFileId).JSON[0]);
                        downloadIntent.putExtra("filename", downloadFileName);
                        startService(downloadIntent);
                        downloadFileId = -1;
                        downloadFileName = null;
                    } else {
                        Snackbar.make(findViewById(R.id.root_layout), getString(R.string.permission_storage_granted), Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(findViewById(R.id.root_layout), getString(R.string.permission_storage_denied), Snackbar.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_taskdetail_main, menu);
        refreshAction = menu.findItem(R.id.action_refresh);
        opencloseAction = menu.findItem(R.id.action_close_task);
        if (task.getIsActive()) {
            opencloseAction.setTitle(R.string.taskview_fab_close_task);
        } else {
            opencloseAction.setTitle(R.string.taskview_fab_open_task);
        }
        if (activeRequests > 0 && refreshAction != null) {
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
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_new_comment:
                if (progressVisible)
                    Toast.makeText(this, getString(R.string.toast_wait_loading), Toast.LENGTH_SHORT).show();
                else
                    showCommentDialog(null);
                return true;
            case R.id.action_new_subtask:
                if (progressVisible)
                    Toast.makeText(this, getString(R.string.toast_wait_loading), Toast.LENGTH_SHORT).show();
                else
                    showSubtaskDialog(null);
                return true;
            case R.id.action_edit_task:
                Intent intent = new Intent(getBaseContext(), TaskEditActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("projectusers", (Hashtable<Integer, String>)users);
                startActivityForResult(intent, Constants.RequestEditTask);
                return true;
            case R.id.action_change_column:
                if (progressVisible)
                    Toast.makeText(this, getString(R.string.toast_wait_loading), Toast.LENGTH_SHORT).show();
                else
                    showChangeColumnDialog();
                return true;
            case R.id.action_change_swimlane:
                if (progressVisible)
                    Toast.makeText(this, getString(R.string.toast_wait_loading), Toast.LENGTH_SHORT).show();
                else
                    showChangeSwimlaneDialog();
                return true;
            case R.id.action_close_task:
                setResult(Constants.ResultChanged);
                if (task.getIsActive()) {
                    kanboardAPI.closeTask(task.getId());
                } else {
                    kanboardAPI.openTask(task.getId());
                }
                return true;
            case R.id.action_delete_task:
                showDeleteTaskDialog(task);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFABMenuOpen)
            collapseFABMenu();
        else
            super.onBackPressed();
    }

    private void restoreSavedState(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.v(Constants.TAG, "TaskDetailActivity: restore savedInstanceState.");
        task = (KanboardTask) savedInstanceState.getSerializable("task");
        category = (KanboardCategory) savedInstanceState.getSerializable("category");
        swimlane = (KanboardSwimlane) savedInstanceState.getSerializable("swimlane");
        me = (KanboardUserInfo) savedInstanceState.getSerializable("me");
        Object ou = savedInstanceState.getSerializable("users");
        if (ou instanceof HashMap)
            users = new Hashtable<>((HashMap<Integer, String>) ou);
        else
            users = (Hashtable<Integer, String>) ou;
        comments = (ArrayList<KanboardComment>) savedInstanceState.getSerializable("comments");
        subtasks = (ArrayList<KanboardSubtask>) savedInstanceState.getSerializable("subtasks");
        files = (ArrayList<KanboardTaskFile>) savedInstanceState.getSerializable("files");
        projectColumns = (ArrayList<KanboardColumn>) savedInstanceState.getSerializable("projectColumns");
        projectSwimlanes = (ArrayList<KanboardSwimlane>) savedInstanceState.getSerializable("projectSwimlanes");

        setTaskDetails();
        setCategoryDetails();
        setSwimlaneDetails(swimlane.getName());

        if (comments != null) {
            commentListview.setAdapter(new CommentAdapter (getBaseContext(), comments, true));
            hideCommentProgress();
//            findViewById(R.id.card_comments).setVisibility(View.VISIBLE);
        } else {
//            findViewById(R.id.card_comments).setVisibility(View.GONE);
        }

        if (subtasks != null) {
            subtaskListview.setAdapter(new SubtaskAdapter(getBaseContext(), subtasks, true));
            hideSubtaskProgress();
//            findViewById(R.id.card_subtasks).setVisibility(View.VISIBLE);
        } else {
//            findViewById(R.id.card_subtasks).setVisibility(View.GONE);
        }

        if (files != null) {
            filesListview.setAdapter(new TaskFilesAdapter(getBaseContext(), files));
            hideFilesProgress();
//            findViewById(R.id.card_files).setVisibility(View.VISIBLE);
            Log.d(Constants.TAG, "restore files");
        } else {
//            findViewById(R.id.card_files).setVisibility(View.GONE);
            Log.d(Constants.TAG, "no files");
        }

    }

    private void expandFABMenu() {
        Log.i(Constants.TAG, "Expand FAB.");
        ViewCompat.animate(fabMenu).rotation(90.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(5.0F)).start();
        findViewById(R.id.fab_menu_item0).startAnimation(fabOpenAnimation);
        findViewById(R.id.fab_menu_item1).startAnimation(fabOpenAnimation);
        findViewById(R.id.fab_menu_item2).startAnimation(fabOpenAnimation);
        findViewById(R.id.fab_menu_item3).startAnimation(fabOpenAnimation);
        findViewById(R.id.fab_menu_item4).startAnimation(fabOpenAnimation);
        fabMenuButtonRemoveTask.setClickable(true);
        fabMenuButtonOpenCloseTask.setClickable(true);
        fabMenuButtonNewComment.setClickable(true);
        fabMenuButtonNewSubtask.setClickable(true);
        fabMenuButtonEditTask.setClickable(true);
        isFABMenuOpen = true;
    }

    private void collapseFABMenu() {
        Log.i(Constants.TAG, "Collapse FAB.");
        ViewCompat.animate(fabMenu).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(5.0F)).start();
        findViewById(R.id.fab_menu_item0).startAnimation(fabCloseAnimation);
        findViewById(R.id.fab_menu_item1).startAnimation(fabCloseAnimation);
        findViewById(R.id.fab_menu_item2).startAnimation(fabCloseAnimation);
        findViewById(R.id.fab_menu_item3).startAnimation(fabCloseAnimation);
        findViewById(R.id.fab_menu_item4).startAnimation(fabCloseAnimation);
        fabMenuButtonRemoveTask.setClickable(false);
        fabMenuButtonOpenCloseTask.setClickable(false);
        fabMenuButtonNewComment.setClickable(false);
        fabMenuButtonNewSubtask.setClickable(false);
        fabMenuButtonEditTask.setClickable(false);
        isFABMenuOpen = false;
    }

    private void showProgress() {
        activeRequests++;
        if (activeRequests > 0 && refreshAction != null && !progressVisible) {
            ProgressBar prog = new ProgressBar(self);
            prog.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
            refreshAction.setActionView(prog);
            refreshAction.expandActionView();
            progressVisible = true;
        }
    }

    private void hideProgress() {
        activeRequests = activeRequests > 0 ? --activeRequests : 0;
        if (activeRequests == 0 && refreshAction != null && progressVisible) {
            refreshAction.collapseActionView();
            refreshAction.setActionView(null);
            progressVisible = false;
        }
    }

    private void showCommentProgress() {
        commentListview.setVisibility(View.GONE);
        findViewById(R.id.commentProgress).setVisibility(View.VISIBLE);
    }

    private void hideCommentProgress() {
        commentListview.setVisibility(View.VISIBLE);
        findViewById(R.id.commentProgress).setVisibility(View.GONE);
    }

    private void showSubtaskProgress() {
        subtaskListview.setVisibility(View.GONE);
        findViewById(R.id.subtaskProgress).setVisibility(View.VISIBLE);
    }

    private void hideSubtaskProgress() {
        subtaskListview.setVisibility(View.VISIBLE);
        findViewById(R.id.subtaskProgress).setVisibility(View.GONE);
    }

    private void showFilesProgress() {
        filesListview.setVisibility(View.GONE);
        findViewById(R.id.filesProgress).setVisibility(View.VISIBLE);
    }

    private void hideFilesProgress() {
        filesListview.setVisibility(View.VISIBLE);
        findViewById(R.id.filesProgress).setVisibility(View.GONE);
    }

    private void refresh() {
        Log.i(Constants.TAG, "Loading task data.");
        showProgress();
        kanboardAPI.getTask(task.getId());
        showProgress();
        kanboardAPI.getProjectUsers(task.getProjectId());
        showProgress();
        kanboardAPI.getAllComments(task.getId());
        showCommentProgress();
        showProgress();
        kanboardAPI.getAllSubtasks(task.getId());
        showSubtaskProgress();
        showProgress();
        kanboardAPI.getAllTaskFiles(task.getId());
        showFilesProgress();
        showProgress();
        kanboardAPI.getColumns(task.getProjectId());
        showProgress();
        kanboardAPI.getActiveSwimlanes(task.getProjectId());
    }

    private void showCommentDialog(@Nullable final KanboardComment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setTitle(getString(comment == null ? R.string.taskview_fab_new_comment : R.string.taskview_dlg_update_comment));
        final EditText input = new EditText(this);
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
                    if (comment == null) {
                        Log.i(Constants.TAG, "Creating new comment.");
                        kanboardAPI.createComment(task.getId(), me.getId(), input.getText().toString());
                    } else {
                        Log.i(Constants.TAG, "Updating comment.");
                        kanboardAPI.updateComment(comment.getId(), input.getText().toString());
                    }
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
                    if (subtask == null) {
                        Log.i(Constants.TAG, "Creating new subtask.");
                        kanboardAPI.createSubtask(task.getId(), editTitle.getText().toString(), userid, null, null, null);
                    } else {
                        Log.i(Constants.TAG, "Updating subtask.");
                        kanboardAPI.updateSubtask(subtask.getId(), subtask.getTaskId(), editTitle.getText().toString(), userid, null, null, null);
                    }
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

    private void showChangeColumnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setTitle(getText(R.string.taskview_fab_change_column));
        final Spinner input = new Spinner(this);
        ArrayAdapter<KanboardColumn> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectColumns);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input.setAdapter(adapter);
        for (int i = 0; i < projectColumns.size(); i++) {
            if (projectColumns.get(i).getId() == task.getColumnId()) {
                input.setSelection(i);
                break;
            }
        }
        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(Constants.ResultChanged);
                kanboardAPI.moveTaskPosition(task.getProjectId(), task.getId(), ((KanboardColumn)input.getSelectedItem()).getId(), 1, task.getSwimlaneId());
                dialog.dismiss();
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

    private void showChangeSwimlaneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setTitle(getText(R.string.taskview_fab_change_swimlane));
        final Spinner input = new Spinner(this);
        ArrayAdapter<KanboardSwimlane> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectSwimlanes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input.setAdapter(adapter);
        for (int i = 0; i < projectSwimlanes.size(); i++) {
            if (projectSwimlanes.get(i).getId() == task.getSwimlaneId()) {
                input.setSelection(i);
                break;
            }
        }
        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(Constants.ResultChanged);
                kanboardAPI.moveTaskPosition(task.getProjectId(), task.getId(), task.getColumnId(), 1, ((KanboardSwimlane)input.getSelectedItem()).getId());
                dialog.dismiss();
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

    private void showDeleteCommentDialog(final KanboardComment comment) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(TaskDetailActivity.this);
        dlgBuilder.setTitle(getString(R.string.delete_dlg_comment));
        dlgBuilder.setMessage(getString(R.string.delete_dlg_message));
        dlgBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                kanboardAPI.removeComment(comment.getId());
            }
        });
        dlgBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        dlgBuilder.show();
    }

    private void showDeleteSubtaskDialog(final KanboardSubtask subtask) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(TaskDetailActivity.this);
        dlgBuilder.setTitle(getString(R.string.delete_dlg_subtask));
        dlgBuilder.setMessage(getString(R.string.delete_dlg_message));
        dlgBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                kanboardAPI.removeSubtask(subtask.getId());
            }
        });
        dlgBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        dlgBuilder.show();
    }

    private void showDeleteTaskFileDialog(final KanboardTaskFile taskfile) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(TaskDetailActivity.this);
        dlgBuilder.setTitle(getString(R.string.delete_dlg_file));
        dlgBuilder.setMessage(getString(R.string.delete_dlg_message));
        dlgBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                kanboardAPI.removeTaskFile(taskfile.getId());
            }
        });
        dlgBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        dlgBuilder.show();
    }

    private void showDeleteTaskDialog(final KanboardTask task) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(TaskDetailActivity.this);
        dlgBuilder.setTitle(getString(R.string.delete_dlg_task));
        dlgBuilder.setMessage(getString(R.string.delete_dlg_message));
        dlgBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                kanboardAPI.removeTask(task.getId());
            }
        });
        dlgBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgBuilder.show();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setTaskDetails() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(task.getTitle());

        if (task.getIsActive())
            textStatus.setText(Utils.fromHtml(getString(R.string.taskview_status, getString(R.string.taskview_status_open))));
        else
            textStatus.setText(Utils.fromHtml(getString(R.string.taskview_status, getString(R.string.taskview_status_closed))));

        textPosition.setText(Utils.fromHtml(getString(R.string.taskview_position, task.getPosition())));
        textPriority.setText(Utils.fromHtml(getString(R.string.taskview_priority, task.getPriority())));
        if (task.getOwnerId() > 0)
            textOwner.setText(Utils.fromHtml(getString(R.string.taskview_owner, users == null ? Integer.toString(task.getOwnerId()) : users.get(task.getOwnerId()))));
        else
            textOwner.setText(Utils.fromHtml(getString(R.string.taskview_owner, getString(R.string.task_not_assigned))));
        textCreator.setText(Utils.fromHtml(getString(R.string.taskview_creator, users == null ? Integer.toString(task.getCreatorId()) : users.get(task.getCreatorId()))));
        textDateCreated.setText(Utils.fromHtml(getString(R.string.taskview_date_created, task.getDateCreation())));
        textDateModified.setText(Utils.fromHtml(getString(R.string.taskview_date_modified, task.getDateModification())));
        textDateMoved.setText(Utils.fromHtml(getString(R.string.taskview_date_moved, task.getDateMoved())));

        textHoursEstimated.setText(Utils.fromHtml(getString(R.string.taskview_hours_estimated, task.getTimeEstimated())));
        textHoursUsed.setText(Utils.fromHtml(getString(R.string.taskview_hours_spent, task.getTimeSpent())));

        if (task.getDateStarted() != null) {
            textDateStart.setText(Utils.fromHtml(getString(R.string.taskview_date_start, task.getDateStarted())));
            textDateStart.setVisibility(View.VISIBLE);
        } else {
            textDateStart.setVisibility(View.INVISIBLE);
        }

        if (task.getDateDue() != null) {
            textDateDue.setText(Utils.fromHtml(getString(R.string.taskview_date_due, task.getDateDue())));
            textDateDue.setVisibility(View.VISIBLE);
        } else {
            textDateDue.setVisibility(View.INVISIBLE);
        }

        if (task.getDateStarted() == null && task.getDateDue() == null) {
            textDateStart.setVisibility(View.GONE);
            textDateDue.setVisibility(View.GONE);
        }

        if (task.getDescription() != null && !task.getDescription().contentEquals("")) {
            textDescription.setText(Utils.fromHtml(mRenderer.render(mParser.parse(task.getDescription()))));
            findViewById(R.id.card_description).setVisibility(View.VISIBLE);
        }

        if (task.getIsActive()) {
            fabMenuButtonOpenCloseTask.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.task_close, null));
            fabMenuLabelOpenCloseTask.setText(getString(R.string.taskview_fab_close_task));
        } else {
            fabMenuButtonOpenCloseTask.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.task_open, null));
            fabMenuLabelOpenCloseTask.setText(getString(R.string.taskview_fab_open_task));
        }
        if (opencloseAction != null) {
            if (task.getIsActive()) {
                opencloseAction.setTitle(getString(R.string.taskview_fab_close_task));
            } else {
                opencloseAction.setTitle(getString(R.string.taskview_fab_open_task));
            }
        }
    }

    private void setSwimlaneDetails(String swimlanename) {
        textSwimlane.setText(Utils.fromHtml(getString(R.string.taskview_swimlane, swimlanename)));
        textSwimlane.setVisibility(View.VISIBLE);
    }

    private void setCategoryDetails() {
        if (category != null) {
            textCategory.setText(Utils.fromHtml(getString(R.string.taskview_category, category.getName())));
        } else {
            textCategory.setText(Utils.fromHtml(getString(R.string.taskview_category, getString(R.string.task_not_assigned))));
        }
    }

    //region internal classes
    private class SubtaskAdapter extends ArrayAdapter<KanboardSubtask> {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<KanboardSubtask> mObjects;
        private boolean mShowAdd = false;

        public SubtaskAdapter(Context context, List<KanboardSubtask> objects, boolean showAdd) {
            super(context, R.layout.listitem_subtask, objects);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mObjects = objects;
            mShowAdd = showAdd;
        }

        @Override
        public int getCount() {
            return mObjects.size() + (mShowAdd ? 1 : 0);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (position < getCount() - (mShowAdd ? 1 : 0)) {
                convertView = mInflater.inflate(R.layout.listitem_subtask, parent, false);
                convertView.setLongClickable(true);

                TextView text = (TextView) convertView.findViewById(android.R.id.text1);
                CheckBox check = (CheckBox) convertView.findViewById(android.R.id.checkbox);
                Switch toggle = (Switch) convertView.findViewById(R.id.buttonToggle);

                // Use tag to save list position
                toggle.setTag(position);
                check.setTag(position);

                final OnSubtaskTimetrackingListener startTimer = new OnSubtaskTimetrackingListener() {
                    @Override
                    public void onSubtaskTimetracking(boolean result, double time) {
                    }
                };
                final OnSubtaskTimetrackingListener stopTimer = new OnSubtaskTimetrackingListener() {
                    @Override
                    public void onSubtaskTimetracking(boolean result, double time) {
                    }
                };
                View.OnClickListener toggleClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((Switch) v).isChecked()) {
                            kanboardAPI.setSubtaskStartTime(mObjects.get((int) v.getTag()).getId(), me.getId(), startTimer);
                        } else {
                            kanboardAPI.setSubtaskEndTime(mObjects.get((int) v.getTag()).getId(), me.getId(), stopTimer);
                        }
                    }
                };

                toggle.setOnClickListener(toggleClick);
                check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((CheckBox) v).isChecked())
                            kanboardAPI.updateSubtask(mObjects.get((int) v.getTag()).getId(), mObjects.get((int) v.getTag()).getTaskId(), null, null, null, null, 2);
                        else
                            kanboardAPI.updateSubtask(mObjects.get((int) v.getTag()).getId(), mObjects.get((int) v.getTag()).getTaskId(), null, null, null, null, 0);
                    }
                });

                if (mObjects.get(position).getUserId() == me.getId())
                    toggle.setEnabled(true);
                else
                    toggle.setEnabled(false);

                double timer = 0;
                if (hasTimer.containsKey(mObjects.get(position).getId())) {
                    toggle.setChecked(true);
                    timer = hasTimer.get(mObjects.get(position).getId());
                } else
                    toggle.setChecked(false);

                if (mObjects.get(position).getStatus() == 2) {
                    text.setText(Utils.fromHtml(String.format(Locale.getDefault(), "<del>%s</del>", mObjects.get(position).getTitle())));
                    check.setChecked(true);
                } else if (mObjects.get(position).getStatus() == 1) {
                    text.setText(Utils.fromHtml(String.format(Locale.getDefault(), "<b>%s</b>", mObjects.get(position).getTitle())));
                    check.setChecked(false);
                } else {
                    text.setText(mObjects.get(position).getTitle());
                    check.setChecked(false);
                }

                text.setSelected(true);

                ((TextView) convertView.findViewById(android.R.id.text2)).setText(String.format(Locale.getDefault(), "%.2fh", mObjects.get(position).getTimeSpent() + timer));
            } else {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(getString(R.string.taskview_fab_new_subtask));
                ((TextView) convertView.findViewById(android.R.id.text1)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            return convertView;
        }
    }

    private class CommentAdapter extends ArrayAdapter<KanboardComment> {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<KanboardComment> mObjects;
        private boolean mShowAdd = false;

        public CommentAdapter(Context context, List<KanboardComment> objects, boolean showAdd) {
            super(context, R.layout.listitem_comment, objects);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mObjects = objects;
            mShowAdd = showAdd;
        }

        @Override
        public int getCount() {
            return mObjects.size() + (mShowAdd ? 1 : 0);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (position < getCount() - (mShowAdd ? 1 : 0)) {
                convertView = mInflater.inflate(R.layout.listitem_comment, parent, false);
                convertView.setLongClickable(true);
                ((TextView) convertView.findViewById(R.id.username)).setText(Utils.fromHtml(String.format("<small>%s</small>", users == null ? mObjects.get(position).getUsername() : users.get(mObjects.get(position).getUserId()))));
                ((TextView) convertView.findViewById(R.id.date)).setText(Utils.fromHtml(String.format("<small>%tF</small>", mObjects.get(position).getDateModification())));
                ((TextView) convertView.findViewById(R.id.comment)).setText(Utils.fromHtml(mRenderer.render(mParser.parse(mObjects.get(position).getContent()))));
            } else {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                ((TextView) convertView.findViewById(android.R.id.text1)).setText(getString(R.string.taskview_fab_new_comment));
                ((TextView) convertView.findViewById(android.R.id.text1)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            return convertView;
        }
    }

    private class TaskFilesAdapter extends ArrayAdapter<KanboardTaskFile> {
        private Context mContext;
        private LayoutInflater mInflater;
        List<KanboardTaskFile> mObjects;

        public TaskFilesAdapter(Context context, List<KanboardTaskFile> objects) {
            super(context, R.layout.listitem_comment, objects);
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mObjects = objects;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_taskfiles, parent, false);
                convertView.setLongClickable(true);
            }

            ((TextView) convertView.findViewById(R.id.username)).setText(Utils.fromHtml(String.format("<small>%s</small>", users == null ? mObjects.get(position).getUsername() : users.get(mObjects.get(position).getUserId()))));
            ((TextView) convertView.findViewById(R.id.date)).setText(Utils.fromHtml(String.format("<small>%tF</small>", mObjects.get(position).getFileDate())));
            ((TextView) convertView.findViewById(R.id.filename)).setText(String.format("%s", mObjects.get(position).getName()));
            double size = mObjects.get(position).getSize();
            int rounds = 0;
            while (size > 1024 && rounds < 4) {
                size /= 1024;
                rounds++;
            }
            ((TextView) convertView.findViewById(R.id.filesize)).setText(String.format(Locale.getDefault(), "%.2f %s", size, mContext.getResources().getStringArray(R.array.file_sizes)[rounds]));
            return convertView;
        }
    }
    //endregion
}
