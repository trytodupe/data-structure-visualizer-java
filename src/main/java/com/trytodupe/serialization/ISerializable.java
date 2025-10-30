package com.trytodupe.serialization;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

public interface ISerializable {

    /**
     * Serialize this object to a JsonObject, including its class type.
     */
    default JsonObject toJson(Gson gson) {
        JsonObject obj = gson.toJsonTree(this).getAsJsonObject();
        obj.addProperty("type", this.getClass().getName());
        return obj;
    }

    /**
     * Deserialize a JsonObject to an ISerializable subclass.
     */
    static ISerializable fromJson(Gson gson, JsonObject obj) {
        try {
            String type = obj.get("type").getAsString();
            Class<?> clazz = Class.forName(type);
            ISerializable instance = (ISerializable) gson.fromJson(obj, clazz);
            instance.postDeserialize();
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize object: " + obj, e);
        }
    }

    default void postDeserialize() {}
}
