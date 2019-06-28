package com.example.room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.room.db.StudentDB
import com.example.room.entity.Student
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var studentDB: StudentDB? = null
    private var studentList = listOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        studentDB = StudentDB.getInstance(this)

        val getAllData = Runnable {
            studentList = studentDB?.studentDao()?.getAll()!!
        }
        Thread(getAllData).start()

        val addData = Runnable {
            val tempStudent = Student()
            val nameText = nameEditText.text.toString()
            val ageText = ageEditText.text.toString()
            val addressText = addressEditText.text.toString()

            tempStudent.name = nameText
            tempStudent.age = ageText.toInt()
            tempStudent.address = addressText
            studentDB!!.studentDao().insert(tempStudent)
            studentList = studentDB?.studentDao()?.getAll()!!
        }

        addButton.setOnClickListener { Thread(addData).start() }

        notifyButton.setOnClickListener {
            var text = ""

            for(tempStudent in studentList) text += tempStudent.toString()

            dataTextView.text = text
        }
    }

    override fun onDestroy() {
        StudentDB.destroyInstance()
        super.onDestroy()
    }
}