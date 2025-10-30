package com.trytodupe.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonProvider {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static Gson get() {
        return gson;
    }
}