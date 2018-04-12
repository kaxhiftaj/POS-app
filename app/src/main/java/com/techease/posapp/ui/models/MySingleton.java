package com.techease.posapp.ui.models;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private static RequestQueue requestQueue;
    private static Context mCtx;

    private MySingleton(Context context){
        mCtx = context;

    }
    private RequestQueue getRequestQueue(){
        if(requestQueue != null)
        {
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized MySingleton getmInstance(Context context){
        if(mInstance == null){
           mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQue(Request<T> request){
     getRequestQueue().add(request);
    }
}
