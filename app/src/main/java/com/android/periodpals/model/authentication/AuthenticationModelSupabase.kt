package com.android.periodpals.model.authentication

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.android.periodpals.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import java.security.MessageDigest
import java.util.UUID

private const val TAG = "AuthenticationModelSupabase"

/**
 * Implementation of the [AuthenticationModel] interface using Supabase for authentication.
 *
 * @property supabase The Supabase client instance.
 * @property pluginManagerWrapper Wrapper for the Supabase plugin manager.
 */
class AuthenticationModelSupabase(
    private val supabase: SupabaseClient,
    private val pluginManagerWrapper: PluginManagerWrapper =
        PluginManagerWrapperImpl(supabase.pluginManager),
) : AuthenticationModel {

  private val supabaseAuth: Auth = pluginManagerWrapper.getAuthPlugin()

  /**
   * Registers a new user with the provided email and password using Supabase.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   * @param onSuccess Callback function to be called on successful registration.
   * @param onFailure Callback function to be called on registration failure, with the exception as
   *   a parameter.
   */
  override suspend fun register(
      userEmail: String,
      userPassword: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      supabaseAuth.signUpWith(Email) {
        email = userEmail
        password = userPassword
      }
      Log.d(TAG, "register: successfully registered the user")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "register: failed to register the user: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Logs in a user with the provided email and password using Supabase.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   * @param onSuccess Callback function to be called on successful login.
   * @param onFailure Callback function to be called on login failure, with the exception as a
   *   parameter.
   */
  override suspend fun login(
      userEmail: String,
      userPassword: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      supabaseAuth.signInWith(Email) {
        email = userEmail
        password = userPassword
      }
      Log.d(TAG, "login: successfully logged in the user")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "login: failed to log in the user: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Logs out the current user using Supabase.
   *
   * @param onSuccess Callback function to be called on successful logout.
   * @param onFailure Callback function to be called on logout failure, with the exception as a
   *   parameter.
   */
  override suspend fun logout(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    try {
      supabaseAuth.signOut(SignOutScope.LOCAL)
      Log.d(TAG, "logout: successfully logged out the user")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "logout: failed to log out the user: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Checks if a user is logged in using Supabase.
   *
   * @param onSuccess Callback function to be called if the user is logged in.
   * @param onFailure Callback function to be called if the user is not logged in, with the
   *   exception as a parameter.
   */
  override suspend fun isUserLoggedIn(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    try {
      val currentUser = supabaseAuth.currentUserOrNull()
      if (currentUser != null) {
        Log.d(TAG, "logout: successfully logged out the user")
        onSuccess()
      } else {
        onFailure(Exception("Not logged in"))
      }
    } catch (e: Exception) {
      Log.d(TAG, "logout: failed to log out the user: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Fetches the current user's authentication data.
   *
   * @param onSuccess Callback function to be called if user's data is successfully fetched
   * @param onFailure Callback function to be called if exception is raised
   */
  override suspend fun currentAuthenticationUser(
      onSuccess: (UserInfo) -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      val currentUser: UserInfo? = supabaseAuth.currentUserOrNull()
      if (currentUser == null) {
        Log.d(TAG, "currentAuthUser: no user logged in")
        onFailure(Exception("No User Logged In"))
      }
      Log.d(TAG, "currentAuthUser: successfully retrieved data object")
      onSuccess(currentUser!!)
    } catch (e: Exception) {
      Log.d(TAG, "currentAuthUser: exception thrown: ${e.message} ")
      onFailure(e)
    }
  }

  override suspend fun loginGoogle(
      context: Context,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Create a CredentialManager instance
    val credentialManager = CredentialManager.create(context)

    // Generate a raw nonce and hash it using SHA-256
    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

    // Configure Google ID option
    val googleIdOption: GetGoogleIdOption =
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
            .setNonce(hashedNonce)
            .build()

    // Create a GetCredentialRequest
    val request: GetCredentialRequest =
        GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

    try {
      // Retrieve the credential
      val result = credentialManager.getCredential(request = request, context = context)
      val credential = result.credential
      val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
      val googleIdToken = googleIdTokenCredential.idToken

      // Sign in with Supabase
      supabase.auth.signInWith(IDToken) {
        idToken = googleIdToken
        provider = Google
        nonce = rawNonce
      }

      Toast.makeText(context, "You are signed in", Toast.LENGTH_SHORT).show()
      onSuccess()
    } catch (e: GoogleIdTokenParsingException) {
      Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
      onFailure(e)
    } catch (e: GetCredentialException) {
      Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
      onFailure(e)
    }
  }
}
