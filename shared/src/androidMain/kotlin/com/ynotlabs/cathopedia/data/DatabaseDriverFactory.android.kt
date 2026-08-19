package com.ynotlabs.cathopedia.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        // The platform's system SQLite doesn't reliably ship FTS5 on every OS image;
        // requery's bundled SQLite always has it, so the search index actually works.
        AndroidSqliteDriver(
            schema = CathopediaDatabase.Schema,
            context = context,
            name = "cathopedia.db",
            factory = RequerySQLiteOpenHelperFactory(),
        )
}
