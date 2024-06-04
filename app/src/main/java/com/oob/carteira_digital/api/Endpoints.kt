package com.oob.carteira_digital.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface Endpoints {
    @FormUrlEncoded
    @POST("/api/v1/auth/signin")
    suspend fun login(@FieldMap params: HashMap<String?, String?>): Response<ResponseBody>

    @GET("/api/v1/account/info")
    suspend fun accountInfo(): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/api/v1/notification/get")
    suspend fun getNotifications(@Field("id") id: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/api/v1/password/forgot")
    suspend fun forgotPassword(@Field("email") email: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/api/v1/password/reset")
    suspend fun resetPassword(@FieldMap params: HashMap<String?, String?>): Response<ResponseBody>

}