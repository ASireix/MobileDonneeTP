package com.example.creationdonnee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class PlaningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planning)

        val edit08_10 = findViewById<EditText>(R.id.edit_08_10)
        val edit10_12 = findViewById<EditText>(R.id.edit_10_12)
        val edit14_16 = findViewById<EditText>(R.id.edit_14_16)
        val edit16_18 = findViewById<EditText>(R.id.edit_16_18)

        val btnSavePlanning = findViewById<Button>(R.id.btn_save_planning)

        btnSavePlanning.setOnClickListener {
            val planning = mapOf(
                "08h-10h" to edit08_10.text.toString(),
                "10h-12h" to edit10_12.text.toString(),
                "14h-16h" to edit14_16.text.toString(),
                "16h-18h" to edit16_18.text.toString()
            )

            // Sauvegarder le planning dans la base de données
            savePlanningToDatabase(planning)

            val userid = intent.getIntExtra("USER_ID", -1)
            val intent = Intent(this, PlanningSummaryActivity::class.java)
            intent.putExtra("USER_ID", userid)
            startActivity(intent)
        }
    }

    private fun savePlanningToDatabase(planning: Map<String, String>) {
        val dbHelper = DatabaseHelper(this)
        val userId = intent.getIntExtra("USER_ID", -1)
        dbHelper.savePlanning(userId, planning)
    }
}
