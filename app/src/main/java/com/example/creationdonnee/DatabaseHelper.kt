package com.example.creationdonnee

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 4

        // Table users
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MAIL = "mail"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_DATE_OF_BIRTH = "date_of_birth"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_HOBBIES = "hobbies" // Stocké en JSON

        // Table planning
        private const val TABLE_PLANNING = "planning"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_PLANNING = "planning"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableUsers = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MAIL TEXT UNIQUE,
                $COLUMN_NAME TEXT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_PHONE TEXT,
                $COLUMN_DATE_OF_BIRTH TEXT,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_HOBBIES TEXT
            )
        """.trimIndent()

        db.execSQL(createTableUsers)

        val createTablePlanning = """
            CREATE TABLE IF NOT EXISTS $TABLE_PLANNING (
                $COLUMN_USER_ID INTEGER,
                $COLUMN_PLANNING TEXT,
                $COLUMN_DATE TEXT,
                FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """.trimIndent()

        db.execSQL(createTablePlanning)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLANNING")
        onCreate(db)

    }

    fun addUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(user.dateOfBirth)

        val hobbiesJson = Gson().toJson(user.hobbies)

        values.put(COLUMN_MAIL, user.mail)
        values.put(COLUMN_NAME, user.name)
        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_PHONE, user.phoneNumber)
        values.put(COLUMN_DATE_OF_BIRTH, dateString)
        values.put(COLUMN_HOBBIES, hobbiesJson)
        values.put(COLUMN_PASSWORD, user.password)

        val success = db.insert(TABLE_USERS, null, values)
        db.close()
        return success != -1L
    }

    fun checkIfUserExists(email: String, username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_MAIL = ? OR $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(email, username))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun deleteAllUsers() {
        val db = this.writableDatabase
        val deleteQuery = "DELETE FROM $TABLE_USERS"
        db.execSQL(deleteQuery)
        db.execSQL("DELETE FROM $TABLE_PLANNING")
        db.close()
    }

    @SuppressLint("Range")
    fun getUserByUsernameOrEmail(usernameOrEmail: String): User? {
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? OR $COLUMN_MAIL = ?"
        val selectionArgs = arrayOf(usernameOrEmail, usernameOrEmail)

        val result = db.rawQuery(query, selectionArgs)

        if (result.moveToFirst()) {
            val email = result.getString(result.getColumnIndex(COLUMN_MAIL))
            val nameVal = result.getString(result.getColumnIndex(COLUMN_NAME))
            val username = result.getString(result.getColumnIndex(COLUMN_USERNAME))
            val phoneNumber = result.getString(result.getColumnIndex(COLUMN_PHONE))
            val dateOfBirth = Date(result.getLong(result.getColumnIndex(COLUMN_DATE_OF_BIRTH)))
            val hobbies = result.getString(result.getColumnIndex(COLUMN_HOBBIES)).split(",").toList()
            val password = result.getString(result.getColumnIndex(COLUMN_PASSWORD))

            result.close()
            db.close()

            return User(email, nameVal, username, phoneNumber, dateOfBirth, hobbies, password)
        }

        result.close()
        db.close()
        return null
    }

    fun addFakeUser() {
        // Crée un utilisateur factice
        val email = "email@email.com"
        val username = "testuser"
        val lastName = "Test"
        val phoneNumber = "+1234567890"
        val dateOfBirth = Date()
        val hobbies = listOf("Sports", "Musique")

        val user = User(
            mail = email,
            name = lastName,
            username = username,
            phoneNumber = phoneNumber,
            dateOfBirth = dateOfBirth, // Utiliser la date récupérée
            hobbies = hobbies,
            password = "mdp"
        )
        addUser(user)
    }

    fun savePlanning(userId: Int, planning: Map<String, String>) {
        val db = this.writableDatabase
        val values = ContentValues()

        val gson = Gson()
        val planningJson = gson.toJson(planning)

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val checkQuery = "SELECT * FROM $TABLE_PLANNING WHERE $COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(checkQuery, arrayOf(userId.toString()))

        if (cursor.count > 0) {
            val updateQuery = """
            UPDATE $TABLE_PLANNING 
            SET $COLUMN_PLANNING = ?, $COLUMN_DATE = ?
            WHERE $COLUMN_USER_ID = ?
        """.trimIndent()
            val stmt = db.compileStatement(updateQuery)
            stmt.bindString(1, planningJson)
            stmt.bindString(2, date)
            stmt.bindLong(3, userId.toLong())
            stmt.executeUpdateDelete()
        } else {
            values.put(COLUMN_USER_ID, userId)
            values.put(COLUMN_PLANNING, planningJson)
            values.put(COLUMN_DATE, date)

            db.insert(TABLE_PLANNING, null, values)
        }

        cursor.close()
        db.close()
    }

    @SuppressLint("Range")
    fun getPlanningByUserId(userId: Int): String {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_PLANNING FROM $TABLE_PLANNING WHERE $COLUMN_USER_ID = ?"
        val statement = db.compileStatement(query)

        // Exécuter la requête SQL
        val result = db.rawQuery(query, arrayOf(userId.toString()))

        var planning = "Aucun planning trouvé"
        if (result.moveToFirst()) {
            planning = result.getString(result.getColumnIndex(COLUMN_PLANNING))
        }
        result.close()
        db.close()

        return planning
    }

    @SuppressLint("Range")
    fun getUserIdByUsernameOrEmail(usernameOrEmail: String): Int {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_ID FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? OR $COLUMN_MAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(usernameOrEmail, usernameOrEmail))

        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
        }
        cursor.close()
        db.close()
        return userId
    }
}
