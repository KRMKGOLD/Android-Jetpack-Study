package com.example.room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.room.db.StudentDB
import com.example.room.entity.Student

class MainActivity : AppCompatActivity() {

    private var studentDB : StudentDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        studentDB = StudentDB.getInstance(this)

        val insert = Runnable {
            studentDB?.studentDao()?.insert(Student(0, "A", 10, "A's Address"))
        }
    }
}
