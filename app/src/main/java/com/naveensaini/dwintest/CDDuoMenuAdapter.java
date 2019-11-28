package com.naveensaini.dwintest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import nl.psdcompany.duonavigationdrawer.views.DuoOptionView;

public class CDDuoMenuAdapter extends BaseAdapter {


    private ArrayList<String> optionsArrayList;
    private LayoutInflater inflater;

    private ArrayList<String> newOptionsArrayList;


    public CDDuoMenuAdapter(Context context, ArrayList<String> optionsArrayList) {
        this.optionsArrayList = optionsArrayList;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return optionsArrayList.size();
    }


    @Override
    public Object getItem(int position) {
        return optionsArrayList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final DuoOptionView duoOptionView;

        if (convertView == null) {
            duoOptionView = new DuoOptionView(parent.getContext());

        } else {
            duoOptionView = (DuoOptionView) convertView;

        }

        duoOptionView.bind(optionsArrayList.get(position), null, null);

        duoOptionView.setOnClickListener(v -> Log.i("DuoMenuItem", optionsArrayList.get(position)));

        return duoOptionView;

    }
}

