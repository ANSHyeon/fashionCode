package com.anshyeon.fashioncode.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anshyeon.fashioncode.data.local.dao.ClothesDao
import com.anshyeon.fashioncode.data.model.Clothes

@Database(entities = [Clothes::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clothesDao(): ClothesDao
}