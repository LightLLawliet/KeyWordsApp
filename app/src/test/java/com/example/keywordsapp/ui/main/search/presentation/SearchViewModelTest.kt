package com.example.keywordsapp.ui.main.search.presentation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.*
import org.junit.Test

class SearchViewModelTest {
    private val interactor = FakeInteractor()
    private val communication = FakeCommunication()
    private val dispatchers = FakeDispatchers()
    private val validation = FakeValidation()
    private val resources = FakeResources()
    private val viewModel =
        lSearchViewModel(
            interactor = interactor,
            communication = communication,
            dispatchers = dispatchers,
            validation = validation,
            resources = resources
        )

    @Test
    fun `search invalid query`() = runBlocking {
        validation.isValid = false

        viewModel.advices(query = "a")

        assertEquals(0, interactor.queryList.size)
        assertEquals(0, interactor.randomAdviceCallCount)
        assertEquals(0, dispatchers.ioCallCount)
        assertEquals("a", validation.isValidCallList[0])
        assertEquals(1, validation.isValidCallList.size)
        assertEquals(0, validation.mapCallList.size)
        assertEquals(SearchUiState.Error(message = "invalid"), communication.stateList[0])
        assertEquals(1, communication.stateList.size)
    }

    @Test
    fun `search valid query result error`() = runBlocking {
        validation.isValid = true
        validation.mapResult = "b"
        interactor.searchAdviceResultByQuery =
            SearchAdviceResult.Error(exception = ServiceException())
        viewModel.advices(query = "a")
        assertEquals(1, validation.isValidCallList.size)
        assertEquals("a", validation.isValidCallList[0])
        assertEquals(1, validation.mapCallList.size)
        assertEquals(SearchUiState.Progress, communication.stateList[0])
        assertEquals(1, dispatchers.ioCallCount)
        assertEquals("b", interactor.queryList[0])
        assertEquals(1, interactor.queryList.size)
        assertEquals(0, interactor.randomAdviceCallCount)
        assertEquals(SearchUiState.Error(message = "service"), communication.stateList[1])
    }
}

private class FakeInteractor : SearchInteractor {
    var queryList = ArrayList<String>()
    var searchAdviceResultByQuery: SearchAdviceResult? = null
    override suspend fun advices(query: String): SearchAdviceResult {
        queryList.add(query)
        return searchAdviceResultByQuery
    }

    var randomAdviceCallCount = 0
    var searchAdviceRandomResult: SearchAdviceResult? = null
    override suspend fun randomAdvice(): SearchAdviceResult {
        randomAdviceCallCount++
        return searchAdviceRandomResult
    }
}

private class FakeCommunication : SearchCommunication {
    val stateList = ArrayList<SearchUiState>()
    override fun map(data: SearchUiState) {
        stateList.add(data)
    }
}

private class FakeDispatchers : DispatchersList {
    private val testDispatcher = TestCoroutineDispatcher()
    var ioCallCount = 0
    override fun io(): CoroutineDispatcher {
        ioCallCount++
    }

    var uiCallCount = 0
    override fun ui(): CoroutineDispatcher {
        uiCallCount++
    }
}

private class FakeValidation() : Validation {
    val isValidCallList = ArrayList<String>()
    var isValid: Boolean? = null
    override fun isValid(query: String): Boolean {
        isValidCallList.add(query)
        return isValid!!
    }

    val mapCallList = ArrayList<String>()
    var mapResult: String? = null
    override fun map(data: String): String {
        return mapResult!!
    }
}


private class FakeResources() : ProvideRecources {
    override fun string(id: Int): String {
        return when (id) {
            R.stringl.invalid_input_message -> {
                return "invalid"
            }
            R.stringl.service_error_message
            else -> throw java.lang.IllegalStateException()
        }
    }
}


