package com.naveensaini.dwintest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CBDailyFragment extends Fragment {

    static RecyclerView cDailyRecyclerView;
    static CCDailyRecyclerAdapter CCDailyRecyclerAdapter;


    public CBDailyFragment() {
    }


    public static CBDailyFragment newInstance() {
        CBDailyFragment fragment = new CBDailyFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.cc_fragment_daily, container, false);

        makeRecyclerViewReady(view);

        return view;
    }


    private void makeRecyclerViewReady(View view) {

        cDailyRecyclerView = view.findViewById(R.id.cDailyRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        cDailyRecyclerView.setLayoutManager(linearLayoutManager);

        CCDailyRecyclerAdapter = new CCDailyRecyclerAdapter(getContext(), 0,
                AApplicationClass.dailyReferenceNunberList,
                AApplicationClass.dailyTitleList,
                AApplicationClass.dailyTextList,
                AApplicationClass.dailyImageList,
                AApplicationClass.dailyMediaStringList,
                AApplicationClass.dailyTagStringList);
        cDailyRecyclerView.setAdapter(CCDailyRecyclerAdapter);


    }
}