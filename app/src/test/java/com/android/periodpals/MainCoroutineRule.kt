package com.android.periodpals

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// https://stackoverflow.com/questions/71807957/how-test-a-viewmodel-function-that-launch-a-viewmodelscope-coroutine-android-ko

/*
 This rule allows to run the viewModelScope portions of code correctly.
 Make sure to add this rule to your View Model's tests to correctly run
*/

@ExperimentalCoroutinesApi
class MainCoroutineRule(private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    TestWatcher() {

  override fun starting(description: Description?) {
    super.starting(description)
    Dispatchers.setMain(dispatcher)
  }

  override fun finished(description: Description?) {
    super.finished(description)
    Dispatchers.resetMain()
  }
}
