package com.oob.carteira_digital.viewmodels

import androidx.lifecycle.ViewModel
import com.oob.carteira_digital.CardFragment
import com.oob.carteira_digital.models.Account
import com.oob.carteira_digital.objects.Preferences
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class VMAccount : ViewModel() {
    private val account = Account()

    suspend fun login(cpf: String, password: String): String {
        return account.login(cpf, password)
    }

    fun offlineLogin(cpf: String, password: String): String {
        return account.offlineLogin(cpf, password)
    }

    fun offlineBiometricLogin(): String {
        return account.offlineLogin(Preferences.getAuthCpf(), Preferences.getAuthPassword())
    }

    suspend fun biometricLogin(): String {
        return account.biometricLogin()
    }

    fun checkLogin(): Boolean {
        return account.checkLogin()
    }

    suspend fun forgotPassword(email: String): String {
        return account.forgotPassword(email)
    }

    suspend fun resetPassword(newPassword: String, confirmNewPassword: String): String {
        if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            return "Preencha todos os campos"
        }

        if (newPassword != confirmNewPassword) {
            return "As senhas não coincidem"
        }

        return account.resetPassword(newPassword, confirmNewPassword)
    }

    suspend fun getInfo(): List<String> {
        val info = account.getAccountInfo()
        return info
    }

    fun getCourses(coursesRaw: String): List<CardFragment.Course> {
        return account.getCourses(coursesRaw)
    }

    fun isValidCourse(coursesRaw: String): Boolean {
        var valid = false
        val courses = getCourses(coursesRaw)

        if (courses.isNotEmpty()) {
            for (course in courses) {
                val customDayNames = mapOf(
                    DayOfWeek.MONDAY to "Segunda",
                    DayOfWeek.TUESDAY to "Terça",
                    DayOfWeek.WEDNESDAY to "Quarta",
                    DayOfWeek.THURSDAY to "Quinta",
                    DayOfWeek.FRIDAY to "Sexta",
                    DayOfWeek.SATURDAY to "Sábado",
                    DayOfWeek.SUNDAY to "Domingo"
                )
                val days = course.days.split(",").map { it.trim() }.toSet()
                val todayDate = LocalDate.now()
                val todayDayRaw = todayDate.dayOfWeek
                val todayDay = customDayNames[todayDayRaw] ?: ""
                val enterTime = LocalTime.parse(course.enter_time)
                val leaveTime = LocalTime.parse(course.leave_time)
                val currentTime = LocalTime.now()
                val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)
                val endDateRaw = course.end_date
                val endDate = LocalDate.parse(endDateRaw, formatter)
                val currentDate = LocalDate.now()

                if (endDate.isAfter(currentDate) && todayDay in days && currentTime.isAfter(
                        enterTime
                    ) && currentTime.isBefore(leaveTime)
                ) {
                    valid = true
                }
            }
        }
        return valid
    }
}