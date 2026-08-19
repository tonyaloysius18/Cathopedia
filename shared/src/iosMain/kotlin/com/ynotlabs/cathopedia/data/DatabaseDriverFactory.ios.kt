package com.ynotlabs.cathopedia.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ynotlabs.cathopedia.db.CathopediaDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(CathopediaDatabase.Schema, "cathopedia.db")
}
