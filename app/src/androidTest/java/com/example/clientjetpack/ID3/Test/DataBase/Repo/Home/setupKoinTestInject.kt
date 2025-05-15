package com.example.clientjetpack.ID3.Test.DataBase.Repo.Home

import androidx.test.platform.app.InstrumentationRegistry
import com.example.clientjetpack.ID3.Test.DataBase.Repo.InfosSqlDataBasesRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

fun setupKoinTestInject(testScheduler: TestCoroutineScheduler? = null) {
    stopKoin()

    val testDispatcher = testScheduler?.let { StandardTestDispatcher(it) }

    startKoin {
        modules(
            module {
                single {
                    TestAppDatabase.getTestDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
                }

                single {
                    InfosSqlDataBasesRepository(
                        get(),
                        get(),
                        testDispatcher ?: kotlinx.coroutines.Dispatchers.IO,
                    )
                }
                single { FireBaseHandler() }
            })
    }
}
