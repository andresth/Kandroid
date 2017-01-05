package in.andres.kandroid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.andres.kandroid.kanboard.KanboardColumn;
import in.andres.kandroid.kanboard.KanboardProject;

/**
 * Created by Thomas Andres on 05.01.17.
 */

public class DashProjectArrayAdapter extends ArrayAdapter<KanboardProject> {
    private final Context mContext;
    private final List<KanboardProject> mValues;

    public DashProjectArrayAdapter(Context context, List<KanboardProject> values) {
        super(context, R.layout.listitem_dash_project, values);
        mContext = context;
        mValues = values;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.listitem_dash_project, parent, false);

        TextView projectID = (TextView) rootView.findViewById(R.id.project_id);
        TextView projectName = (TextView) rootView.findViewById(R.id.project_name);
        TextView projectDescription = (TextView) rootView.findViewById(R.id.project_description);
        TextView projectColumns = (TextView) rootView.findViewById(R.id.project_columns);
        ImageView projectPrivate = (ImageView) rootView.findViewById(R.id.project_private);

        projectID.setText(String.format("#%1s", Integer.toString(mValues.get(position).ID)));
        projectName.setText(mValues.get(position).Name);
        projectPrivate.setImageDrawable(mContext.getDrawable(mValues.get(position).IsPrivate ? R.drawable.project_private : R.drawable.project_public));
        projectDescription.setText(mValues.get(position).Description);
        List<String> columns = new ArrayList<>();
        for (KanboardColumn c: mValues.get(position).Columns)
            columns.add(String.format("<big><b>%1s</b></big> %2s", c.NumberTasks, c.Title));
        projectColumns.setText(Html.fromHtml(TextUtils.join(" <big><b>|</b></big> ", columns)));

        return rootView;
    }
}
