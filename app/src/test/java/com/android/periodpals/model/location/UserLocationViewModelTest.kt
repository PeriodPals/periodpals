package com.android.periodpals.model.location

import com.android.periodpals.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class UserLocationViewModelTest {

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  private lateinit var userLocationModel: UserLocationModel

  private lateinit var userLocationViewModel: UserLocationViewModel

  companion object {
    val uid = "user123"
    val location = LocationGIS("Point", listOf(12.34, 56.78))
    val locationDto = UserLocationDto(uid, location)
  }

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    userLocationModel = mock(UserLocationModel::class.java)
    userLocationViewModel = UserLocationViewModel(userLocationModel)
  }

  @After
  fun tearDown() {
    // Clean up resources if needed
  }

  @Test
  fun `uploadUserLocation successful create`() = runTest {
    var onSuccessCalled = false

    `when`(userLocationModel.create(eq(locationDto), any(), any())).thenAnswer {
      it.getArgument<() -> Unit>(1)()
    }

    userLocationViewModel.uploadUserLocation(uid, location, onSuccess = { onSuccessCalled = true })

    assertTrue(onSuccessCalled)
  }

  @Test
  fun `uploadUserLocation failure create calls update`() = runTest {
    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("create failed")) }
        .`when`(userLocationModel)
        .create(eq(locationDto), any(), any())
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(userLocationModel)
        .update(eq(locationDto), any(), any())

    userLocationViewModel.uploadUserLocation(uid, location)

    verify(userLocationModel).update(eq(locationDto), any(), any())
  }

  @Test
  fun `uploadUserLocation update failure`() = runTest {
    var onFailureCalled = false

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("create failed")) }
        .`when`(userLocationModel)
        .create(eq(locationDto), any(), any())
    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("update failed")) }
        .`when`(userLocationModel)
        .update(eq(locationDto), any(), any())

    userLocationViewModel.uploadUserLocation(
        "user123",
        location,
        onFailure = { onFailureCalled = true },
    )

    assertTrue(onFailureCalled)
  }

  @Test
  fun `uploadUserLocation update success`() = runTest {
    var onSuccessCalled = false

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("create failed")) }
        .`when`(userLocationModel)
        .create(eq(locationDto), any(), any())
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(userLocationModel)
        .update(eq(locationDto), any(), any())

    userLocationViewModel.uploadUserLocation(
        "user123",
        location,
        onSuccess = { onSuccessCalled = true },
    )

    assertTrue(onSuccessCalled)
  }
}
