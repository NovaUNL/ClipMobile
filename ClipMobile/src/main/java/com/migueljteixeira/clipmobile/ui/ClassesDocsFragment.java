package com.migueljteixeira.clipmobile.ui;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.adapters.StudentClassesDocsAdapter;
import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentClassDoc;
import com.migueljteixeira.clipmobile.network.StudentClassesDocsRequest;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.util.tasks.GetStudentClassesDocsTask;
import com.migueljteixeira.clipmobile.util.tasks.GetStudentYearsTask;
import com.migueljteixeira.clipmobile.util.tasks.UpdateStudentNumbersTask;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ClassesDocsFragment extends BaseFragment implements GetStudentClassesDocsTask.OnTaskFinishedListener {

    private int lastExpandedGroupPosition;
    private StudentClassesDocsAdapter mListAdapter;
    private List<StudentClassDoc> classDocs;
    @InjectView(R.id.list_view) ExpandableListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lastExpandedGroupPosition = -1;
        classDocs = new LinkedList<StudentClassDoc>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_classes_docs, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListAdapter = new StudentClassesDocsAdapter(getActivity(),
                getResources().getStringArray(R.array.classes_docs_array), classDocs);
        mListView.setAdapter(mListAdapter);
        mListView.setOnGroupClickListener(onGroupClickListener);
        mListView.setOnChildClickListener(onChildClickListener);

        // unfinished task around?
        /*if ( ( mYearsTask != null && mYearsTask.getStatus() != AsyncTask.Status.FINISHED ) ||
                ( mUpdateTask != null && mUpdateTask.getStatus() != AsyncTask.Status.FINISHED ) )
            showProgressSpinnerOnly(true);

        // The view has been loaded already
        if(mListAdapter != null) {
            mListView.setAdapter(mListAdapter);
            mListView.setOnGroupClickListener(onGroupClickListener);
            mListView.setOnChildClickListener(onChildClickListener);
            return;
        }*/
    }

    ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if(mListView.isGroupExpanded(groupPosition))
                mListView.collapseGroup(groupPosition);

            else {
                // show Progress Bar
                showProgressSpinnerOnly(true);

                GetStudentClassesDocsTask mDocsTask = new GetStudentClassesDocsTask(getActivity(), ClassesDocsFragment.this);
                mDocsTask.execute(groupPosition);
            }

            return true;
        }
    };

    @Override
    public void onTaskFinished(Student result, int groupPosition) {
        showProgressSpinnerOnly(false);

        List<StudentClassDoc> docs = result.getClassesDocs();
        for(StudentClassDoc d : docs)
                System.out.println("-> " + d.getName() + " , " + d.getSize());

        // Server is unavailable right now
        if(result == null || result.getClassesDocs().size() == 0) return;

        // Collapse last expanded group
        if(lastExpandedGroupPosition != -1)
            mListView.collapseGroup(lastExpandedGroupPosition);

        lastExpandedGroupPosition = groupPosition;

        // Set new data and notifyDataSetChanged
        classDocs.clear();
        classDocs.addAll(result.getClassesDocs());
        mListAdapter.notifyDataSetChanged();

        // Expand group position
        mListView.expandGroup(groupPosition, true);
    }

    ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            String name = classDocs.get(childPosition).getName();
            String url = "https://clip.unl.pt" + classDocs.get(childPosition).getUrl();

            System.out.println("-> uurl " + url);

            // Download document
            StudentClassesDocsRequest.downloadDoc(getActivity(), name, url);

            return true;
        }
    };
}
