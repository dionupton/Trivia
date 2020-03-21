package com.udemy.trivia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.udemy.trivia.data.AnswerListAsyncResponse;
import com.udemy.trivia.data.QuestionBank;
import com.udemy.trivia.model.Question;

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

           }
       });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.prev_button:
                break;
            case R.id.next_button:
                if(currentQuestionIndex != questionList.size()-1)
                currentQuestionIndex++;
                UpdateQuestion();
                break;
            case R.id.trueButton:
                break;
            case R.id.falseButton:
                break;
        }
    }

    public void UpdateQuestion(){
        questionTextview.setText(questionList.get(currentQuestionIndex).getAnswer());
    }
}
