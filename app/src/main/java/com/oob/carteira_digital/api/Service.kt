package com.oob.carteira_digital.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Service {
    private val baseUrl = "https://oob-carteira-digital-website.vercel.app"

    private val client = OkHttpClient.Builder()
        .addInterceptor(AddCookiesInterceptor())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: Endpoints = retrofit.create(Endpoints::class.java)

    fun getService(): Endpoints {
        return service
    }
}