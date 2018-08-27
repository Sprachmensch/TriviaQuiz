package com.example.sprachmensch.triviaquiz.activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sprachmensch.triviaquiz.R;
import com.example.sprachmensch.triviaquiz.data.QuestionsSingleton;
import com.example.sprachmensch.triviaquiz.utils.MyViewModel;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import timber.log.Timber;

public class QuizActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.ImageButton)
    Button ImageButton1;
    @BindView(R.id.ImageButton2)
    Button ImageButton2;
    @BindView(R.id.ImageButton3)
    Button ImageButton3;
    @BindView(R.id.ImageButton4)
    Button ImageButton4;

    @BindView(R.id.container)
    ConstraintLayout container;
    @BindView(R.id.viewKonfetti)
    KonfettiView viewKonfetti;

    private MyViewModel viewModel;
    private Random rnd;
    private QuestionsSingleton questionsSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        viewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        if (savedInstanceState != null) {
            viewModel.questionNumber = savedInstanceState.getInt("key_questionNum");
            viewModel.bgColor = savedInstanceState.getInt("key_bgColor");
        } else {
//            viewModel.questionNumber = 0;
            loadQuestionsNumber();
            if (viewModel.questionNumber >= 50)
                viewModel.questionNumber = 0;
        }


        rnd = new Random();
        if (questionsSingleton == null)
            questionsSingleton = QuestionsSingleton.getInstance();

        // Bind Butterknife
        ButterKnife.bind(this);

        // Init Timber
        Timber.plant(new Timber.DebugTree());
        Timber.tag("QuizActivity");

        if (questionsSingleton.getQuestions() != null)
            getNextQuestion();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("key_questionNum", viewModel.questionNumber);
        outState.putInt("key_bgColor", viewModel.bgColor);
    }

    private void getNextQuestion() {
        resetBtnBackgroundColors();
        switchBackgroundColor();

        Timber.d("isloaded: %s", questionsSingleton.isLoaded());
        textView.setText("QUESTION #" + viewModel.questionNumber + "\n" + Html.fromHtml(questionsSingleton.get(viewModel.questionNumber).getQuestion()));

        setRightButton(questionsSingleton.get(viewModel.questionNumber).getCorrectAnswer(),
                questionsSingleton.get(viewModel.questionNumber).getIncorrectAnswer1(),
                questionsSingleton.get(viewModel.questionNumber).getIncorrectAnswer2(),
                questionsSingleton.get(viewModel.questionNumber).getIncorrectAnswer3());
    }

    private void switchBackgroundColor() {
        container.setBackgroundColor(Color.parseColor(viewModel.bgColors[viewModel.bgColor]));
    }

    private void resetBtnBackgroundColors() {
        ImageButton1.setBackgroundColor(Color.parseColor("#0A000000"));
        ImageButton2.setBackgroundColor(Color.parseColor("#0A000000"));
        ImageButton3.setBackgroundColor(Color.parseColor("#0A000000"));
        ImageButton4.setBackgroundColor(Color.parseColor("#0A000000"));
    }

    private void setRightButton(String answer1, String answer2, String answer3, String answer4) {
        /*
                set the text to all btns
                first is the correct one
                get a rnd number ( 1-4 )
                switch the first with the rnd number
                then set the onclicklistener
         */

        ImageButton1.setText(Html.fromHtml(answer1));
        ImageButton2.setText(Html.fromHtml(answer2));
        ImageButton3.setText(Html.fromHtml(answer3));
        ImageButton4.setText(Html.fromHtml(answer4));

        final int ButtonNumber = rnd.nextInt(4);
        switchButtons(ButtonNumber);

        ImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton1.setBackgroundColor(Color.parseColor("#53000000"));
                if (ButtonNumber == 0)
                    nextRound();
                else
                    GameOver();
            }
        });

        ImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton2.setBackgroundColor(Color.parseColor("#53000000"));
                if (ButtonNumber == 1)
                    nextRound();
                else
                    GameOver();
            }
        });

        ImageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton3.setBackgroundColor(Color.parseColor("#53000000"));
                if (ButtonNumber == 2)
                    nextRound();
                else
                    GameOver();
            }
        });

        ImageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton4.setBackgroundColor(Color.parseColor("#53000000"));
                if (ButtonNumber == 3)
                    nextRound();
                else
                    GameOver();

            }
        });
    }

    private void GameOver() {
        increaseBackgroundColor();
        container.setBackgroundColor(Color.parseColor("#fe6868"));
        textView.setText(R.string.game_gameover);
        textView.setText(textView.getText() + questionsSingleton.get(viewModel.questionNumber).getCorrectAnswer());

//        viewModel.questionNumber = 0;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                saveQuestionsNumber(viewModel.questionNumber);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("score", viewModel.score);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }, 2000);
    }

    private void increaseBackgroundColor() {
        viewModel.bgColor++;

        if (viewModel.bgColor >= 4)
            viewModel.bgColor = 0;
    }

    private void showKonfetti() {
        int x = viewKonfetti.getWidth();
        int y = viewKonfetti.getHeight();

        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.CYAN, Color.MAGENTA)
                .setDirection(0.0, 179.0)
                .setSpeed(4f, 8f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT)
                .addSizes(new Size(12, 4f), new Size(8, 6f))
                .setPosition(0, x * 1f, 0, 0f)
                .streamFor(100, 1000L);

        Timber.d(" viewKonfetti x:" + x + " y: " + y);
    }

    private void nextRound() {
        increaseBackgroundColor();
        showKonfetti();
        viewModel.questionNumber++;
        viewModel.score++;

        textView.setText(R.string.quiz_correctBtn);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getNextQuestion();
            }
        }, 1500);
    }

    private void switchButtons(int ButtonNumber) {
        String temp = null;

        if (ButtonNumber == 0) {
            ImageButton1.setText(ImageButton1.getText() + "°");
        } else if (ButtonNumber == 1) {
            temp = ImageButton1.getText().toString();
            ImageButton1.setText(ImageButton2.getText());
            ImageButton2.setText(temp + "°");
        } else if (ButtonNumber == 2) {
            temp = ImageButton1.getText().toString();
            ImageButton1.setText(ImageButton3.getText());
            ImageButton3.setText(temp + "°");
        } else if (ButtonNumber == 3) {
            temp = ImageButton1.getText().toString();
            ImageButton1.setText(ImageButton4.getText());
            ImageButton4.setText(temp + "°");
        }
    }

    private void loadQuestionsNumber() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("prefs", 0);
        viewModel.questionNumber = prefs.getInt("key_number", 0);
    }

    private void saveQuestionsNumber(int number) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key_number", viewModel.questionNumber);
        editor.apply();
        Timber.d("saveQuestionsNumber() Saved #" + viewModel.questionNumber + " to the SharedPreferences!");
    }
}