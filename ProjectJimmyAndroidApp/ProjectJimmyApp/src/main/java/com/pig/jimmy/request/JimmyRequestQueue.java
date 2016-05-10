package com.pig.jimmy.request;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Vlad Silin on 18/03/16.
 *
 * A singleton class for interaction with the Volley request queue
 */
public class JimmyRequestQueue {
    private static JimmyRequestQueue mInstance;
    private RequestQueue mRequestQueue;

    private Context mContext;

    private JimmyRequestQueue(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized JimmyRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new JimmyRequestQueue(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
