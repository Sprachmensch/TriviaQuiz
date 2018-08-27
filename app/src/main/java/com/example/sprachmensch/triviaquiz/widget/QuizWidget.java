package com.example.sprachmensch.triviaquiz.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.sprachmensch.triviaquiz.R;

/**
 * Implementation of App Widget functionality.
 */
public class QuizWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);

        SharedPreferences prefs= context.getSharedPreferences("prefs",0);
        int score = prefs.getInt("key_score", -1); // getting Integer

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quiz_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        if(score==-1)
            views.setTextViewText(R.id.appwidget_score_tv, "-");
        else
            views.setTextViewText(R.id.appwidget_score_tv, ""+score);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

