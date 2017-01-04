package in.andres.kandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import in.andres.kandroid.kanboard.KanboardProject;
import in.andres.kandroid.kanboard.KanboardSubtask;
import in.andres.kandroid.kanboard.KanboardTask;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class TextFragment extends Fragment {

    private static final String ARG_SECTION_NAME = "section_name";

    private List<KanboardProject> mProjects = null;
    private List<KanboardTask> mTasks = null;
    private List<KanboardSubtask> mSubtasks = null;

    public TextFragment() {}

    public static TextFragment newInstance(String sectionName) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NAME, sectionName);
        fragment.setArguments(args);
        return fragment;
    }

    public void setProjects(List<KanboardProject> projects) {
        mProjects = projects;
    }

    public void setTasks(List<KanboardTask> tasks) {
        mTasks = tasks;
    }

    public void setSubtasks(List<KanboardSubtask> subtasks) {
        mSubtasks = subtasks;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.fragmentTextView);
        StringBuilder text = new StringBuilder();
        if (mProjects != null) {
            for (KanboardProject p: mProjects) {
                text.append(p.Name);
                text.append("\n");
            }
            textView.setText(text.toString());
        } else if (mTasks != null) {
            for (KanboardTask t: mTasks) {
                text.append(t.Title);
                text.append("\n");
            }
            textView.setText(text.toString());
        } else if (mSubtasks != null) {
            for (KanboardSubtask s: mSubtasks) {
                text.append(s.Title);
                text.append("\n");
            }
            textView.setText(text.toString());
        }
        return rootView;
    }
}
