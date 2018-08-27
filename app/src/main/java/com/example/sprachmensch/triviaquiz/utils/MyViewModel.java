package com.example.sprachmensch.triviaquiz.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.sprachmensch.triviaquiz.data.Question;
import com.example.sprachmensch.triviaquiz.data.QuestionDatabase;

import java.util.List;

import timber.log.Timber;

public class MyViewModel extends AndroidViewModel {
    public int score = 0;
    public int questionNumber = -1;
    public int bgColor = 0;
    public String[] bgColors = {"#95d159", "#fe6868", "#70b9f2", "#fcb328"};
    public boolean googleIsLoggedIn;
    private LiveData<List<Question>> questions;

    public MyViewModel(@NonNull Application application) {
        super(application);
        Timber.plant(new Timber.DebugTree());
        Timber.tag("MyViewModel");
        Timber.d("created the ViewModel");

        QuestionDatabase questionDatabase = QuestionDatabase.getInstance(application);
        questions = questionDatabase.daoAccess().getAllQuestions();
    }

    public LiveData<List<Question>> getQuestions() {
        return questions;
    }
}
