package com.naveensaini.dwintest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class DDTagsRecyclerAdapter extends RecyclerView.Adapter<DDTagsRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> tagsArrayList = new ArrayList<>();
    private ArrayList<String> entryTagsArrayList = new ArrayList<>();
    private int entryType;
    private int passedPosition;

    boolean checkedListenerDisabled;


    public DDTagsRecyclerAdapter(Context context, ArrayList<String> tagsArrayList, String entryTagsString,
                                 int dEntryType, int dPassedPosition) {
        this.context = context;
        this.tagsArrayList = tagsArrayList;
        this.entryType = dEntryType;
        this.passedPosition = dPassedPosition;
        checkedListenerDisabled = false;

        makeTagsListFromString(entryTagsString);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dd_tags_recycler_entry, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Will be enabled in the end of BindViewHolder
        checkedListenerDisabled = true;


        // Initiating the list
        Log.i("XJDDT-onBindViewHolder", String.valueOf(position));

        if (tagsArrayList != null && !tagsArrayList.isEmpty()) {

            holder.tagNameText.setText(tagsArrayList.get(position));
            if (entryTagsArrayList != null && !entryTagsArrayList.isEmpty()) {
                if (entryTagsArrayList.contains(tagsArrayList.get(position))) {
                    holder.tagCheckBox.setChecked(true);
                } else {
                    holder.tagCheckBox.setChecked(false);
                }
            }

        }


        checkedListenerDisabled = false;
    }


    @Override
    public int getItemCount() {
        return tagsArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView tagNameText;
        protected CheckBox tagCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tagNameText = itemView.findViewById(R.id.ddTagEntryText);
            tagCheckBox = itemView.findViewById(R.id.ddTagEntryCheckBox);


            tagCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!checkedListenerDisabled) {
                    if (isChecked) {
                        entryTagsArrayList.add(tagsArrayList.get(getAdapterPosition()));
                    } else {
                        entryTagsArrayList.remove(tagsArrayList.get(getAdapterPosition()));
                    }


                    String newTagsString = null;
                    if (entryTagsArrayList != null && !entryTagsArrayList.isEmpty()) {
                        try {
                            newTagsString = ZZObjectSerializer.serialize(entryTagsArrayList);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                    ((DAEntryViewActivity) context).setTagsText(newTagsString);
                    DAEntryViewActivity.dTagsString = newTagsString;
                    changeTagStringInMainArrayList(newTagsString);

                    AApplicationClass.zzUtilitiesClass.addNewTagsStringToDataBase
                            (DAEntryViewActivity.dEntryReferenceNumber, newTagsString);

                    Log.i("XJDDT-onBindVH", "Completed");
                }
            });
        }
    }


    ////////////////////////// Other methods //////////////////
    private void makeTagsListFromString(String entryTagsString) {
        if (entryTagsString != null) {
            try {
                entryTagsArrayList = (ArrayList<String>) ZZObjectSerializer.deserialize(entryTagsString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void changeTagStringInMainArrayList(String newTagsString) {
        switch (entryType) {
            case 0:
                AApplicationClass.dailyTagStringList.set(passedPosition, newTagsString);
                break;
            case 1:
                AApplicationClass.weeklyTagStringList.set(passedPosition, newTagsString);
                break;
            case 2:
                AApplicationClass.monthlyTagStringList.set(passedPosition, newTagsString);
                break;
            case 3:
                AApplicationClass.yearlyTagStringList.set(passedPosition, newTagsString);
                break;
        }

    }
}









