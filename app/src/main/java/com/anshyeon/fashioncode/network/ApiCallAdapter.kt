package com.anshyeon.fashioncode.network

import com.anshyeon.fashioncode.network.model.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ApiCallAdapter(
    private val type: Type
) : CallAdapter<Type, Call<ApiResponse<Type>>> {
    override fun responseType(): Type = type
    override fun adapt(call: Call<Type>): Call<ApiResponse<Type>> {
        return ApiCall(call)
    }
}