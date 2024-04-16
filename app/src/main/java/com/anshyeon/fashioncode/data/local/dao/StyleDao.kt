package com.anshyeon.fashioncode.data.local.dao

import androidx.room.*
import com.anshyeon.fashioncode.data.model.LocalStyle
import kotlinx.coroutines.flow.Flow

@Dao
interface StyleDao {

    @Query("SELECT * FROM styles")
    fun getAllStyleList(): Flow<List<LocalStyle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(style: LocalStyle)

    @Query("DELETE FROM styles")
    suspend fun deleteAll()
}