package com.example.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StudentDB")
class Student(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "age") var age: Int?,
    @ColumnInfo(name = "address") var address: String?
) {
    constructor() : this(null, "", 0, "")

    override fun toString(): String = "Student(id = $id, name = $name, age = $age, address = $address)\n"
}