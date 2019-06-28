package com.example.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.room.entity.Student

@Dao
interface StudentDAO {
    @Query("Select * From StudentDB")
    fun getAll() : List<Student>

    @Insert(onConflict = REPLACE)
    fun insert(student : Student)

    @Update
    fun update(student : Student)

    @Delete
    fun delete(student: Student)

    @Query("Delete from StudentDB")
    fun deleteAll()
}