package com.example.clientjetpack.ID3.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.clientjetpack.ID3.Test.DataBase.FireBaseHandler
import com.example.clientjetpack.ID3.Test.DataBase.Repo._InfosSqlDataBases_GroupeRepositorys
import com.example.clientjetpack.ID3.Test.DataBase.Repo._InfosSqlDataBases_GroupeRepositorysImp
import com.example.clientjetpack.ID3.Test.DataBase.TestAppDatabase
import com.example.clientjetpack.Modules.LogFilterRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class __ID3InstrumentalTest : KoinTest {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess")
        .filterByTag("OperationTrackerImp")
        .build()

    private val testDispatcher = StandardTestDispatcher()

    // Use the implementation class instead of the interface
    private val repositoriesImpl: _InfosSqlDataBases_GroupeRepositorysImp by inject()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        startKoin {
            modules(
                module {
                    // Fixed: Use the companion object method directly
                    single {
                        TestAppDatabase.getTestDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
                    }

                    single<_InfosSqlDataBases_GroupeRepositorys> {
                        _InfosSqlDataBases_GroupeRepositorysImp(
                            InstrumentationRegistry.getInstrumentation().targetContext,
                            get()
                        )
                    }

                    single {
                        _InfosSqlDataBases_GroupeRepositorysImp(
                            InstrumentationRegistry.getInstrumentation().targetContext,
                            get()
                        )
                    }

                    single { this@__ID3InstrumentalTest }
                    single { FireBaseHandler() }
                }
            )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun idTest2Num1() = runTest {

    }
}
