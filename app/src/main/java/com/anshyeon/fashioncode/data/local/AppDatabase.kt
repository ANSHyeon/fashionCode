package com.anshyeon.fashioncode.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anshyeon.fashioncode.data.local.dao.ClothesDao
import com.anshyeon.fashioncode.data.local.dao.StyleDao
import com.anshyeon.fashioncode.data.local.dao.UserDao
import com.anshyeon.fashioncode.data.model.Clothes
import com.anshyeon.fashioncode.data.model.Converters
import com.anshyeon.fashioncode.data.model.LocalStyle
import com.anshyeon.fashioncode.data.model.User

@Database(entities = [Clothes::class, LocalStyle::class, User::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clothesDao(): ClothesDao
    abstract fun stylesDao(): StyleDao
    abstract fun userDao(): UserDao
}