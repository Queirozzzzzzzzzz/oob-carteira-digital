package com.oob.carteira_digital.api

import com.oob.carteira_digital.objects.Preferences
import okhttp3.Interceptor
import okhttp3.Response


class AddCookiesInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val cookies = Preferences.getAuthCookie()
        if (cookies != null) {
            proceed(
                request()
                    .newBuilder()
                    .addHeader("Cookie", cookies.toString())
                    .build()
            )
        } else {
            proceed(request())
        }
    }

}
