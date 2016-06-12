package com.shift.hack.beacon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.shift.hack.beacon.adapter.BeaconsAdapter;
import com.shift.hack.beacon.model.User;
import com.shift.hack.beacon.network.ApiClient;
import com.shift.hack.beacon.network.ServiceGenerator;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeaconsActivity extends AppCompatActivity {

    private User user;
    private BeaconsAdapter adapter;
    private TextView noBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = User.getUser(this);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        ListView listView = (ListView) findViewById(R.id.listView);
        noBeacons = (TextView) findViewById(R.id.noBeacons);
        adapter = new BeaconsAdapter();

        listView.setAdapter(adapter);

        getSupportActionBar().setTitle("My Beacons");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BeaconActivity.class);
                intent.putExtra("beacon", adapter.getItem(position).toString());
                startActivity(intent);
            }
        });

        noBeacons.setVisibility(View.GONE);
        ServiceGenerator.createService(ApiClient.class).getBeacons(user.get_id()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    JsonArray ja;

                    try {
                        ja = new JsonParser().parse(response.body().string()).getAsJsonArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                        finish();
                        return;
                    }

                    Log.d("AAABBB", ja.toString());

                    for (int i = 0; i < ja.size(); i++) {
                        adapter.addToDataList(ja.get(i).getAsJsonObject());
                    }

                    if (ja.size() == 0) {
                        noBeacons.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d("AA", response.message());
                    noBeacons.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("AA", t.getMessage());
                noBeacons.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
