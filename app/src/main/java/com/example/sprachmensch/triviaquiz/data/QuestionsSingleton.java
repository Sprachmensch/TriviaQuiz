package com.example.sprachmensch.triviaquiz.data;

import java.util.List;

import timber.log.Timber;

public class QuestionsSingleton {
    private static List<Question> questions = null;
    private static QuestionsSingleton INSTANCE = null;
    private final boolean isLoaded;

    public QuestionsSingleton(List<Question> questions) {
        Timber.plant(new Timber.DebugTree());
        this.questions = questions;
        isLoaded = true;
        Timber.tag("QuestionsSingleton").d("created a Singleton");
    }

    public static synchronized QuestionsSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QuestionsSingleton(questions);
        }
        return (INSTANCE);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public Question get(int i) {
        return questions.get(i);
    }

    public List<Question> getQuestions() {
        return questions;
    }

}
