package in.andres.kandroid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * Created by Thomas Andres on 04.01.17.
 */

public class DashProjectsFragment extends Fragment {

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
//        Log.d("ProjectsFragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_expandable_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Log.d("ProjectsFragment", "onActivityCreated");
        if (((MainActivity)getActivity()).getDashboard() != null) {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.GONE);
            getView().findViewById(R.id.expandable_list).setVisibility(View.VISIBLE);
            DashProjectsAdapter listAdapter = new DashProjectsAdapter(getActivity(), ((MainActivity)getActivity()).getDashboard());
            ((ExpandableListView) getView().findViewById(R.id.expandable_list)).setAdapter(listAdapter);
        } else {
            getView().findViewById(R.id.fragment_dash_errortext).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.expandable_list).setVisibility(View.GONE);
        }
    }
}
