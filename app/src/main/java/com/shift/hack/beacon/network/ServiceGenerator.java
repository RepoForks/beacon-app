package com.shift.hack.beacon.network;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.shift.hack.beacon.model.Device;
import com.shift.hack.beacon.model.User;
import com.shift.hack.beacon.util.ParameterizedTypeImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    public static final String API_BASE_URL = "http://bconfy.herokuapp.com/v1/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static GsonBuilder gsonBuilder;
    private static Retrofit.Builder builder;

    public static <T> T createService(Class<T> serviceClass) {
        gsonBuilder = new GsonBuilder();
        registerTypeAdapter(gsonBuilder, Device.class);
        registerTypeAdapter(gsonBuilder, User.class);

        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));

        Retrofit retrofit = builder.client(httpClient.build()).build();

        return retrofit.create(serviceClass);
    }

    private static void registerTypeAdapter(GsonBuilder gsonBuilder, Class<?> clazz) {
        gsonBuilder.registerTypeAdapter(clazz, new SingleDeserializer(clazz));
        Type type = ParameterizedTypeImpl.make(List.class, new Type[]{clazz}, null);
        gsonBuilder.registerTypeAdapter(type, new ListDeserializer(clazz));
    }

    static class SingleDeserializer<T> implements JsonDeserializer<T> {

        private final Class<T> clazz;

        public SingleDeserializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            T obj = null;

            try {
                obj = clazz.getConstructor(JsonObject.class).newInstance(je.getAsJsonObject());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return obj;
        }
    }

    static class ListDeserializer<T> implements JsonDeserializer<List<T>> {

        private final Class<T> clazz;

        public ListDeserializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public List<T> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            JsonArray content = je.getAsJsonArray();
            List<T> list = new ArrayList<>();

            for (int i=0; i<content.size(); i++) {
                try {
                    list.add(clazz.getConstructor(JsonObject.class).newInstance(content.get(i).getAsJsonObject()));
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            return list;
        }
    }
}
