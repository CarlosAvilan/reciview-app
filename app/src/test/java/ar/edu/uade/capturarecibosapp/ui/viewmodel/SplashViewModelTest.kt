package ar.edu.uade.capturarecibosapp.ui.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun SplashViewModelTest_NavigateToNext_NoEmiteAntesDeDosSegundos() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        val results = mutableListOf<Unit>()

        val job = launch { viewModel.navigateToNext.collect { results.add(it) } }

        advanceTimeBy(1999)

        assertTrue(results.isEmpty())
        job.cancel()
    }

    @Test
    fun SplashViewModelTest_NavigateToNext_EmiteUnaVezTrasDosSegundos() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        val results = mutableListOf<Unit>()

        val job = launch { viewModel.navigateToNext.collect { results.add(it) } }

        advanceTimeBy(2001)

        assertEquals(1, results.size)
        job.cancel()
    }

    @Test
    fun SplashViewModelTest_NavigateToNext_NoEmiteDosVeces() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        val results = mutableListOf<Unit>()

        val job = launch { viewModel.navigateToNext.collect { results.add(it) } }

        advanceTimeBy(5000)

        assertEquals(1, results.size)
        job.cancel()
    }
}