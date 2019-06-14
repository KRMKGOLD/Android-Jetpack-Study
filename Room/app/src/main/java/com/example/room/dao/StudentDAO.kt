package com.example.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.room.entity.Student

interface StudentDAO {
    @Query("Select * From Student")
    fun getAll() : List<Student>

    @Insert(onConflict = REPLACE)
    fun insert(student : Student)

    @Update
    fun update(student : Student)

    @Delete
    fun delete(student: Student)

    @Query("Delete from student")
    fun deleteAll()
}