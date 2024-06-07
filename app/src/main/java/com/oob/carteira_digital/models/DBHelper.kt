package com.oob.carteira_digital.models

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.oob.carteira_digital.objects.Preferences

class DBHelper(context: Context) : SQLiteOpenHelper(context, "database.db", null, 1) {

    private val sql = arrayOf(
        """              
    CREATE TABLE IF NOT EXISTS account (
    id INT PRIMARY KEY,
    is_admin BOOLEAN DEFAULT FALSE,
    is_student BOOLEAN DEFAULT FALSE,
    full_name VARCHAR(70) NOT NULL,
    email VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    registration VARCHAR(50) NOT NULL UNIQUE,
    institution VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK(status IN ('active', 'inactive', 'suspended')) DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    picture VARCHAR(255) DEFAULT NULL,
    last_login DATETIME
    );
    
    CREATE INDEX idx_cpf ON account (cpf);
    CREATE INDEX idx_email ON account (email);
    """, """
    CREATE TABLE IF NOT EXISTS student (
        id INT AUTO_INCREMENT PRIMARY KEY,
        account_id INT,
        courses VARCHAR(20),
        level VARCHAR(50) NOT NULL,
        FOREIGN KEY (account_id) REFERENCES account(id)
    );
    """, """
    CREATE TABLE IF NOT EXISTS notification (
        id INT PRIMARY KEY,
        title TEXT NOT NULL,
        content TEXT NOT NULL,
        created_at TIMESTAMP NOT NULL,
        read BOOLEAN DEFAULT FALSE
    );
    """
    )


    override fun onCreate(db: SQLiteDatabase) {
        defineTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    private fun defineTables(db: SQLiteDatabase) {
        sql.forEach { db.execSQL(it) }
    }

    private fun selectMapQuery(sql: String): Map<String, String> {
        try {
            val db = this.readableDatabase
            val c = db.rawQuery(sql, null)

            val results = mutableListOf<Map<String, String>>()
            val columnNames = c.columnNames
            while (c.moveToNext()) {
                val rowData = mutableMapOf<String, String>()
                for (columnName in columnNames) {
                    val columnIndex = c.getColumnIndex(columnName)
                    val value = if (!c.isNull(columnIndex)) {
                        c.getString(columnIndex)
                    } else {
                        "null"
                    }
                    rowData[columnName] = value
                }
                results.add(rowData)
            }

            c.close()
            db.close()
            return results[0]
        } catch (e: Exception) {
            Log.e("DB", e.message.toString())
            return emptyMap()
        }
    }

    private fun selectListQuery(sql: String): List<Map<String, String>> {
        val results = mutableListOf<Map<String, String>>()
        try {
            val db = this.readableDatabase
            val c = db.rawQuery(sql, null)

            val columnNames = c.columnNames
            while (c.moveToNext()) {
                val rowData = mutableMapOf<String, String>()
                for (columnName in columnNames) {
                    val columnIndex = c.getColumnIndex(columnName)
                    val value = if (!c.isNull(columnIndex)) {
                        c.getString(columnIndex)
                    } else {
                        "null"
                    }
                    rowData[columnName] = value
                }
                results.add(rowData)
            }

            c.close()
            db.close()
        } catch (e: Exception) {
            Log.e("DB", e.message.toString())
        }
        return results
    }


    private fun insertQuery(table: String, values: ContentValues): Long {
        val db = this.writableDatabase
        val res = db.insert(table, null, values)
        db.close()
        return res
    }

    private fun updateQuery(sql: String) {
        val db = this.writableDatabase
        db.execSQL(sql)
        db.close()
    }

    private fun dropQuery(table: String) {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $table")
        defineTables(db)
        db.close()
    }

    fun saveAccount(params: List<String>) {
        try {
            val accountValues = ContentValues()
            val studentValues = ContentValues()
            val columns = arrayOf(
                "id",
                "courses",
                "level",
                "is_admin",
                "is_student",
                "full_name",
                "email",
                "birth_date",
                "cpf",
                "registration",
                "institution",
                "status",
                "created_at",
                "picture",
                "last_login"
            )

            for (i in params.indices) {
                when (i) {
                    0 -> {
                        accountValues.put(
                            columns[i], params[i]
                        )
                        studentValues.put("account_id", params[i])
                    }

                    in 1..2 -> studentValues.put(columns[i], params[i])
                    in 3..14 -> accountValues.put(columns[i], params[i])
                }
            }

            dropQuery("account")
            dropQuery("student")
            insertQuery("account", accountValues)
            Preferences.setAccountId(params[0])
            if (accountValues.getAsBoolean("is_student")) {
                insertQuery("student", studentValues)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun insertNotifications(params: List<String>) {
        try {
            val columns = arrayOf("id", "title", "content", "created_at")
            val notifications = processNotifications(params)

            for (i in notifications.indices) {
                val values = ContentValues()
                for (j in notifications[i].indices) {
                    values.put(columns[j], notifications[i][j])
                }
                insertQuery("notification", values)
            }
        } catch (e: Exception) {
            Log.e("NOTIFICATION", e.message.toString())
        }
    }

    fun getAccount(): Map<String, String> {
        val id = Preferences.getAccountId()

        var account: Map<String, String>
        val isStudent = selectMapQuery("SELECT is_student FROM account WHERE id = $id;")

        if (isStudent["is_student"] == "1") {
            account =
                selectMapQuery("SELECT * FROM account a JOIN student s ON a.id = s.account_id WHERE a.id = $id;").toMutableMap()

            account["birth_date"] = formatDate(account["birth_date"].toString())
            return account
        } else {
            account = selectMapQuery("SELECT * FROM account WHERE id = $id;").toMutableMap()
            account["birth_date"] = formatDate(account["birth_date"].toString())
        }

        return account
    }

    fun getNotifications(): List<Map<String, String>> {
        val notifications = selectListQuery("SELECT * FROM notification ORDER BY created_at DESC;")
        return notifications
    }

    fun getUnreadNotifications(): List<Map<String, String>> {
        val notifications = selectListQuery("SELECT * FROM notification WHERE read = 0 ORDER BY created_at DESC;")
        return notifications
    }

    fun getCourses(): String {
        val account = getAccount()
        val courses = account["courses"]
        return courses.toString()
    }

    fun markAllNotificationsAsRead() {
        updateQuery("UPDATE notification SET read = 1;")
    }

    fun updateNotificationReadStatus(id: String) {
        updateQuery("UPDATE notification SET read = 1 WHERE id = $id;")
    }

    fun getRegistration(): String {
        val info = getAccount()
        val registration = info["registration"]
        return registration.toString()
    }

    // PRIVATE FUNCTIONS

    private fun formatDate(input: String): String {
        val first10 = input.take(10)

        val reversed = first10.split("-").reversed()
        val formatted = reversed.joinToString("-")

        return formatted.replace("-", "/")
    }

    private fun processNotifications(params: List<String>): List<List<String>> {
        val notifications = mutableListOf<List<String>>()
        val notificationSize = 4

        for (i in params.indices step notificationSize) {
            val notification = params.subList(i, (i + notificationSize).coerceAtMost(params.size))
            notifications.add(notification)
        }

        return notifications
    }

}