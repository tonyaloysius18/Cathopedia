package com.ynotlabs.cathopedia.data

import app.cash.sqldelight.db.SqlDriver

/** Platform-specific bundled-SQLite driver, per the Phase 0 offline-first design. */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
