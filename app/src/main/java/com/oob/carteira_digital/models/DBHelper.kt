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
        end_date TIMESTAMP NOT NULL,
        courses VARCHAR(20),
        level VARCHAR(50) NOT NULL,
        FOREIGN KEY (account_id) REFERENCES account(id)
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

    private fun selectQuery(sql: String): Map<String, String> {
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

    private fun insertQuery(table: String, values: ContentValues): Long {
        val db = this.writableDatabase
        val res = db.insert(table, null, values)
        db.close()
        return res
    }

    private fun deleteQuery(table: String, id: String) {
        val db = this.writableDatabase
        db.delete(table, "id=?", arrayOf(id))
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
            val COLUMNS = arrayOf(
                "id",
                "end_date",
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
                            COLUMNS[i], params[i]
                        )
                        studentValues.put("account_id", params[i])
                    }

                    in 1..3 -> studentValues.put(COLUMNS[i], params[i])
                    in 4..15 -> accountValues.put(COLUMNS[i], params[i])
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

    fun getAccount(): Map<String, String> {
        val id = Preferences.getAccountId()

        var account: Map<String, String>
        val isStudent = selectQuery("SELECT is_student FROM account WHERE id = $id;")

        if (isStudent["is_student"] == "1") {
            account =
                selectQuery("SELECT * FROM account a JOIN student s ON a.id = s.account_id WHERE a.id = $id;").toMutableMap()

            account["birth_date"] = formatDate(account["birth_date"].toString())
            return account
        } else {
            account = selectQuery("SELECT * FROM account WHERE id = $id;").toMutableMap()
            account["birth_date"] = formatDate(account["birth_date"].toString())
        }

        return account
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

}