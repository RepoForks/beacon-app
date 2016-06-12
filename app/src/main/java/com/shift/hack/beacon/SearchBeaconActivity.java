package com.shift.hack.beacon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shift.hack.beacon.model.User;
import com.shift.hack.beacon.network.ApiClient;
import com.shift.hack.beacon.network.ServiceGenerator;
import com.skyfishjy.library.RippleBackground;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.Collection;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBeaconActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;

    private TextView textSearching;
    private View beaconLayout;
    private Beacon beacon = null;
    private User user = null;
    private CircleImageView beaconView;
    private TextView beaconName;
    private TextView userName;
    private boolean called = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_beacon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = User.getUser(this);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        textSearching = (TextView) findViewById(R.id.text_searching);
        beaconLayout = findViewById(R.id.beacon_layout);

        CircleImageView imageView = (CircleImageView) findViewById(R.id.profile_image);

        beaconView = (CircleImageView) findViewById(R.id.beacon_image);
        beaconName = (TextView) findViewById(R.id.beacon_name);
        userName = (TextView) findViewById(R.id.user_name);

        Glide.with(this).load("http://graph.facebook.com/" + user.getFbid() + "/picture?width=200&height=200").into(imageView);

        ((RippleBackground)findViewById(R.id.ripple)).startRippleAnimation();

        beaconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                intent.putExtra("uuid", beacon.getId1().toString());
                startActivity(intent);
            }
        });

        /*Beacon beacon = new Beacon.Builder()
                .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[]{0l})) // Remove this for beacon layouts without d: fields
                .build();
        // Change the layout below for other beacon types
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: " + errorCode);
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i(TAG, "Advertisement start succeeded.");
            }
        });*/
    }

    private void loadBeacon() {
        Log.d("LOADBEACON", "LOADBEACON");
        ServiceGenerator.createService(ApiClient.class).getDevice(beacon.getId1().toString()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                Log.d("onResponse", "LOADBEACON");
                if (response.body() != null) {
                    final JsonObject jsonObject;

                    try {
                        String json = response.body().string();
                        jsonObject = new JsonParser()
                            .parse(json)
                            .getAsJsonArray()
                            .get(0)
                            .getAsJsonObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Erro... BIURR", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            textSearching.setText("1 beacon found");
                            beaconLayout.setVisibility(View.VISIBLE);
                            String uid = jsonObject.get("owner").getAsJsonObject().get("accounts")
                                .getAsJsonObject().get("facebook").getAsJsonObject().get("_id").getAsString();

                            Glide.with(getApplicationContext())
                                    .load("http://graph.facebook.com/" + uid + "/picture?width=200&height=200")
                                    .into(beaconView);

                            beaconName.setText(jsonObject.get("name").getAsString());
                            userName.setText(jsonObject.get("owner").getAsJsonObject().get("name").getAsString());
                        }
                    });
                } else {
                    Log.d("ERROR", response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("onFailure", t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");

                    beacon = (Beacon) beacons.toArray()[0];

                    if (!called) {
                        called = true;
                        loadBeacon();
                    }
                }
            }
        });

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }


}
