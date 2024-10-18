package com.android.periodpals.model.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

  private val _user = MutableStateFlow<User?>(null)
  val user: StateFlow<User?> = _user

  fun loadUserProfile() {
    viewModelScope.launch {
      val result = userRepository.loadUserProfile(1).asDomainModel() // hardcoded id
      _user.value = result
    }
  }

  fun saveUser(user: User) {
    viewModelScope.launch { userRepository.createUserProfile(user) }
  }

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
