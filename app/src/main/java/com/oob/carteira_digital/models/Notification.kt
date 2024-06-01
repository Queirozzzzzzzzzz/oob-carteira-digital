package com.oob.carteira_digital.models

import android.util.Log
import com.google.gson.Gson
import com.oob.carteira_digital.api.Service
import com.oob.carteira_digital.objects.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Notification {

    private val service = Service().getService()
    private val gson = Gson()
    suspend fun getNotifications(): List<String> {
        val response = withContext(Dispatchers.IO) {
            service.getNotifications(Preferences.getAccountId())
        }

        return if (response.isSuccessful) {
           try {
               val result = gson.fromJson(response.body()?.string(), Any::class.java) as List<Map<*, *>>
               var stringList = result.map { it.values.map { it.toString() } }.flatten()
               stringList = stringList.map {
                   if (it.endsWith(".0")) {
                       it.substring(0, it.length - 2)
                   } else {
                       it
                   }
               }
               stringList
           }  catch (e: Exception) {
               Log.e("Notification", e.message.toString())
               emptyList()
           }
        } else {
            emptyList()
        }
    }
}