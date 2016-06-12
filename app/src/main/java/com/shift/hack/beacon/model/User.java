package com.shift.hack.beacon.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by mauricio on 6/12/16.
 */
public class User {

    public String _id;
    public String name;
    public String fbid;
    public String token;

    public User(JsonObject object) {
        Log.d("USER", object.toString());
        _id = object.get("_id").getAsString();
        name = object.get("name").getAsString();

        try {
            fbid = object.get("accounts").getAsJsonObject().get("facebook")
                    .getAsJsonObject().get("_id").getAsString();
            token = object.get("accounts").getAsJsonObject().get("facebook")
                    .getAsJsonObject().get("token").getAsString();
        } catch (NullPointerException ignore) {
            fbid = object.get("fbid").getAsString();
            token = object.get("token").getAsString();
        }
    }

    public static User fromJson(String json) {
        if (json.equals("{}")) return null;
        return new User((new JsonParser().parse(json)).getAsJsonObject());
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("_id", _id);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("fbid", fbid);
        jsonObject.addProperty("token", token);
        return jsonObject;
    }

    public static void setUser(User user, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("BEACON", Context.MODE_PRIVATE);
        sharedPref.edit().putString("user", user.toJson().toString()).commit();
    }

    public static User getUser(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("BEACON", Context.MODE_PRIVATE);
        return User.fromJson(sharedPref.getString("user", "{}"));
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFbid() {
        return fbid;
    }

    public void setFbid(String fbid) {
        this.fbid = fbid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
