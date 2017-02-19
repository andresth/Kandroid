package in.andres.kandroid.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import in.andres.kandroid.R;
import in.andres.kandroid.Utils;
import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardTask;
import in.andres.kandroid.kanboard.events.OnUpdateTaskListener;

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

public class TaskEditActivity extends AppCompatActivity {
    private KanboardTask task;
    private String taskTitle;
    private String taskDescription;
    private Date startDate;
    private Date dueDate;
    private double hoursEstimated;
    private double hoursSpent;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button btnStartDate;
    private Button btnDueDate;
    private EditText editHoursEstimated;
    private EditText editHoursSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        setupActionBar();

        editTextTitle = (EditText) findViewById(R.id.edit_task_title);
        editTextDescription = (EditText) findViewById(R.id.edit_task_description);
        btnStartDate = (Button) findViewById(R.id.btn_start_date);
        btnDueDate = (Button) findViewById(R.id.btn_due_date);
        editHoursEstimated = (EditText) findViewById(R.id.edit_hours_estimated);
        editHoursSpent = (EditText) findViewById(R.id.edit_hours_spent);

        if (getIntent().hasExtra("task")) {
            task = (KanboardTask) getIntent().getSerializableExtra("task");
            taskTitle = task.getTitle();
            taskDescription = task.getDescription();
            startDate = task.getDateStarted();
            dueDate = task.getDateDue();
            hoursEstimated = task.getTimeEstimated();
            hoursSpent = task.getTimeSpent();
        }

        editTextTitle.setText(taskTitle);
        editTextDescription.setText(taskDescription);
        editHoursEstimated.setText(Double.toString(hoursEstimated));
        editHoursSpent.setText(Double.toString(hoursSpent));
        btnStartDate.setText(Utils.fromHtml(getString(R.string.taskview_date_start, startDate)));
        btnDueDate.setText(Utils.fromHtml(getString(R.string.taskview_date_due, dueDate)));

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (startDate != null)
                    calendar.setTime(startDate);

                DatePickerDialog dlgDate = new DatePickerDialog(TaskEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startDate = calendar.getTime();
                        btnStartDate.setText(Utils.fromHtml(getString(R.string.taskview_date_start, startDate)));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dlgDate.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDate = null;
                        btnStartDate.setText(Utils.fromHtml(getString(R.string.taskview_date_start, startDate)));
                    }
                });
                dlgDate.show();
            }
        });
        btnDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (dueDate != null)
                    calendar.setTime(dueDate);

                DatePickerDialog dlgDate = new DatePickerDialog(TaskEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dueDate = calendar.getTime();
                        btnDueDate.setText(Utils.fromHtml(getString(R.string.taskview_date_due, dueDate)));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dlgDate.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dueDate = null;
                        btnDueDate.setText(Utils.fromHtml(getString(R.string.taskview_date_due, dueDate)));
                    }
                });
                dlgDate.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
                try {
                    KanboardAPI kanboardAPI = new KanboardAPI(preferences.getString("serverurl", ""), preferences.getString("username", ""), preferences.getString("password", ""));
                    kanboardAPI.addOnUpdateTaskListener(new OnUpdateTaskListener() {
                        @Override
                        public void onUpdateTask(boolean success) {
                            finish();
                        }
                    });
                    kanboardAPI.updateTask(task.getId(), editTextTitle.getText().toString(), null, null, null, dueDate, editTextDescription.getText().toString(), null, null, null, null, null, null, null, null, null, null);
                    ProgressBar prog = new ProgressBar(TaskEditActivity.this);
                    prog.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
                    item.setActionView(prog);
                    item.expandActionView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edittask_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.taskview_fab_edit_task));
        }
    }
}
