package com.shift.hack.beacon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.shift.hack.beacon.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mauricio on 6/12/16.
 */
public class BeaconsAdapter extends BaseAdapter {
    private List<JsonObject> dataList;

    public void addToDataList(JsonObject jsonObject) {
        dataList.add(jsonObject);
        notifyDataSetChanged();
    }

    public void removeFromDataList(String uuid) {
        dataList.remove(getItemPos(uuid));
        notifyDataSetChanged();
    }

    public BeaconsAdapter() {
        dataList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public JsonObject getItem(int position) {
        return dataList.get(position);
    }

    public int getItemPos(String uuid) {
        int i=0;

        for (JsonObject object : dataList) {
            if (object.get("uuid").getAsString().equals(uuid)) {
                return i;
            }

            i++;
        }

        return -1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_beacons, parent, false);

        JsonObject jsonObject = dataList.get(position);

        TextView beaconName = (TextView) view.findViewById(R.id.beacon_name);
        TextView userName = (TextView) view.findViewById(R.id.user_name);

        beaconName.setText(jsonObject.get("name").getAsString());
        userName.setText(jsonObject.get("owner").getAsJsonObject().get("name").getAsString());

        return view;
    }
}
