package com.anshyeon.fashioncode.data.local.dao

import androidx.room.*
import com.anshyeon.fashioncode.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUserList(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("UPDATE users SET nick_name = :nickName, profile_url = :profileUrl WHERE userId = :userId")
    suspend fun update(userId: String, nickName: String, profileUrl: String?)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}