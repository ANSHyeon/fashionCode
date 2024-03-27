package com.anshyeon.fashioncode.data.local.dao

import androidx.room.*
import com.anshyeon.fashioncode.data.model.Clothes
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothesDao {

    @Query("SELECT * FROM clothes")
    fun getAllClothesList(): Flow<List<Clothes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatRoom: Clothes)

    @Query("DELETE FROM clothes")
    suspend fun deleteAll()
}