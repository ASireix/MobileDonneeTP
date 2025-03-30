package com.example.creationdonnee

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlanningSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planning_summary)

        val textViewSummary = findViewById<TextView>(R.id.textViewSummary)

        val dbHelper = DatabaseHelper(this)
        val userId = intent.getIntExtra("USER_ID", -1)
        val planningJson = dbHelper.getPlanningByUserId(userId)

        textViewSummary.text = "Voici votre planning :\n\n$planningJson"

        val btnReturn = findViewById<Button>(R.id.btn_return)
        btnReturn.setOnClickListener {
            finish()
        }
    }
}
