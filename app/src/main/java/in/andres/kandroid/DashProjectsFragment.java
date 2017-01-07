package in.andres.kandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashProjectsFragment extends ListFragment {

    public DashProjectsFragment() {}

    public static DashProjectsFragment newInstance() {
//    public static DashProjectsFragment newInstance(List<KanboardProject> values) {
        return new DashProjectsFragment();
        // FIXME: Fragment does not accept arguments
//        DashProjectsFragment fragment = new DashProjectsFragment();
//        fragment.setProjects(values);
//        Bundle args = new Bundle();
//        args.putSerializable(DashProjectsFragment.ARG_VALUES, (Serializable) values);
//        fragment.setArguments(args);
//        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expandable_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((MainActivity)getActivity()).getDashboard() != null) {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.GONE);
            getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
            DashProjectsAdapter listAdapter = new DashProjectsAdapter(getActivity(), ((MainActivity)getActivity()).getDashboard());
            ((ExpandableListView) getView().findViewById(android.R.id.list)).setAdapter(listAdapter);
        } else {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.VISIBLE);
            getView().findViewById(android.R.id.list).setVisibility(View.GONE);
        }
    }
}
