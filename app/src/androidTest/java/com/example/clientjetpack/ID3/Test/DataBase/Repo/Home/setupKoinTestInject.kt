package com.example.clientjetpack.ID3.Test.DataBase.Repo.Home

import androidx.test.platform.app.InstrumentationRegistry
import com.example.clientjetpack.ID3.Test.DataBase.Repo.InfosSqlDataBasesRepository
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

fun setupKoinTestInject() {
    stopKoin()
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
                    )
                }
                single { FireBaseHandler() }
            })
    }
}
