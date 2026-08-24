package com.ynotlabs.cathopedia.di

import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.DatabaseDriverFactory
import com.ynotlabs.cathopedia.data.RosarySessionRepository
import com.ynotlabs.cathopedia.db.CathopediaDatabase

/**
 * Root of the (manual, no-framework) dependency graph. One instance per process.
 * Content loading is suspend (it reads a bundled resource), so it isn't done
 * here — see [CathopediaRepository.ensureContentLoaded], called from SplashScreen.
 */
class AppContainer(driverFactory: DatabaseDriverFactory) {
    val database: CathopediaDatabase = CathopediaDatabase(driverFactory.createDriver())
    val repository: CathopediaRepository = CathopediaRepository(database)
    val rosarySessionRepository: RosarySessionRepository = RosarySessionRepository(database)
}
