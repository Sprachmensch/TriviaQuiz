package com.example.sprachmensch.triviaquiz.activities;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.sprachmensch.triviaquiz.R;
import com.example.sprachmensch.triviaquiz.data.Question;
import com.example.sprachmensch.triviaquiz.data.QuestionsSingleton;
import com.example.sprachmensch.triviaquiz.utils.MyViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SCORE = 7001;
    private MyViewModel viewModel;

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.ImageButton2)
    Button ImageButton2;
    @BindView(R.id.ImageButton4)
    Button ImageButton4;

    @BindView(R.id.container)
    ConstraintLayout container;

    private int saveMeScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar();

        viewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        ButterKnife.bind(this);

        Timber.plant(new Timber.DebugTree());
        Timber.tag("MainActivity");

        handleLiveData();

        Timber.d(" questionNum: %s", viewModel.questionNumber);
        if (viewModel.questionNumber == -1)
            showMenu();
        else
            showGame();

        loadScore();
    }

    private void handleLiveData() {
        viewModel.getQuestions().observe(this, new Observer<List<Question>>() {
            @Override
            public void onChanged(@Nullable List<Question> questions) {
                QuestionsSingleton questionsSingleton = new QuestionsSingleton(questions);
                Timber.d("LIVEDATA via MainActivity");
            }
        });
    }

    private void showMenu() {
        if (viewModel.bgColor != 0) {
            viewModel.bgColor = 0;
            switchBackgroundColor();
        }

        textView.setText(getString(R.string.title_score, " -"));

        ImageButton2.setText(R.string.start_main);
        ImageButton2.setBackgroundColor(Color.parseColor("#0A000000"));

        ImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.questionNumber = 0;
                showGame();
            }
        });

        ImageButton4.setText(R.string.about_main);
        ImageButton4.setBackgroundColor(Color.parseColor("#0A000000"));
        ImageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAbout();
            }
        });
    }

    private void showGame() {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivityForResult(intent, RC_SCORE);
    }

    private void showAbout() {
        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);

        TextView content=dialog.findViewById(R.id.content);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setText(Html.fromHtml(getString(R.string.about_text)));

        dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void switchBackgroundColor() {
        Timber.d("viewModel.bgColor : %s", viewModel.bgColor);
        container.setBackgroundColor(Color.parseColor(viewModel.bgColors[viewModel.bgColor]));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Timber.d("onActivityResult requestCode: %s", requestCode);
        if (requestCode == RC_SCORE) {
            saveScore(intent.getIntExtra("score", 0));
            saveMeScore = intent.getIntExtra("score", 0);
            Timber.d("SaveMeScore: %s", saveMeScore);
            textView.setText(getString(R.string.title_score, "" + saveMeScore));
        }
    }

    private void saveScore(int score) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key_score", score);
        editor.apply();
        Timber.d("saveScore() Saved the Score: " + score + " to the SharedPreferences!");
    }

    private void loadScore() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("prefs", 0);
        saveMeScore=prefs.getInt("key_score", 0);
        textView.setText(getString(R.string.title_score, "" + saveMeScore));
        Timber.d("loadScore() Loaded the Score: " + saveMeScore + " from the SharedPreferences!");
    }
}
