package com.example.sprachmensch.triviaquiz.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sprachmensch.triviaquiz.data.Question;
import com.example.sprachmensch.triviaquiz.data.QuestionDatabase;
import com.example.sprachmensch.triviaquiz.data.QuestionsSingleton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import timber.log.Timber;

public class Repository {

    private QuestionsSingleton questionsSingleton;
    private QuestionDatabase questionDatabase;
    private boolean dbIsPopulated;

    public Repository(final Context context) {
        Timber.plant(new Timber.DebugTree());
        Timber.tag("Repository");
        Timber.d("lets start ...");

        dbIsPopulated = checkDB(context);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (questionDatabase.daoAccess().questionCount() == 0) {
                    loadJSON(context);
                    Timber.d("Load JSON - because DB is empty!");
                } else {
                    loadDB();
                    Timber.d("DB is not empty!");
                }
            }
        });
    }


    private void loadDB() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final List<Question> questions = questionDatabase.daoAccess().getAllQuestionsNonLive();

                if (questionsSingleton == null) {
                    questionsSingleton = new QuestionsSingleton(questions);
                }
            }
        });
    }

    private void loadJSON(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String triviaURL = "https://opentdb.com/api.php?amount=50&difficulty=easy&type=multiple";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, triviaURL, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Gson gson = new GsonBuilder().create();
                            JSONArray temp = response.getJSONArray("results");

                            Type category = new TypeToken<List<Question>>() {
                            }.getType();
                            final List<Question> questions = gson.fromJson(temp.toString(), category);

                            if (questionsSingleton == null)
                                questionsSingleton = new QuestionsSingleton(questions);

                            final QuestionDatabase questionDatabase = QuestionDatabase.getInstance(context);
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    questionDatabase.daoAccess().insertJSON(questions);
                                    Timber.d( "Added questions to the DB");
                                }
                            });


                        } catch (JSONException e) {
                            Timber.d(  " JSONException:" + e.toString() + " RELOAD AGAIN!!!:");
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Timber.e(  "VolleyError: %s", error.toString());
                    }
                });
        queue.add(jsonObjectRequest);
    }

    private boolean checkDB(Context context) {
        questionDatabase = QuestionDatabase.getInstance(context);
        boolean isPopulated = false;

        if (questionDatabase.isBuild())
            isPopulated = true;

        Timber.d(  "isPopulated:%s", isPopulated);

        return isPopulated;
    }
}
