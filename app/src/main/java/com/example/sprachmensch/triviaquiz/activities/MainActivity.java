package com.example.sprachmensch.triviaquiz.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sprachmensch.triviaquiz.R;
import com.example.sprachmensch.triviaquiz.data.Question;
import com.example.sprachmensch.triviaquiz.data.QuestionsSingleton;
import com.example.sprachmensch.triviaquiz.utils.MyViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private AdView adView;
    private LeaderboardsClient leaderboardsClient;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
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

    @BindView(R.id.SignInButton)
    Button signInButton;
    private int saveMeScore;
    private String LEADERBOARD_ID = "CgkIlJ_YyJEQEAIQAw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar();

        viewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        ButterKnife.bind(this);

        Timber.plant(new Timber.DebugTree());
        Timber.tag("MainActivity");

        setupAdmob();

        initGooglePlayServices();

        handleLiveData();

        Timber.d(" questionNum: %s", viewModel.questionNumber);
        if (viewModel.questionNumber == -1)
            showMenu();
        else
            showGame();
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

    private void GooglePlayServiceSignOut() {
        GoogleSignInClient client = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        client.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Timber.d("Logged out from Google Play Services!");
                        signInButton.setText(R.string.gps_login);
                        viewModel.googleIsLoggedIn = false;
                        textView.setText(getString(R.string.title_score, " -"));
                    }
                });
    }

    private void showMenu() {
        if (viewModel.bgColor != 0) {
            viewModel.bgColor = 0;
            switchBackgroundColor();
        }

        textView.setText(getString(R.string.title_score, ""));
        if (!viewModel.googleIsLoggedIn)
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
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void initGooglePlayServices() {
        googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        .build());
        googleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            GoogleSignInAccount account = task.getResult();
                            onConnected(account);
                        } else {
                            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
                        }
                    }
                });

        // handle the GPS SignIn/SignOut Button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewModel.googleIsLoggedIn)
                    initGooglePlayServices();
                else
                    GooglePlayServiceSignOut();
            }
        });
    }

    private void setupAdmob() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void switchBackgroundColor() {
        Timber.d("viewModel.bgColor : %s", viewModel.bgColor);
        container.setBackgroundColor(Color.parseColor(viewModel.bgColors[viewModel.bgColor]));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Timber.d("onActivityResult requestCode: %s", requestCode);
        if (requestCode == RC_SIGN_IN) {
            Timber.d("onConnect intent: %s", intent.getExtras().toString());
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                onConnected(signedInAccount);
            } else {
                Timber.d("onConnect NOT a Success! : %s", result.getStatus().toString());
            }
        } else if (requestCode == RC_SCORE) {
            saveScore(intent.getIntExtra("score", 0));
            saveMeScore = intent.getIntExtra("score", 0);
            Timber.d("SaveMeScore: %s", saveMeScore);
        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Timber.d("onConnect isSuccess!");
        viewModel.googleIsLoggedIn = true;
        Timber.d("onConnect got Leaderboard!");
        signInButton.setText(R.string.gps_logout);
        leaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);

        // show "welcome back" splash
        GamesClient gameClient = Games.getGamesClient(this, googleSignInAccount);
        Games.getGamesClient(this,
                googleSignInAccount).setGravityForPopups(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        gameClient.setViewForPopups(findViewById(R.id.container));

        // submit the latest score
        if (saveMeScore != 0)
            submitLeaderboardScore(saveMeScore);


        // takes the leaderboard id , Time span, From which collection
        leaderboardsClient.loadCurrentPlayerLeaderboardScore(LEADERBOARD_ID, 2, 1)
                .addOnSuccessListener(new OnSuccessListener<AnnotatedData<LeaderboardScore>>() {
                    @Override
                    public void onSuccess(AnnotatedData<LeaderboardScore> leaderboardScoreAnnotatedData) {
                        long score = 0;
                        if (leaderboardScoreAnnotatedData != null) {
                            if (leaderboardScoreAnnotatedData.get() != null) {
                                score = (int) leaderboardScoreAnnotatedData.get().getRawScore();
                                Timber.d("Got the Score! Score: %s", score);
                            } else {
                                Timber.d("leaderboardScoreAnnotatedData.get() is NULL");
                                Timber.d("data: %s ", leaderboardScoreAnnotatedData.toString());
                            }
                        } else {
                            Timber.d("leaderboardScoreAnnotatedData is NULL");
                        }
                        textView.setText(getString(R.string.title_score, " " + String.valueOf(score)));
                    }
                });
    }

    private void submitLeaderboardScore(int score) {
        leaderboardsClient.submitScore(LEADERBOARD_ID, score);
        Timber.d("submitLeaderboardScore() Submitted: " + score + " to the Leaderboard!");
    }

    private void saveScore(int score) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key_score", score);
        editor.apply();
        Timber.d("saveScore() Saved the Score: " + score + " to the SharedPreferences!");
    }
}
