package com.shift.hack.beacon.util;

import com.facebook.login.LoginResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LoginSerializer implements JsonSerializer<LoginResult> {

    @Override
    public JsonElement serialize(LoginResult src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("app_id", new JsonPrimitive(src.getAccessToken().getApplicationId()));
        jsonObject.add("access_token", new JsonPrimitive(src.getAccessToken().getToken()));
        jsonObject.add("user_id", new JsonPrimitive(src.getAccessToken().getUserId()));
        jsonObject.add("expires", new JsonPrimitive(src.getAccessToken().getExpires().toString()));
        jsonObject.add("last_refresh", new JsonPrimitive(src.getAccessToken().getLastRefresh().toString()));
        List<String> permissionsGranted = new ArrayList<>(src.getRecentlyGrantedPermissions());
        List<String> permissionsDenied = new ArrayList<>(src.getRecentlyDeniedPermissions());
        JsonArray jsonArray = new JsonArray();
        for(String str : permissionsGranted)
            jsonArray.add(str);

        jsonObject.add("granted_permissions", jsonArray);

        jsonArray = new JsonArray();
        for(String str : permissionsDenied)
            jsonArray.add(str);

        jsonObject.add("denied_permissions", jsonArray);

        return jsonObject;
    }
}
