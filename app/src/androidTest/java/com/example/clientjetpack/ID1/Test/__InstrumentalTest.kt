package com.example.clientjetpack.ID1.Test

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clientjetpack.ID1.Test.Packages.Init.initialTestData
import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels
import com.example.clientjetpack.ID1.Test._A.Tests.Filter.LogFilterRule
import com.example.clientjetpack.ID1.Test._A.Tests._ID1.Test._testID1
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.testID2
import com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.testID_2_B
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class __InstrumentalTest: KoinTest {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val combinedLogFilter = LogFilterRule.filter()
        .filterByTag("InstrumentalTest")
        .filterByTag("onOperationSuccess")
        .filterByTag("OperationTrackerImp")
        .build()

    private val testDispatcher = StandardTestDispatcher()

    private val viewModel: TarificationViewModel by inject()

    private val fireBaseHandler by lazy { viewModel.inputSqlGroupeRepositorys.fireBaseHandler }

    private val parentDbRef: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InputEtInfosSql")

    private val sonDataBaseRef: DatabaseReference = parentDbRef.child("A_Tarification")

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        startKoin {
            modules(
                module {
                    single { this@__InstrumentalTest }
                    viewModel { TarificationViewModel() }
                }
            )
        }

        testDispatcher.scheduler.advanceUntilIdle()

        fireBaseHandler.clearDatabaseAsync(sonDataBaseRef)
        fireBaseHandler.addAllToFireBaseAsync(initialTestData, sonDataBaseRef)

        viewModel.inputSqlGroupeRepositorys.TarificationRepository().loadDataFromFirebase()

        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun testID1_AddLogFrommock() = runTest {
        _testID1()
    }
    @Test
    fun testID2_() = runTest {
        testID2(viewModel)
    }

    @Test fun testID_2_B() = runTest { testID_2_B(viewModel) }


    fun testID9_AddLoadFB() = runTest {
        fireBaseHandler.clearDatabaseAsync(sonDataBaseRef)
        fireBaseHandler.addAllToFireBaseAsync(initialTestData, sonDataBaseRef)

        val result = fireBaseHandler.loadDatasAsync(
            sonDataBaseRef,
            InputEtInfosSqlModels.Tarification::class.java
        )

        assertEquals(
            result,
            initialTestData
        )
    }
}
