package com.shift.hack.beacon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.shift.hack.beacon.adapter.TransactionsAdapter;
import com.shift.hack.beacon.model.User;
import com.shift.hack.beacon.network.ApiClient;
import com.shift.hack.beacon.network.ServiceGenerator;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    private User user;
    private TransactionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
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
        adapter = new TransactionsAdapter();

        listView.setAdapter(adapter);

        getSupportActionBar().setTitle("My Transactions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ServiceGenerator.createService(ApiClient.class).getTransactions(user.get_id()).enqueue(new Callback<ResponseBody>() {
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

                    for (int i = 0; i < ja.size(); i++) {
                        adapter.addToDataList(ja.get(i).getAsJsonObject());
                    }
                } else {
                    Log.d("AA", response.message());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("AA", t.getMessage());
                finish();
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
