package com.shift.hack.beacon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shift.hack.beacon.model.User;

public class CheckoutActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = User.getUser(this);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        String json = getIntent().getExtras().getString("beaconJson");
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        String fbid = jsonObject.get("owner").getAsJsonObject().get("accounts").getAsJsonObject()
                        .get("facebook").getAsJsonObject().get("_id").getAsString();

        ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
        TextView titleText = (TextView) findViewById(R.id.title);
        TextView priceText = (TextView) findViewById(R.id.price);
        MaterialEditText editPrice = (MaterialEditText) findViewById(R.id.editPrice);

        Glide.with(this).load("https://graph.facebook.com/" + fbid +
                "/picture?width=200&height=200&access_token=" + user.getToken()).into(profileImage);

        float total = jsonObject.get("value").getAsFloat();
        float received = jsonObject.get("amountReceived").getAsFloat();

        titleText.setText(jsonObject.get("name").getAsString());
        priceText.setText(String.format("R$ %.2f", total - received));
        editPrice.setHint(String.format("R$ %.2f", total - received));
        editPrice.setText("" + ((int) (total - received)));

        getSupportActionBar().setTitle(jsonObject.get("owner").getAsJsonObject().get("name").getAsString());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.buttonPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfirmedActivity.class);
                startActivity(intent);
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
