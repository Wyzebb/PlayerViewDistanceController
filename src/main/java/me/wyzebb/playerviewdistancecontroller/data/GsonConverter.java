package me.wyzebb.playerviewdistancecontroller.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.wyzebb.playerviewdistancecontroller.models.WorldDataRow;

public class GsonConverter {
    public static WorldDataRow stringToModel(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.fromJson(jsonString, WorldDataRow.class);
    }

    public static String modelToString(WorldDataRow wdr) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(wdr);
    }
}
