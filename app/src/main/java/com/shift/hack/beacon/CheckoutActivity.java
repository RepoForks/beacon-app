package com.shift.hack.beacon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shift.hack.beacon.model.User;
import com.simplify.android.sdk.Card;
import com.simplify.android.sdk.CardToken;
import com.simplify.android.sdk.Simplify;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class CheckoutActivity extends AppCompatActivity {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private User user;
    private Card mCard;
    private CreditCardForm mCardForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Simplify.init("sbpb_MDYzNjdmYjYtOGFmMC00MWQ4LTg4MTEtYzM2YjBmM2VjYTlk");
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


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // create a new card object
        mCard = new Card().setAddressZip("12345");

        // init card editor
        mCardForm = (CreditCardForm) findViewById(R.id.credit_card_form);
        final Button checkoutButton = (Button) findViewById(R.id.buttonPay);
        // add state change listener
        // add checkout button click listener
        assert checkoutButton != null;
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCard.setNumber(mCardForm.getCreditCard().getCardNumber().replaceAll("\\s+",""));
                Log.v("CARD_NUMBER",  mCardForm.getCreditCard().getCardNumber());
                mCard.setExpMonth(mCardForm.getCreditCard().getExpDate().split("/")[0]);
                mCard.setExpYear(mCardForm.getCreditCard().getExpDate().split("/")[1]);
                mCard.setCvc(mCardForm.getCreditCard().getSecurityCode());
                mCard.setAddressZip(mCardForm.getCreditCard().getZipCode());


                // create a card token
                Simplify.createCardToken(mCard, new CardToken.Callback() {
                    @Override
                    public void onSuccess(CardToken cardToken) {
                        Intent intent = new Intent(getApplicationContext(), ConfirmedActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "Invalid card", Toast.LENGTH_LONG).show();
                        Log.v("ERROR", "Error: ", throwable);
                    }
                });
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Checkout Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.shift.hack.beacon/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Checkout Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.shift.hack.beacon/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                mCard.setNumber(scanResult.cardNumber);
                mCardForm.setCardNumber(scanResult.cardNumber, false);

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    mCard.setExpMonth(String.valueOf(scanResult.expiryMonth));
                    mCard.setExpYear(String.valueOf(scanResult.expiryYear));
                    mCardForm.setExpDate(scanResult.expiryMonth+"/"+scanResult.expiryYear, false);
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    mCard.setCvc(scanResult.cvv);
                    mCardForm.setSecurityCode(scanResult.cvv, false);
                }

                if (scanResult.postalCode != null) {
                    mCard.setAddressZip(scanResult.postalCode);
                    mCardForm.setZipCode(scanResult.postalCode, false);
                }
                if (mCardForm.isCreditCardValid()) {
                    Log.v("CARD", "Valid!");
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
                Log.v("CARD", resultDisplayStr);
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }
}
