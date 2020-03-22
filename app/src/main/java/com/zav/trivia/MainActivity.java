package com.zav.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zav.trivia.data.AnswerListAsyncResponse;
import com.zav.trivia.data.QuestionBank;
import com.zav.trivia.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextview;
    private TextView countdownTextView;
    private Button trueButton, falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentScore = 0, highScore = 0;
    private TextView scoreTextview, highScoreView;
    private boolean ableToClick;
    private CountDownTimer countdown = null;

    private int currentQuestionIndex = 0;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);
        countdownTextView = findViewById(R.id.counter_text);
        questionTextview = findViewById(R.id.question_textview);
        scoreTextview = findViewById(R.id.score_text);
        highScoreView = findViewById(R.id.high_score_text);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        SharedPreferences getShareData = getSharedPreferences("SAVES", MODE_PRIVATE);
        highScore = getShareData.getInt("highscore", 0);

        highScoreView.setText("High Score : " + Integer.toString(highScore));

        questionList =  new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
           @Override
           public void processFinished(ArrayList<Question> questionArrayList) {
               Collections.shuffle(questionList);
               questionTextview.setText(questionList.get(currentQuestionIndex).getAnswer());
               ableToClick = true;

           }
       });



    }

    @Override
    public void onClick(View view) {

        if(ableToClick) {
            switch (view.getId()) {
                case R.id.trueButton:
                    if(countdown != null) countdown.cancel();

                    ableToClick = false;
                    CheckAnswer(true);
                    UpdateQuestion();

                    break;
                case R.id.falseButton:
                    if(countdown != null) countdown.cancel();
                    ableToClick = false;
                    CheckAnswer(false);
                    UpdateQuestion();

                    break;
            }
        }
    }

    private void CheckAnswer(boolean userAnswer){
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();

        int toastMessageID = 0;

        if(userAnswer == answerIsTrue){
            fadeView(true);
            toastMessageID = R.string.correct_answer;

        }else {
            ShakeAnimation();
            toastMessageID = R.string.wrong_answer;
        }

        Toast.makeText(MainActivity.this, toastMessageID,
                Toast.LENGTH_SHORT).show();
    }

    public void UpdateQuestion(){
        if(currentQuestionIndex <= questionList.size()-1){
            questionTextview.setText(questionList.get(currentQuestionIndex).getAnswer());
        }else{
            //all questions finished. Simply repeat questions.
            Collections.shuffle(questionList);
            currentQuestionIndex = 0;
        }

    }


    private void fadeView(final boolean correct){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(correct) {
                    cardView.setCardBackgroundColor(Color.GREEN);
                    score(true);
                }else{
                    cardView.setCardBackgroundColor(Color.RED);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextQuestion();
                reverseFade();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void reverseFade(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);

        alphaAnimation.setDuration(150);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                ableToClick = true;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void ShakeAnimation(){
        ableToClick = false;
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake);

        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);

                score(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //reverseFade();
                //cardView.setCardBackgroundColor(Color.WHITE);
                fadeView(false);
                //nextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void nextQuestion(){
        currentQuestionIndex++;
        UpdateQuestion();
        ableToClick = true;

        if(countdown != null) countdown.cancel();

        countdown = new  CountDownTimer(7000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                countdownTextView.setText("Out of time !");
                CheckAnswer(!questionList.get(currentQuestionIndex).isAnswerTrue());
                UpdateQuestion();
            }
        }.start();


    }

    private void score(boolean correct){
        if(correct){
            currentScore++;
        }else{
            if(currentScore > 0) currentScore--;
        }
        scoreTextview.setText(Integer.toString(currentScore));

        if(currentScore > highScore) {
            highScore = currentScore;
            SharedPreferences sharedPreferences = getSharedPreferences("SAVES", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highscore", currentScore);

            editor.apply();
            highScoreView.setText("High Score : " + Integer.toString(highScore));


        }

    }


}
