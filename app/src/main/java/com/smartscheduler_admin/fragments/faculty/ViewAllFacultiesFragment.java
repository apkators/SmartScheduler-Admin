package com.smartscheduler_admin.fragments.faculty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartscheduler_admin.R;
import com.smartscheduler_admin.adapter_recycler_view.AdapterAllFaculty;
import com.smartscheduler_admin.adapter_recycler_view.AdapterAllFaculty;
import com.smartscheduler_admin.model.FacultyModel;
import com.smartscheduler_admin.model.FacultyModel;

import java.util.ArrayList;
import java.util.Collections;

public class ViewAllFacultiesFragment extends Fragment {

    ArrayList<FacultyModel> list;
    AdapterAllFaculty adapter;
    RecyclerView recyclerView;
    View NoRecordFoundView;
    DatabaseReference myRef;
    ProgressBar loadingBar;
    FirebaseUser firebaseUser;
    ValueEventListener allValueListener=null;
    public ViewAllFacultiesFragment() {
        // Required empty public constructor
    }

  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_all_faculties, container, false);
        NoRecordFoundView = view.findViewById(R.id.noRcdFnd);
        NoRecordFoundView.setVisibility(View.GONE);
        loadingBar = view.findViewById(R.id.loadingBar);

        myRef = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.rec);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        adapter = new AdapterAllFaculty(list, getActivity(), this);

        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    @Override
    public void onStop() {
        if (allValueListener != null) {
            myRef.removeEventListener(allValueListener);
        }
        super.onStop();
    }

    private void getData() {
        loadingBar.setVisibility(View.VISIBLE);
        myRef.child("Faculty").addValueEventListener(allValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String ID= "";
                        String NAME= "";
                        String SUBJECT= "";

                        String DEPARTMENT= "";

                        ID = dataSnapshot.getKey();

                        if (dataSnapshot.hasChild("NAME"))
                            NAME = dataSnapshot.child("NAME").getValue().toString();
                        if (dataSnapshot.hasChild("DEPARTMENT"))
                        DEPARTMENT = dataSnapshot.child("DEPARTMENT").getValue().toString();
                        if (dataSnapshot.hasChild("SUBJECT"))
                        SUBJECT = dataSnapshot.child("SUBJECT").getValue().toString();


                        list.add(new FacultyModel(ID,NAME,DEPARTMENT,SUBJECT));

                    }
                    if (list.isEmpty()) {
                        if (loadingBar.getVisibility() == View.VISIBLE)
                            loadingBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        NoRecordFoundView.setVisibility(View.VISIBLE);
                    } else {
                        Collections.reverse(list);
                        loadingBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    loadingBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    NoRecordFoundView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.setVisibility(View.GONE);
            }
        });
    }


}