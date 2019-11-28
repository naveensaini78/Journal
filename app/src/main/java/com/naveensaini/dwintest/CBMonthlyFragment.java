package com.naveensaini.dwintest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CBMonthlyFragment extends Fragment {

    static RecyclerView cRecyclerView;
    static CCOtherEntriesRecyclerAdapter cbMonthlyAdapter;


    public CBMonthlyFragment() {
        // Required empty public constructor
    }


    public static CBMonthlyFragment newInstance() {
        CBMonthlyFragment fragment = new CBMonthlyFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cc_fragment_monthly, container, false);

        makeRecyclerViewReady(view);

        return view;
    }


    private void makeRecyclerViewReady(View view) {

        cRecyclerView = view.findViewById(R.id.cMonthlyRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        cRecyclerView.setLayoutManager(linearLayoutManager);

        cbMonthlyAdapter = new CCOtherEntriesRecyclerAdapter(getContext(), 2,
                AApplicationClass.monthlyReferenceNunberList, AApplicationClass.monthlyTitleList,
                AApplicationClass.monthlyTextList, AApplicationClass.monthlyImageList,
                AApplicationClass.monthlyMediaStringList, AApplicationClass.monthlyTagStringList);
        cRecyclerView.setAdapter(cbMonthlyAdapter);


    }
}