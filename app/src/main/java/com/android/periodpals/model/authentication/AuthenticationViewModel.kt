package com.android.periodpals.model.authentication

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.UserAuthenticationState
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.launch

private const val TAG = "AuthenticationViewModel"

/**
 * ViewModel for handling authentication-related operations.
 *
 * @property authenticationModel The authentication model used for performing auth operations.
 */
class AuthenticationViewModel(private val authenticationModel: AuthenticationModel) : ViewModel() {

  private val _userAuthenticationState =
      mutableStateOf<UserAuthenticationState>(UserAuthenticationState.Loading)
  private val _authUserData = mutableStateOf<AuthenticationUserData?>(null)

  val userAuthenticationState: State<UserAuthenticationState> = _userAuthenticationState
  val authUserData: State<AuthenticationUserData?> = _authUserData

  /**
   * Registers a new user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   * @param onSuccess Callback to be invoked when the registration is successful.
   * @param onFailure Callback to be invoked when the registration fails.
   */
  fun signUpWithEmail(
      userEmail: String,
      userPassword: String,
      onSuccess: () -> Unit = { Log.d(TAG, "signUp success callback") },
      onFailure: (Exception) -> Unit = { e: Exception -> Log.d(TAG, "signUp failure callback: $e") }
  ) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.register(
          userEmail = userEmail,
          userPassword = userPassword,
          onSuccess = {
            Log.d(TAG, "signUpWithEmail: registered user successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Registered user successfully")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "signUpWithEmail: failed to register user: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Logs in a user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   * @param onSuccess Callback to be invoked when the login is successful.
   * @param onFailure Callback to be invoked when the login fails.
   */
  fun logInWithEmail(
      userEmail: String,
      userPassword: String,
      onSuccess: () -> Unit = { Log.d(TAG, "signIn success callback") },
      onFailure: (Exception) -> Unit = { e: Exception -> Log.d(TAG, "signIn failure callback: $e") }
  ) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.login(
          userEmail = userEmail,
          userPassword = userPassword,
          onSuccess = {
            Log.d(TAG, "logInWithEmail: logged in successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Logged in successfully")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "logInWithEmail: failed to log in: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Logs out the current user.
   *
   * @param onSuccess Callback to be invoked when the logout is successful.
   * @param onFailure Callback to be invoked when the logout fails.
   */
  fun logOut(
      onSuccess: () -> Unit = { Log.d(TAG, "logOut success callback") },
      onFailure: (Exception) -> Unit = { e: Exception -> Log.d(TAG, "logOut failure callback: $e") }
  ) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.logout(
          onSuccess = {
            Log.d(TAG, "logOut: logged out successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Logged out successfully")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "logOut: failed to log out: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Checks if a user is logged in.
   *
   * @param onSuccess Callback to be invoked when the user is confirmed to be logged in.
   * @param onFailure Callback to be invoked when the user is not logged in.
   */
  fun isUserLoggedIn(
      onSuccess: () -> Unit = { Log.d(TAG, "isUserLoggedIn success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "isUserLoggedIn failure callback: $e")
      }
  ) {
    Thread.sleep(1500)
    viewModelScope.launch {
      authenticationModel.isUserLoggedIn(
          onSuccess = {
            Log.d(TAG, "isUserLoggedIn: user is confirmed logged in")
            _userAuthenticationState.value = UserAuthenticationState.Success("User is logged in")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "isUserLoggedIn: user is not logged in")
            _userAuthenticationState.value = UserAuthenticationState.Error("User is not logged in")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Loads AuthenticationUserData to local state.
   *
   * @param onSuccess Callback to be invoked when the user data is successfully loaded.
   * @param onFailure Callback to be invoked when the user data fails to load.
   */
  fun loadAuthenticationUserData(
      onSuccess: () -> Unit = { Log.d(TAG, "loadAuthenticationUserData success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "loadAuthenticationUserData failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      authenticationModel.currentAuthenticationUser(
          onSuccess = {
            Log.d(TAG, "loadAuthUserData: user data successfully loaded")
            _authUserData.value = it.asAuthenticationUserData()
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "loadAuthUserData: failed to load user data")
            _authUserData.value = null
            onFailure(e)
          },
      )
    }
  }

  /** Convert UserInfo into AuthenticationUserData */
  private fun UserInfo.asAuthenticationUserData(): AuthenticationUserData {
    return AuthenticationUserData(uid = this.id, email = this.email)
  }
}
