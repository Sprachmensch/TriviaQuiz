package com.example.sprachmensch.triviaquiz.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DAOAccess {

    @Insert
    void insertJSON(List<Question> questions);

    @Query("SELECT * FROM Question")
    LiveData<List<Question>> getAllQuestions();

    @Query("SELECT * FROM Question")
    List<Question> getAllQuestionsNonLive();

    //count and give back how many questions are in the db
    @Query("SELECT COUNT(*) FROM Question")
    int questionCount();

}
