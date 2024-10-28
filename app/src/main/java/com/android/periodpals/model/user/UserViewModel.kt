package com.android.periodpals.model.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing user data.
 *
 * @property userRepository The repository used for loading and saving user profiles.
 */
class UserViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

  private val _user = MutableStateFlow<User?>(null)
  val user: StateFlow<User?> = _user

  init {
    loadUserProfile()
  }

  /** Loads the user profile and updates the user state. */
  fun loadUserProfile() {
    viewModelScope.launch {
      val result = userRepository.loadUserProfile(1).asDomainModel() // hardcoded id
      _user.value = result
    }
  }

  /**
   * Saves the user profile.
   *
   * @param user The user profile to be saved.
   */
  fun saveUser(user: User) {
    viewModelScope.launch { userRepository.createUserProfile(user) }
  }

  /** Converts a UserDto to a User. */
  private fun UserDto.asDomainModel(): User {
    return User(
        id = this.id,
        displayName = this.displayName,
        email = this.email,
        imageUrl = this.imageUrl,
        description = this.description,
        age = this.age)
  }
}
