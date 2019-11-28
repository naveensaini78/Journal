package com.naveensaini.dwintest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CBWeeklyFragment extends Fragment {

    static RecyclerView cRecyclerView;
    static CCOtherEntriesRecyclerAdapter cbWeeklyAdapter;


    public CBWeeklyFragment() {
    }


    public static CBWeeklyFragment newInstance() {
        CBWeeklyFragment fragment = new CBWeeklyFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("XJCBW-onCreate", "WeeklyFragment");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cc_fragment_weekly, container, false);

        makeRecyclerViewReady(view);

        Log.i("XJCBW-onCreateView", "WeeklyFragment");

        return view;
    }


    private void makeRecyclerViewReady(View view) {

        cRecyclerView = view.findViewById(R.id.cWeeklyRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManager);

        cbWeeklyAdapter = new CCOtherEntriesRecyclerAdapter(getContext(), 1,
                AApplicationClass.weeklyReferenceNunberList, AApplicationClass.weeklyTitleList,
                AApplicationClass.weeklyTextList, AApplicationClass.weeklyImageList,
                AApplicationClass.weeklyMediaStringList, AApplicationClass.weeklyTagStringList);
        cRecyclerView.setAdapter(cbWeeklyAdapter);


    }
}