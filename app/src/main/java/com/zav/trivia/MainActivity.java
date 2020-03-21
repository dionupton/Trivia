package com.zav.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextview;
    private TextView questionCounterTextview;
    private Button trueButton, falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;

    private int currentQuestionIndex = 0;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);
        questionCounterTextview = findViewById(R.id.counter_text);
        questionTextview = findViewById(R.id.question_textview);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);


        questionList =  new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
           @Override
           public void processFinished(ArrayList<Question> questionArrayList) {
               questionTextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
               questionCounterTextview.setText(currentQuestionIndex + " / " + questionArrayList.size()); //0 /234

           }
       });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.prev_button:
                if(currentQuestionIndex > 0){
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    UpdateQuestion();
                }
                break;
            case R.id.next_button:
                if(currentQuestionIndex != questionList.size()-1)
                currentQuestionIndex++;
                UpdateQuestion();
                break;
            case R.id.trueButton:
                CheckAnswer(true);
                UpdateQuestion();
                break;
            case R.id.falseButton:
                CheckAnswer(false);
                UpdateQuestion();
                break;
        }
    }

    private void CheckAnswer(boolean userAnswer){
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();

        int toastMessageID = 0;

        if(userAnswer == answerIsTrue){
            fadeView();
            toastMessageID = R.string.correct_answer;

        }else {
            ShakeAnimation();
            toastMessageID = R.string.wrong_answer;
        }

        Toast.makeText(MainActivity.this, toastMessageID,
                Toast.LENGTH_SHORT).show();
    }

    public void UpdateQuestion(){
        questionTextview.setText(questionList.get(currentQuestionIndex).getAnswer());
        questionCounterTextview.setText(currentQuestionIndex + " / " + questionList.size());
    }


    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        //alphaAnimation.setRepeatCount(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
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

        alphaAnimation.setDuration(350);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void ShakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake);

        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
