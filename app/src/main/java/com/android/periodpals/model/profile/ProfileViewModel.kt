package com.android.periodpals.model.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository, savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _profileList = MutableStateFlow<List<Profile>?>(listOf())
    val profileList: Flow<List<Profile>?> = _profileList

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: Flow<Profile?> = _profile

    private val _name = MutableStateFlow("")
    val name: Flow<String> = _name

    private val _description = MutableStateFlow("")
    val description: Flow<String> = _description

    private val _imageUrl = MutableStateFlow("")
    val imageUrl: Flow<String> = _imageUrl

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email


    private fun loadUserProfile(profileId: String) {
        viewModelScope.launch {
            val result = profileRepository.loadUserProfile(profileId).asDomainModel()
            _profile.emit(result)
            _name.emit(result.name)
            _description.emit(result.description)
            _email.emit(result.email)

        }
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
    }

    fun onSaveProfile() {
        viewModelScope.launch {
            profileRepository.updateUserProfile(
                id = _profile.value?.id,
                description = _description.value,
                name = _name.value,
                avatarUrl = _imageUrl.value,
                email = _email.value
            )
        }
    }

    /**
    fun onImageChange(url: String) {
        _imageUrl.value = url
    }
    **/

    private fun ProfileDto.asDomainModel(): Profile {
        return Profile(
            id = this.id,
            name = this.name,
            email = this.email,
            avatarUrl = this.avatarUrl,
            description = this.description
        )
    }
}