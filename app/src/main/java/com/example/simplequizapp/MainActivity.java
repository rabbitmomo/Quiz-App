package com.example.simplequizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    List<Integer> randomQuestionIndices;
    int score=0;
    int totalQuestion=QuestionAnswer.question.length;
    int currentQuestionIndex=0;
    int seconds = 0;
    int minutes ;
    int remainingSeconds ;

    String selectedAnswer="";

    TextView totalQuestionTextView;
    TextView questionTextView;

    Button ansA;
    Button ansB;
    Button ansC;
    Button ansD;
    Button submitBtn;

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
    }//method runned when program starts

    public void onButtonClick(View view) {
        setContentView(R.layout.activity_main);
        totalQuestionTextView = findViewById(R.id.totalQuestion);//set up textview
        questionTextView = findViewById(R.id.question);//textview
        ansA = findViewById(R.id.ans_A);//set up button
        ansB = findViewById(R.id.ans_B);//button
        ansC = findViewById(R.id.ans_C);//button
        ansD = findViewById(R.id.ans_D);//button
        submitBtn = findViewById(R.id.submit_btn);//button

        ansA.setOnClickListener(this);// Set listener to button when user clicking
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);//Find View by id and set On click listener to the UI Components

        totalQuestionTextView.setText("Markah: "+String.format("%.2f", ((float)score/5)*100) +
                        "%");//set a new text to a text view

        randomQuestionIndices = new ArrayList<>();//create a new array list of any number of question
        SecureRandom random = new SecureRandom();//create a instance called "random" for subclass SecureRandom of Java
        while (randomQuestionIndices.size() < 5) {
            int index = random.nextInt(totalQuestion);
            // generate a number between 0 (inclusve) and 16 (exclusive),
            // total question is 16 for [0] to [15]
            if (!randomQuestionIndices.contains(index)) {//When array list does not contain the random number genrated
                randomQuestionIndices.add(index);//the random number is added to array list
            }
        }  // Generate a list of 5 random question

        loadNewQuestion();
        final TextView timerTextView = (TextView) findViewById(R.id.timerTextView);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                minutes = seconds / 60;
                remainingSeconds = seconds % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, remainingSeconds));
                seconds++;
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);//Set a timer which run in minute and seconds and set textview to timer in string format

        ImageButton stopButton = findViewById(R.id.stop_button);
        ImageButton startButton = findViewById(R.id.start_button);
        ImageView stopping_screen = findViewById(R.id.stopping_symbol);
        stopping_screen.setVisibility(View.GONE);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                findViewById(android.R.id.content).setEnabled(false);

                questionTextView.setVisibility(View.GONE);
                submitBtn.setVisibility(View.GONE);

                stopping_screen.setVisibility(View.VISIBLE);

                ansA.setVisibility(View.GONE);
                ansB.setVisibility(View.GONE);
                ansC.setVisibility(View.GONE);
                ansD.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
            }//method run when user clicks stop
        });

        startButton.setVisibility(View.GONE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.post(runnable);
                findViewById(android.R.id.content).setEnabled(true);

                questionTextView.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);

                stopping_screen.setVisibility(View.GONE);
                ansA.setVisibility(View.VISIBLE);
                ansB.setVisibility(View.VISIBLE);
                ansC.setVisibility(View.VISIBLE);
                ansD.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            }//method run when clicking start from stopping screen
        });


    }//method used when user clicks start button in start screen and set up all the UI Components

    @Override
    public void onClick(View view) {
        Button clickedButton = (Button) view;   // Set clicked button type to all button, 'view' in return meaning be clicked

        if (clickedButton.getId() == R.id.submit_btn) {  // Check if user click the submit button
            if (!selectedAnswer.isEmpty()) {
                // Check if the SelectedAnswer is not empty.
                // (SelectedAnswer is set as empty at start)
                // Whether user direct click submit button before choosing answer or click submit after select answer
                int questionIndex = randomQuestionIndices.get(currentQuestionIndex);
                //questionIndex is assigned to the first element in array list(not array but array list)
                //for example , if 12 is the first element in randomQuestionIndices, it is catched to question index.
                if (selectedAnswer.equals(QuestionAnswer.correctAnswer[questionIndex])) {
                    //now check same place in correctAnswer for Question in other class
                    // whther is it equal to the selectedAnswer's text
                    score++;  //check if the answer is true
                }
                if (currentQuestionIndex >= randomQuestionIndices.size() - 1) {
                    finishQuiz(); //check if the current question is last question
                } else {
                    currentQuestionIndex++;
                    loadNewQuestion(); //proceed to next question if not last question
                }
            }
        } else { //check if user click any button except for submit button
            selectedAnswer = clickedButton.getText().toString(); // get the text to SelectedAnswer and to String
            ansA.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            ansB.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            ansC.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            ansD.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            clickedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green));  //set color to true one
        }

        totalQuestionTextView.setText("Markah: "+String.format("%.2f", ((float)score/5)*100) +
                "%");//set a new text to a text view
    }//method run when user selects an answer and clicks submit button

    void loadNewQuestion() {
        selectedAnswer = ""; // set selectedanswer to empty for every new question

        if (currentQuestionIndex >= randomQuestionIndices.size()) {
            finishQuiz();
            return;
        }  // double check if the question is the last question ? have to adjust

        int questionIndex = randomQuestionIndices.get(currentQuestionIndex);//random index of the question is generated

        ansA.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        ansB.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        ansC.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        ansD.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));

        questionTextView.setText(QuestionAnswer.question[questionIndex]);
        ansA.setText(QuestionAnswer.choice[questionIndex][0]);
        ansB.setText(QuestionAnswer.choice[questionIndex][1]);
        ansC.setText(QuestionAnswer.choice[questionIndex][2]);
        ansD.setText(QuestionAnswer.choice[questionIndex][3]);
        //Set new question and answer choices

    }//method used when user submits a answer

    void finishQuiz(){
        String results="";
        if(score>=5*0.60){
            results="Tahniah! Anda menjawab majoriti soalan dengan betul";
        }
        else{
            results="Anda boleh mencuba lagi";
        }

        new AlertDialog.Builder(this)
                .setTitle(results)
                .setMessage("Markah anda adalah "+
                        String.format("%.2f", ((float)score/5)*100) +
                        "% dalam \n"+minutes+" minit dan "+remainingSeconds+" saat.")
                .setPositiveButton("Restart",(dialogueInterface,i)->restartQuiz())
                .show(); // Give alert to show result and show restarting button in dialog

    }//method used when user finished

    void restartQuiz(){
            handler.removeCallbacks(runnable); // remove time counter for every seconds for one real second
            score=0; //restart score to zero
            currentQuestionIndex=0; //restart question index to zero
            seconds=0; // restart time to zero
            setContentView(R.layout.start_screen); // back to start screen
    } //method used when user restart app

}