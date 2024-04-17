package com.anshyeon.fashioncode.data.repository

import android.net.Uri
import com.anshyeon.fashioncode.data.PreferenceManager
import com.anshyeon.fashioncode.data.dataSource.ImageDataSource
import com.anshyeon.fashioncode.data.dataSource.UserDataSource
import com.anshyeon.fashioncode.data.local.dao.UserDao
import com.anshyeon.fashioncode.data.model.Follow
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.network.FireBaseApiClient
import com.anshyeon.fashioncode.network.extentions.onError
import com.anshyeon.fashioncode.network.extentions.onException
import com.anshyeon.fashioncode.network.extentions.onSuccess
import com.anshyeon.fashioncode.network.model.ApiResponse
import com.anshyeon.fashioncode.network.model.ApiResultException
import com.anshyeon.fashioncode.network.model.ApiResultSuccess
import com.anshyeon.fashioncode.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val fireBaseApiClient: FireBaseApiClient,
    private val preferenceManager: PreferenceManager,
    private val userDataSource: UserDataSource,
    private val imageDataSource: ImageDataSource,
    private val userDao: UserDao,
) {

    fun getLocalIdToken(): String {
        return preferenceManager.getString(Constants.KEY_GOOGLE_ID_TOKEN, "")
    }

    suspend fun saveUserInfo(nickname: String, url: String?) {
        with(preferenceManager) {
            setGoogleIdToken(
                Constants.KEY_GOOGLE_ID_TOKEN,
                userDataSource.getIdToken()
            )
            setUserNickName(
                Constants.KEY_USER_NICKNAME,
                nickname
            )
            url?.let {
                setUserImage(
                    Constants.KEY_USER_PROFILE_URL,
                    it
                )
            }
        }
    }

    suspend fun getUser(): ApiResponse<Map<String, User>> {
        return try {
            fireBaseApiClient.getUser(
                userDataSource.getIdToken(),
                "\"${userDataSource.getUserId()}\""
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getUserInfo(
        userId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<User> = flow {
        try {
            val response = fireBaseApiClient.getUser(
                userDataSource.getIdToken(),
                "\"${userId}\""
            )
            response.onSuccess { data ->
                if (data.values.isNotEmpty()) {
                    val user = data.values.first()
                    emit(
                        user.copy(
                            key = data.keys.first(),
                        )
                    )
                }
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    suspend fun getUserList(): ApiResponse<Unit> {
        return try {
            val response = fireBaseApiClient.getUserList(
                userDataSource.getIdToken()
            )
            response.onSuccess { data ->
                insertUserList(data.map { entry ->
                    entry.value.run {
                        copy(
                            key = entry.key,
                        )
                    }
                })
            }.onError { _, _ ->
                throw Exception()
            }.onException {
                throw Exception()
            }
            ApiResultSuccess(Unit)
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getStyleListByRoom(): Flow<List<User>> {
        return userDao.getAllUserList()
    }

    private suspend fun insertUserList(users: List<User>) {
        userDao.insertAll(users)
    }

    private suspend fun updateUser(nickname: String, profileUrl: String?) {
        userDao.update(getUserId(), nickname, profileUrl)
    }

    suspend fun createUser(
        nickname: String,
        uri: Uri?
    ): ApiResponse<Unit> {
        return try {
            val imageUrl =
                uri?.let { imageDataSource.downloadImage(imageDataSource.uploadImage(it)) }
            val userId = userDataSource.getUserId()
            val user = User(
                userId = userId,
                nickName = nickname,
                profileUrl = imageUrl,
            )
            preferenceManager.setUserImage(Constants.KEY_USER_PROFILE_URL, imageUrl)
            fireBaseApiClient.createUser(
                userDataSource.getIdToken(),
                user
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    suspend fun updateUser(
        nickname: String,
        url: String?,
        uri: Uri?,
        userKey: String
    ): ApiResponse<Unit> {
        return try {
            val imageUrl =
                uri?.let { imageDataSource.downloadImage(imageDataSource.uploadImage(it)) } ?: url

            val updates = mapOf(
                "nickName" to nickname,
                "profileUrl" to imageUrl
            )
            fireBaseApiClient.updateUser(
                userKey,
                userDataSource.getIdToken(),
                updates
            ).onSuccess {
                preferenceManager.setUserImage(Constants.KEY_USER_PROFILE_URL, imageUrl)
                updateUser(nickname, imageUrl)
            }.onError { code, message ->
                throw Exception()
            }.onException {
                throw Exception()
            }
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    suspend fun createFollow(
        following: String,
    ): ApiResponse<Unit> {
        val myUserId = getUserId()
        val follow = Follow(
            myUserId + following,
            follower = myUserId,
            following = following
        )
        return try {
            fireBaseApiClient.createFollow(
                myUserId + following,
                userDataSource.getIdToken(),
                follow
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    suspend fun deleteFollow(
        following: String
    ): ApiResponse<Unit> {
        return try {
            fireBaseApiClient.deleteFollow(
                getUserId() + following,
                userDataSource.getIdToken(),
            )
        } catch (e: Exception) {
            ApiResultException(e)
        }
    }

    fun getFollowerList(
        userId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<List<Follow>> = flow {
        try {
            val response = fireBaseApiClient.getFollower(
                userDataSource.getIdToken(),
                "\"${userId}\""
            )
            response.onSuccess { data ->
                emit(data.map { entry ->
                    entry.value
                })
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getFollowingList(
        userId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<List<Follow>> = flow {
        try {
            val response = fireBaseApiClient.getFollowing(
                userDataSource.getIdToken(),
                "\"${userId}\""
            )
            response.onSuccess { data ->
                emit(data.map { entry ->
                    entry.value
                })
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getFollowerListWithUserInfo(
        viewModelScope: CoroutineScope,
        userId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<List<Follow>> = flow {
        try {
            val response = fireBaseApiClient.getFollower(
                userDataSource.getIdToken(),
                "\"${userId}\""
            )
            response.onSuccess { data ->
                val followerListWithUserInfo = viewModelScope.async {
                    data.map { entry ->
                        viewModelScope.async {
                            var user: User? = null
                            val getUserResponse = getUserInfo(
                                entry.value.follower,
                                {},
                                {}
                            )
                            getUserResponse.collectLatest { result ->
                                user = result
                            }
                            entry.value.copy(
                                nickName = user?.nickName,
                                profileUrl = user?.profileUrl
                            )
                        }
                    }
                }
                emit(followerListWithUserInfo.await().map { it.await() })
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getFollowingListWithUserInfo(
        viewModelScope: CoroutineScope,
        userId: String,
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<List<Follow>> = flow {
        try {
            val response = fireBaseApiClient.getFollowing(
                userDataSource.getIdToken(),
                "\"${userId}\""
            )
            response.onSuccess { data ->
                val followerListWithUserInfo = viewModelScope.async {
                    data.map { entry ->
                        viewModelScope.async {
                            var user: User? = null
                            val getUserResponse = getUserInfo(
                                entry.value.following,
                                {},
                                {}
                            )
                            getUserResponse.collectLatest { result ->
                                user = result
                            }
                            entry.value.copy(
                                nickName = user?.nickName,
                                profileUrl = user?.profileUrl
                            )
                        }
                    }
                }
                emit(followerListWithUserInfo.await().map { it.await() })
            }.onError { _, _ ->
                onError()
            }.onException {
                onError()
            }
        } catch (e: Exception) {
            onError()
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getUserId(): String = userDataSource.getUserId()
}