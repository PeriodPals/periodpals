package com.android.periodpals.model.user

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsc.form_builder.FormState
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import java.text.DateFormat
import java.util.Locale
import kotlinx.coroutines.launch

private const val TAG = "UserViewModel"

private const val MAX_NAME_LENGTH = 128
private const val MAX_DESCRIPTION_LENGTH = 512

private const val ERROR_INVALID_NAME = "Please enter a name"
private const val ERROR_NAME_TOO_LONG = "Name must be less than $MAX_NAME_LENGTH characters"
private const val ERROR_INVALID_DESCRIPTION = "Please enter a description"
private const val ERROR_DESCRIPTION_TOO_LONG =
    "Description must be less than $MAX_DESCRIPTION_LENGTH characters"
private const val ERROR_INVALID_DOB = "Invalid date"

private val nameValidators =
    listOf(
        Validators.Required(message = ERROR_INVALID_NAME),
        Validators.Max(message = ERROR_NAME_TOO_LONG, limit = MAX_NAME_LENGTH),
    )
private val descriptionValidators =
    listOf(
        Validators.Required(message = ERROR_INVALID_DESCRIPTION),
        Validators.Max(message = ERROR_DESCRIPTION_TOO_LONG, limit = MAX_DESCRIPTION_LENGTH),
    )
private val dobValidators =
    listOf(
        Validators.Required(message = ERROR_INVALID_DOB),
        Validators.Custom(message = ERROR_INVALID_DOB, function = { validateDate(it as String) }),
    )
private val profileImageValidators =
    emptyList<Validators>() // TODO: add validators when profile image is implemented

/**
 * ViewModel for managing user data.
 *
 * @property userRepository The repository used for loading and saving user profiles.
 */
class UserViewModel(private val userRepository: UserRepositorySupabase) : ViewModel() {
  companion object {
    const val NAME_STATE_NAME = "name"
    const val DESCRIPTION_STATE_NAME = "description"
    const val DOB_STATE_NAME = "dob"
    const val PROFILE_IMAGE_STATE_NAME = "profile_image"
  }

  private val _user = mutableStateOf<User?>(null)
  val user: State<User?> = _user
  private val _avatar = mutableStateOf<ByteArray?>(null)
  val avatar: State<ByteArray?> = _avatar

  val formState =
      FormState(
          fields =
              listOf(
                  TextFieldState(name = NAME_STATE_NAME, validators = nameValidators),
                  TextFieldState(name = DESCRIPTION_STATE_NAME, validators = descriptionValidators),
                  TextFieldState(name = DOB_STATE_NAME, validators = dobValidators),
                  TextFieldState(
                      name = PROFILE_IMAGE_STATE_NAME, validators = profileImageValidators),
              ))

  /**
   * Initializes the user profile.
   *
   * @param onSuccess Callback function to be called when the user profile is successfully loaded.
   * @param onFailure Callback function to be called when there is an error loading the user
   *   profile.
   */
  fun init(
      onSuccess: () -> Unit = { Log.d(TAG, "init success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "init failure callback: ${e.message}")
      },
  ) {
    loadUser(
        onSuccess = {
          user.value?.let {
            downloadFile(
                it.imageUrl,
                onSuccess = { onSuccess() },
                onFailure = { e: Exception -> onFailure(Exception(e)) })
          }
        },
        onFailure = { e: Exception -> onFailure(Exception(e)) })
  }

  /**
   * Loads the user profile and updates the user state.
   *
   * @param onSuccess Callback function to be called when the user profile is successfully loaded.
   * @param onFailure Callback function to be called when there is an error loading the user
   *   profile.
   */
  fun loadUser(
      onSuccess: () -> Unit = { Log.d(TAG, "loadUser success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "loadUser failure callback: ${e.message}")
      },
  ) {
    viewModelScope.launch {
      userRepository.loadUserProfile(
          onSuccess = { userDto ->
            Log.d(TAG, "loadUserProfile: Successful")
            _user.value = userDto.asUser()
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "loadUserProfile: fail to load user profile: ${e.message}")
            _user.value = null
            onFailure(e)
          },
      )
    }
  }

  /**
   * Saves the user profile.
   *
   * @param user The user profile to be saved.
   * @param onSuccess Callback function to be called when the user profile is successfully saved.
   * @param onFailure Callback function to be called when there is an error saving the user profile.
   */
  fun saveUser(
      user: User,
      onSuccess: () -> Unit = { Log.d(TAG, "saveUser success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "saveUser failure callback: ${e.message}")
      },
  ) {
    viewModelScope.launch {
      userRepository.upsertUserProfile(
          user.asUserDto(),
          onSuccess = {
            Log.d(TAG, "saveUser: Success")
            _user.value = it.asUser()
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "saveUser: fail to save user: ${e.message}")
            _user.value = null
            onFailure(e)
          },
      )
    }
  }

  /**
   * Deletes the user profile.
   *
   * @param idUser The ID of the user profile to be deleted.
   * @param onSuccess Callback function to be called when the user profile is successfully deleted.
   * @param onFailure Callback function to be called when there is an error deleting the user
   *   profile.
   */
  fun deleteUser(
      idUser: String,
      onSuccess: () -> Unit = { Log.d(TAG, "deleteAccount success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "deleteAccount failure callback: ${e.message}")
      },
  ) {
    viewModelScope.launch {
      userRepository.deleteUserProfile(
          idUser,
          onSuccess = {
            Log.d(TAG, "deleteAccount: Success")
            _user.value = null
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "deleteAccount : fail to delete user: ${e.message}")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Uploads a file to the storage.
   *
   * @param filePath The path of the file to be uploaded.
   * @param bytes The bytes of the file to be uploaded.
   * @param onSuccess Callback function to be called on success.
   * @param onFailure Callback function to be called when there is an exception.
   */
  fun uploadFile(
      filePath: String,
      bytes: ByteArray,
      onSuccess: () -> Unit = { Log.d(TAG, "uploadFile success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "uploadFile failure callback: ${e.message}")
      }
  ) {
    viewModelScope.launch {
      userRepository.uploadFile(
          filePath,
          bytes,
          onSuccess = {
            Log.d(TAG, "uploadFile: Success")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "uploadFile: fail to upload file: ${e.message}")
            onFailure(e)
          })
    }
  }

  /**
   * Downloads a file from the storage.
   *
   * @param filePath The path of the file to be downloaded.
   * @param onSuccess Callback function to be called on success.
   * @param onFailure Callback function to be called when there is an exception.
   */
  fun downloadFilePublic(
      filePath: String,
      onSuccess: () -> Unit = { Log.d(TAG, "downloadFile success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "downloadFile failure callback: ${e.message}")
      }
  ) {
    viewModelScope.launch {
      userRepository.downloadFilePublic(
          filePath,
          onSuccess = { bytes ->
            Log.d(TAG, "downloadFile: Success")
            _avatar.value = bytes
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "downloadFile: fail to download file: ${e.message}")
            onFailure(e)
          })
    }
  }

  /**
   * Downloads a file from the storage for the authenticated user.
   *
   * @param filePath The path of the file to be downloaded.
   * @param onSuccess Callback function to be called on success.
   * @param onFailure Callback function to be called when there is an exception.
   */
  fun downloadFile(
      filePath: String,
      onSuccess: () -> Unit = { Log.d(TAG, "downloadFile success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "downloadFile failure callback: ${e.message}")
      }
  ) {
    viewModelScope.launch {
      userRepository.downloadFile(
          filePath,
          onSuccess = { bytes ->
            Log.d(TAG, "downloadFile: Success")
            _avatar.value = bytes
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "downloadFile: fail to download file: ${e.message}")
            onFailure(e)
          })
    }
  }
}

/**
 * Validates the date is in the format DD/MM/YYYY and is a valid date.
 *
 * @param date The date string to validate.
 * @return True if the date is valid, otherwise false.
 */
fun validateDate(date: String): Boolean {
  val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)
  dateFormat.isLenient = false
  return try {
    dateFormat.parse(date)
    true
  } catch (e: Exception) {
    false
  }
}
