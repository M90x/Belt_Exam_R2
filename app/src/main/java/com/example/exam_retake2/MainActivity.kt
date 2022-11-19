package com.example.exam_retake2

import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    private lateinit var sharedPref: SharedPreferences

    //Score & high score View elements
    private lateinit var highScoreView: TextView
    private lateinit var scoreView: TextView

    //Score View elements
    private lateinit var equationView: TextView

    //User answer & Submit button elements
    private lateinit var answerField: EditText
    private lateinit var submitBtn: Button

    //Recycler View declaration
    private var rvList: ArrayList<String> = ArrayList()


    private var score = 0
    private var highScore = 0


    // First & Second number of equations
    private var num1 = Random.nextInt( 9)
    private var num2 = Random.nextInt(9)


    private var operator = 1
    private var correctAnswer = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Alert dialog to welcome the user & explain the app
        CustomAlertDialog(this,"Math Study App", "Welcome to the Math Study App! \n " +
                "How many equations can you solve?")


        //Get Shared Preferences to preserve the high score
        sharedPref = getSharedPreferences("highScoreNum", MODE_PRIVATE)
        highScore = sharedPref.getInt("highScoreNum",0)


        //Assign the High Score variables
        highScoreView = findViewById(R.id.highScoreView)
        highScoreView.text = ("High Score: $highScore")

        //Assign the Score variables
        scoreView = findViewById(R.id.scoreView)
        scoreView.text = ("Score: $score")


        //Assign the Recycler View array
        rvList = ArrayList()
        rvMain.adapter = RecyclerViewAdapter(rvList)
        rvMain.layoutManager = LinearLayoutManager(this)


        //Assign the User answer & Submit button variables
        answerField = findViewById(R.id.answerField)
        submitBtn = findViewById(R.id.submitBtn)


        //Assign the equation view element
        equationView = findViewById(R.id.equationView)

        //Call function to get the equation
        equations()


        //Submit Button  -----------------------------------------------------------------------
        submitBtn.setOnClickListener{

            answerEqu()
            sharedPref.edit().apply{
                putInt("highScoreNum", highScore)
                apply()
            }
        }


    }

    //----------------------------------------Functions----------------------------------------


    //Answer equations ------------------------------------------------------------------------
    private fun answerEqu(){

        val answerNum = answerField.text.toString()

        if(answerNum.isNotEmpty()){

            if(correctAnswer == answerNum.toInt()) {

                rvList.add("${equationView.text} $answerNum")

                score++

                highScoreView.text = ("High Score: $highScore")
                scoreView.text = ("Score: $score")


                //Get Automatically scroll the bottom of the Recycler View
                autoScroll()

                answerField.text.clear()
                answerField.clearFocus()
                rvMain.adapter?.notifyDataSetChanged()

                //Generate another equation
                equations()

            }else{

                rvList.add("The correct answer was $correctAnswer")

                highScore += score
                highScoreView.text = ("High Score: $highScore")

                alertDialogAction("Math Study App", "Play again?")

                answerField.text.clear()
                answerField.clearFocus()

            }
        }else{

            Toast.makeText(this, "Please enter the answer!", Toast.LENGTH_LONG).show()
        }
    }


    //Equations function --------------------------------------------------------------------
    //Equations levels
    private fun equations() {

        //To get the operator of the equations
        when (operator){

            1 -> {
                correctAnswer = num1 + num2
                equationView.text = ("$num1 + $num2 = ")
            }

            2 -> {
                correctAnswer = num1 - num2
                equationView.text = ("$num1 - $num2 = ")
            }

            3 -> {
                correctAnswer = num1 * num2
                equationView.text = ("$num1 * $num2 = ")
            }
        }

        //To make equations progressively more difficult
        when {

            highScore <= 10 -> {
                num1 = Random.nextInt( 9)
                num2 = Random.nextInt(9)
            }

            highScore <= 20 -> {
                num1 = Random.nextInt( 99)
                num2 = Random.nextInt(99)
            }

            highScore <= 30 -> {
                num1 = Random.nextInt( 999)
                num2 = Random.nextInt(999)
        }
            else -> {
                num1 = Random.nextInt( 9999)
                num2 = Random.nextInt(9999)
            }
        }
    }


    //To disable input elements --------------------------------------------------------------
    private fun disableEntry(){
        submitBtn.isEnabled = false
        submitBtn.isClickable = false
        answerField.isEnabled = false
        answerField.isClickable = false
    }

    //To enable input elements --------------------------------------------------------------
    private fun enableEntry(){
        submitBtn.isEnabled = true
        submitBtn.isClickable = true
        answerField.isEnabled = true
        answerField.isClickable = true
    }


    //Get Automatically scroll the bottom of the Recycler View -------------------------------
    private fun autoScroll(){
        rvMain.smoothScrollToPosition(rvList.size-1)
    }


    //Create dropdown menu with options selected ---------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.resetScore -> {
                rvList.clear()
                score = 0
                highScore = 0
                equations()
                rvMain.adapter?.notifyDataSetChanged()

                highScoreView.text = ("High Score: $highScore")
                scoreView.text = ("Score: $score")

                enableEntry()
                true
            }

            R.id.addView -> {
                operator = 1
                rvList.clear()
                score = 0
                equations()
                rvMain.adapter?.notifyDataSetChanged()

                highScoreView.text = ("High Score: $highScore")
                scoreView.text = ("Score: $score")

                true
            }

            R.id.subView -> {
                operator = 2
                rvList.clear()
                score = 0
                equations()
                rvMain.adapter?.notifyDataSetChanged()

                highScoreView.text = ("High Score: $highScore")
                scoreView.text = ("Score: $score")

                true
            }

            R.id.multiView -> {
                operator = 3
                rvList.clear()
                score = 0
                equations()
                rvMain.adapter?.notifyDataSetChanged()

                highScoreView.text = ("High Score: $highScore")
                scoreView.text = ("Score: $score")

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    //Alert dialog for ask user --------------------------------------------------------------
    private fun alertDialogAction (title: String, text: String) {

        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage(text)
            // if the dialog is cancelable
            .setCancelable(false)

            // positive button text and action
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    dialog, id -> this.recreate()

                rvList.clear()

            })

            // negative button text and action
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()

                rvList.add("Game Over!!")
                disableEntry()

            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(title)
        // show alert dialog
        alert.show()
    }

}