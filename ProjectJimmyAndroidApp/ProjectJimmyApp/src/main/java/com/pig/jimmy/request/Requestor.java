package com.pig.jimmy.request;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.pig.jimmy.main.ProjectJimmyApplication;
import com.pig.jimmy.model.Interval;
import com.pig.jimmy.model.Sound;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad Silin on 19/03/16.
 *
 * A static class providing a convenient interface for making requests
 * A single class is sufficient for the current set of requests.
 * Categorization may be required as further functionality is added
 */
public class Requestor {
    private static final String TAG = "REQUESTOR";

    private static final String SERVER_URL = "http://192.168.0.33:5000/";

    private static final String SOUND_LIST_ENDPOINT = "projectjimmy/api/v1.0/sounds";
    private static final String AUTOPLAY_TOGGLE_ENDPOINT = "projectjimmy/api/v1.0/config/autoplay";
    private static final String ENABLE_SOUND_ENDPOINT = "projectjimmy/api/v1.0/sounds/setplayable";
    private static final String IS_PLAYING_ENDPOINT = "projectjimmy/api/v1.0/config/isplaying";
    private static final String SET_INTERVAL_ENDPOINT = "projectjimmy/api/v1.0/config/intervals/set";
    private static final String GET_INTERVAL_ENDPOINT = "projectjimmy/api/v1.0/config/intervals/get";


    public static final String SOUND_LIST_REPSONSE =
            "com.pig.jimmy.request.Requestor.soundListResponse";
    public static final String AUTO_PLAY_TOGGLE_REPSONSE =
            "com.pig.jimmy.request.Requestor.autoPlayToggleResponse";
    public static final String IS_PLAYING_RESPONSE =
            "com.pig.jimmy.request.Requestor.isPlaying";
    public static final String GET_INTERVAL_REPSONSE =
            "com.pig.jimmy.request.Requestor.getIntervalResponse";

    public static final String SOUND_LIST_INTENT_KEY = "soundList";

    public static final String INTERVAL_TYPE_MIN = "min_interval";
    public static final String INTERVAL_TYPE_MAX = "max_interval";
    public static final String INTERVAL_INTENT_KEY = "interval";
    public static final String IS_PLAYING_INTENT_KEY = "isPlaying";
    public static final String INTERVAL_TYPE_INTENT_KEY = "interval_type";

    private Requestor() {
    }

    private static String getSoundListURL() {
        return SERVER_URL + SOUND_LIST_ENDPOINT;
    }

    private static String getIsPlayingURL() {
        return SERVER_URL + IS_PLAYING_ENDPOINT;
    }

    private static String getToggleAutoplayURL() {
        return SERVER_URL + AUTOPLAY_TOGGLE_ENDPOINT;
    }

    private static String getEnableSoundURL(int soundID) {
        return SERVER_URL + ENABLE_SOUND_ENDPOINT + "/" + soundID;
    }

    private static String getGetIntervalURL(String intervalType) {
        return SERVER_URL + GET_INTERVAL_ENDPOINT + "/" + intervalType;
    }

    private static String getSetIntervalURL(String intervalType) {
        return SERVER_URL + SET_INTERVAL_ENDPOINT + "/" + intervalType;
    }

    public static void makeSoundListRequest() {
        JsonObjectRequest soundListRequest = new JsonObjectRequest(Request.Method.GET,
                getSoundListURL(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject soundListResponse) {
                Log.d(TAG, "SOUND LIST RESPONSE RECEIVED");

                String jsonArrayString = soundListResponse.toString();

                JsonParser jsonParser = new JsonParser();
                String json = jsonParser.parse(jsonArrayString).getAsJsonObject().get("sounds")
                        .getAsJsonArray().toString();

                Gson gson = new Gson();
                Type type = new TypeToken<List<Sound>>() {}.getType();
                ArrayList<Sound> soundList = gson.fromJson(json, type);

                Intent intent = new Intent(SOUND_LIST_REPSONSE);
                intent.putParcelableArrayListExtra(SOUND_LIST_INTENT_KEY, soundList);

                LocalBroadcastManager.getInstance(ProjectJimmyApplication.getContext())
                        .sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("SOUND LIST REQUEST ERROR");
            }
        });

        JimmyRequestQueue.getInstance(ProjectJimmyApplication.getContext())
                .addToRequestQueue(soundListRequest);
    }

    public static void makeIsPlayingRequest() {
        StringRequest getIsPlayingRequest = new StringRequest(Request.Method.GET,
                getIsPlayingURL(), new Response.Listener<String>() {
            @Override
            public void onResponse(String isPlayingResponse) {
                Log.d(TAG, "IS_PLAYING RESPONSE RECEIVED");

                boolean isPlaying = Boolean.parseBoolean(isPlayingResponse);
                System.out.println("At request: " + isPlayingResponse);

                Intent intent = new Intent(IS_PLAYING_RESPONSE);
                intent.putExtra(IS_PLAYING_INTENT_KEY, isPlaying);

                LocalBroadcastManager.getInstance(ProjectJimmyApplication.getContext())
                        .sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("IS_PLAYING RESPONSE ERROR");
            }
        });

        JimmyRequestQueue.getInstance(ProjectJimmyApplication.getContext())
                .addToRequestQueue(getIsPlayingRequest);
    }

    public static void makeToggleAutoPlayRequest() {
        StringRequest toggleAutoplayRequest = new StringRequest(Request.Method.GET,
                getToggleAutoplayURL(), new Response.Listener<String>() {
            @Override
            public void onResponse(String isPlayingResponse) {
                Log.d(TAG, "AUTOPLAY RESPONSE RECEIVED");

                boolean isPlaying = Boolean.parseBoolean(isPlayingResponse);
                System.out.println("At request: " + isPlayingResponse);

                Intent intent = new Intent(AUTO_PLAY_TOGGLE_REPSONSE);
                intent.putExtra(IS_PLAYING_INTENT_KEY, isPlaying);

                LocalBroadcastManager.getInstance(ProjectJimmyApplication.getContext())
                        .sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("AUTOPLAY RESPONSE ERROR");
            }
        });

        JimmyRequestQueue.getInstance(ProjectJimmyApplication.getContext())
                .addToRequestQueue(toggleAutoplayRequest);
    }

    public static void makeEnableSoundRequest(Sound sound, boolean enable) {
        sound.setPlayable(enable ? 1 : 0);

        JSONObject newSoundJsonObject = null;
        try {
            Gson gson = new Gson();
            newSoundJsonObject = new JSONObject(gson.toJson(sound));
        } catch (JSONException e) {
            Log.e("JSON", "Unable to parse json from sound object");
        }

        if (newSoundJsonObject == null) return;

        int soundID = sound.getSoundID();

        JsonObjectRequest enableSoundRequest = new JsonObjectRequest(Request.Method.PUT,
                getEnableSoundURL(soundID), newSoundJsonObject,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject enableSoundResponse) {
                Log.d(TAG, "ENABLE SOUND RESPONSE RECEIVED");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("ENABLE SOUND RESPONSE ERROR");
            }
        });

        JimmyRequestQueue.getInstance(ProjectJimmyApplication.getContext())
                .addToRequestQueue(enableSoundRequest);
    }

    public static void makeIntervalSetRequest(Interval interval, String intervalType) {
        JSONObject intervalJSONObject = null;
        try {
            Gson gson = new Gson();
            intervalJSONObject = new JSONObject(gson.toJson(interval));
        } catch (JSONException e) {
            Log.e("JSON", "Unable to parse json from sound object");
        }

        if (intervalJSONObject == null) return;

        JsonObjectRequest setIntervalRequest = new JsonObjectRequest(Request.Method.PUT,
                getSetIntervalURL(intervalType), intervalJSONObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject setIntervalResponse) {
                        Log.d(TAG, "INTERVAL SET RESPONSE RECEIVED");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("INTERVAL SET RESPONSE ERROR");
            }
        });

        JimmyRequestQueue.getInstance(ProjectJimmyApplication.getContext())
                .addToRequestQueue(setIntervalRequest);
    }

    public static void makeIntervalGetRequest(final String intervalType) {
        JsonObjectRequest setIntervalRequest = new JsonObjectRequest(Request.Method.GET,
                getGetIntervalURL(intervalType), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject getIntervalResponse) {
                        Log.d(TAG, "INTERVAL GET RESPONSE RECEIVED");

                        Gson gson = new Gson();
                        String jsonString = getIntervalResponse.toString();
                        Interval interval = gson.fromJson(jsonString, Interval.class);

                        System.out.println(interval.getHours() + interval.getMinutes());

                        Intent intent = new Intent(GET_INTERVAL_REPSONSE);
                        intent.putExtra(INTERVAL_INTENT_KEY, interval);
                        intent.putExtra(INTERVAL_TYPE_INTENT_KEY, intervalType);

                        LocalBroadcastManager.getInstance(ProjectJimmyApplication.getContext())
                                .sendBroadcast(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                System.out.println("INTERVAL GET RESPONSE ERROR");
            }
        });

        JimmyRequestQueue.getInstance(ProjectJimmyApplication.getContext())
                .addToRequestQueue(setIntervalRequest);
    }
}
