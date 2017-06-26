package com.ebabu.tooreest.beans;

import org.json.JSONObject;

/**
 * Created by hp on 16/02/2017.
 */
public class DataHandler {
    private static DataHandler dataHandler = null;

    public static DataHandler getInstance() {
        if (dataHandler == null) {
            dataHandler = new DataHandler();
        }
        return dataHandler;
    }

    private JSONObject jsonObject;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public static void reset() {
        try {
            dataHandler.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        dataHandler = null;
    }
}
