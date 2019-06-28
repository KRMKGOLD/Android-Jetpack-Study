package com.example.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.room.dao.StudentDAO
import com.example.room.entity.Student

@Database(entities = [Student::class], version = 1)
abstract class StudentDB : RoomDatabase() {
    abstract fun studentDao() : StudentDAO

    companion object {
        private var INSTANCE : StudentDB? = null

        fun getInstance(context : Context) : StudentDB? {
            if(INSTANCE == null) {
                synchronized(StudentDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        StudentDB::class.java, "StudentDB.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}