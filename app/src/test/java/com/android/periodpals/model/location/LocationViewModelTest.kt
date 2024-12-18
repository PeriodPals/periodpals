package com.android.periodpals.model.location

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class LocationViewModelTest {
  private lateinit var locationRepository: LocationModel
  private lateinit var locationViewModel: LocationViewModel

  private val testQuery = "EPFL"
  private val mockLocations = listOf(Location(46.5197, 6.5662, "EPFL"))

  private val mockAddressName =
      "1, Avenue de Praz-Rodet, Morges, District de Morges, Vaud, 1110, Switzerland"

  private val mockLat = 46.509858
  private val mockLon = 6.485742

  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    locationRepository = mock(LocationModel::class.java)
    locationViewModel = LocationViewModel(locationRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun setQueryCallsRepository() = runTest {
    locationViewModel.setQuery(testQuery)
    verify(locationRepository).search(eq(testQuery), any(), any())
  }

  @Test
  fun getAddressFromCoordinatesCallsRepository() = runTest {
    locationViewModel.getAddressFromCoordinates(lat = mockLat, lon = mockLon)
    verify(locationRepository)
        .reverseSearch(
            eq(mockLat), eq(mockLon), any<(String) -> Unit>(), any<(Exception) -> Unit>())
  }

  @Test
  fun initialStateIsCorrect() {
    assertThat(locationViewModel.query.value, `is`(""))
    assertThat(locationViewModel.locationSuggestions.value, `is`(emptyList<Location>()))
    assertThat(locationViewModel.address.value, `is`(""))
  }

  @Test
  fun setQueryUpdatesQueryState() = runTest {
    locationViewModel.setQuery(testQuery)
    assertThat(locationViewModel.query.value, `is`(testQuery))
  }

  @Test
  fun searchLocationSuccessUpdatesLocationSuggestions() = runTest {
    // Simulate successful repository call
    doAnswer {
          val successCallback = it.getArgument<(List<Location>) -> Unit>(1)
          successCallback(mockLocations)
        }
        .whenever(locationRepository)
        .search(any(), any(), any())

    locationViewModel.setQuery(testQuery)
    testDispatcher.scheduler.advanceUntilIdle() // Ensure all coroutines complete

    assertThat(locationViewModel.locationSuggestions.value, `is`(mockLocations))
  }

  @Test
  fun searchLocationFailureDoesNotCrash() = runTest {
    // Simulate failure in the repository call
    doAnswer {
          val failureCallback = it.getArgument<(Exception) -> Unit>(2)
          failureCallback(RuntimeException("Network error"))
        }
        .whenever(locationRepository)
        .search(any(), any(), any())

    locationViewModel.setQuery(testQuery)
    testDispatcher.scheduler.advanceUntilIdle() // Ensure all coroutines complete

    assertThat(locationViewModel.locationSuggestions.value, `is`(emptyList<Location>()))
  }

  @Test
  fun reverseSearchSuccessfulUpdatesAddress() = runTest {
    // Simulate successful repository call
    doAnswer {
          val successCallback = it.getArgument<(String) -> Unit>(1)
          successCallback(mockAddressName)
        }
        .whenever(locationRepository)
        .reverseSearch(any(), any(), any(), any())

    locationViewModel.getAddressFromCoordinates(mockLat, mockLon)
    testDispatcher.scheduler.advanceUntilIdle() // Ensure all coroutines complete

    assertThat(locationViewModel.address.value, `is`(mockAddressName))
  }

  @Test
  fun reverseSearchFailureDoesNotCrash() = runTest {
    // Simulate failure in the repository call
    doAnswer {
          val failureCallback = it.getArgument<(Exception) -> Unit>(2)
          failureCallback(RuntimeException("Network error"))
        }
        .whenever(locationRepository)
        .reverseSearch(any(), any(), any(), any())

    locationViewModel.getAddressFromCoordinates(mockLat, mockLon)
    testDispatcher.scheduler.advanceUntilIdle() // Ensure all coroutines complete

    assertThat(locationViewModel.address.value, `is`(""))
  }
}
