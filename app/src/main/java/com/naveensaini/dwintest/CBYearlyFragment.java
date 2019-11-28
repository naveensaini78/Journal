package com.naveensaini.dwintest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CBYearlyFragment extends Fragment {

    static RecyclerView cRecyclerView;
    static CCOtherEntriesRecyclerAdapter cbYearlyAdapter;


    public CBYearlyFragment() {
        // Required empty public constructor
    }


    public static CBYearlyFragment newInstance() {
        CBYearlyFragment fragment = new CBYearlyFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cc_fragment_yearly, container, false);

        makeRecyclerViewReady(view);

        return view;

    }


    private void makeRecyclerViewReady(View view) {

        cRecyclerView = view.findViewById(R.id.cYearlyRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManager);

        cbYearlyAdapter = new CCOtherEntriesRecyclerAdapter(getContext(), 3,
                AApplicationClass.yearlyReferenceNunberList, AApplicationClass.yearlyTitleList,
                AApplicationClass.yearlyTextList, AApplicationClass.yearlyImageList,
                AApplicationClass.yearlyMediaStringList, AApplicationClass.yearlyTagStringList);
        cRecyclerView.setAdapter(cbYearlyAdapter);


    }
}