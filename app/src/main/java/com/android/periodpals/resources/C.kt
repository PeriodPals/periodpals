package com.android.periodpals.resources

/** The `C` object contains nested objects that define constants used throughout the application. */
object C {

  /**
   * The `Tag` object contains nested objects that define constants for tagging UI components in
   * various screens of the application.
   */
  object Tag {

    /** Shared constants for tagging UI components in the CreateAlertScreen and EditAlertScreen. */
    object AlertInputs {
      const val INSTRUCTION_TEXT = "instructionText"
      const val PRODUCT_FIELD = "productField"
      const val URGENCY_FIELD = "urgencyField"
      const val DROPDOWN_ITEM = "dropdownItem"
      const val LOCATION_FIELD = "locationField"
      const val MESSAGE_FIELD = "messageField"
      const val CURRENT_LOCATION = "currentLocation"
    }

    /** Constants for tagging UI components specific to the CreateAlertScreen * */
    object CreateAlertScreen {
      const val SCREEN = "createAlertScreen"
      const val SUBMIT_BUTTON = "submitButton"
    }

    /** Constants for tagging UI components specific to the EditAlertScreen */
    object EditAlertScreen {
      const val SCREEN = "editAlertScreen"
      const val DELETE_BUTTON = "deleteButton"
      const val SAVE_BUTTON = "saveButton"
      const val RESOLVE_BUTTON = "resolveButton"
    }

    /** Constants for tagging UI components in the AlertListsScreen. */
    object AlertListsScreen {
      const val SCREEN = "alertListScreen"
      const val TAB_ROW = "tabRow"
      const val MY_ALERTS_TAB = "myAlertsTab"
      const val PALS_ALERTS_TAB = "palsAlertsTab"

      const val ALERT_PROFILE_PICTURE = "alertProfilePicture"
      const val ALERT_TIME_AND_LOCATION = "alertTimeAndLocation"
      const val ALERT_PRODUCT_AND_URGENCY = "alertProductAndUrgency"
      const val ALERT_PRODUCT_TYPE = "alertProductType"
      const val ALERT_URGENCY = "alertUrgency"
      const val NO_ALERTS_CARD = "noAlertsCard"
      const val NO_ALERTS_ICON = "noAlertsIcon"
      const val NO_ALERTS_TEXT = "noAlertsText"

      const val FILTER_FAB = "filterFab"
      const val FILTER_FAB_BUBBLE = "filterFabBubble"
      const val FILTER_DIALOG = "filterDialog"
      const val FILTER_DIALOG_TEXT = "filterAlertsText"
      const val FILTER_RADIUS_TEXT = "filterRadiusText"
      const val FILTER_RADIUS_SLIDER = "filterRadiusSlider"
      const val FILTER_APPLY_BUTTON = "filterApplyButton"
      const val FILTER_RESET_BUTTON = "filterResetButton"

      object MyAlertItem {
        const val MY_ALERT = "myAlert"
        const val MY_EDIT_BUTTON = "myAlertEditButton"
      }

      object PalsAlertItem {
        const val PAL_ALERT = "palsAlert"
        const val PAL_NAME = "palsName"
        const val PAL_MESSAGE = "palMessage"
        const val PAL_DIVIDER = "palDivider"
        const val PAL_BUTTONS = "palButtons"
        const val PAL_ACCEPT_BUTTON = "palAcceptButton"
        const val PAL_DECLINE_BUTTON = "palDeclineButton"
      }
    }

    /** Constants for tagging UI components in the authentication screens. */
    object AuthenticationScreens {
      /** Constants for tagging UI components in the SignInScreen. */
      object SignInScreen {
        const val SCREEN = "signInScreen"
        const val INSTRUCTION_TEXT = "instructionText"
        const val SIGN_IN_BUTTON = "signInButton"
        const val CONTINUE_WITH_TEXT = "continueWith"
        const val GOOGLE_BUTTON = "googleButton"
        const val NOT_REGISTERED_NAV_LINK = "notRegisteredButton"
      }

      /** Constants for tagging UI components in the SignUpScreen. */
      object SignUpScreen {
        const val SCREEN = "signUpScreen"
        const val INSTRUCTION_TEXT = "instructionText"
        const val CONFIRM_PASSWORD_TEXT = "confirmText"
        const val CONFIRM_PASSWORD_FIELD = "confirmPasswordField"
        const val CONFIRM_PASSWORD_VISIBILITY_BUTTON = "confirmPasswordVisibilityButton"
        const val CONFIRM_PASSWORD_ERROR_TEXT = "confirmPasswordErrorText"
        const val SIGN_UP_BUTTON = "signUpButton"
        const val ALREADY_REGISTERED_NAV_LINK = "alreadyRegisteredButton"
      }

      // shared authentication components
      const val PASSWORD_ERROR_TEXT = "passwordErrorText"
      const val EMAIL_ERROR_TEXT = "emailErrorText"
      const val BACKGROUND = "background"
      const val WELCOME_TEXT = "welcomeText"
      const val EMAIL_FIELD = "emailField"
      const val PASSWORD_FIELD = "passwordField"
      const val PASSWORD_VISIBILITY_BUTTON = "passwordVisibilityButton"
    }

    /** Constants for tagging UI components in the MapScreen. */
    object MapScreen {
      const val SCREEN = "mapScreen"
      const val MAP_VIEW_CONTAINER = "mapViewContainer"
      const val MY_LOCATION_BUTTON = "mapButton"
    }

    /** Constants for tagging UI components in the BottomNavigationMenu. */
    object BottomNavigationMenu {
      const val BOTTOM_NAVIGATION_MENU = "bottomNavigationMenu"
      const val BOTTOM_NAVIGATION_MENU_ITEM = "bottomNavigationMenu"
    }

    /** Constants for tagging UI components in the TopAppBar. */
    object TopAppBar {
      const val TOP_BAR = "topBar"
      const val GO_BACK_BUTTON = "goBackButton"
      const val SETTINGS_BUTTON = "settingsButton"
      const val CHAT_BUTTON = "chatButton"
      const val EDIT_BUTTON = "editButton"
      const val TITLE_TEXT = "titleText"
    }

    /** Constants for tagging UI components in the profile screens. */
    object ProfileScreens {
      /** Constants for tagging UI components in the CreateProfileScreen. */
      object CreateProfileScreen {
        const val SCREEN = "createProfileScreen"
        const val FILTER_RADIUS_EXPLANATION_TEXT = "filterRadiusExplanationText"
      }

      /** Constants for tagging UI components in the EditProfileScreen. */
      object EditProfileScreen {
        const val SCREEN = "editProfileScreen"
        const val EDIT_PROFILE_PICTURE = "editProfilePicture"
      }

      /** Constants for tagging UI components in the ProfileScreen. */
      object ProfileScreen {
        const val SCREEN = "profileScreen"
        const val NAME_FIELD = "nameField"
        const val DESCRIPTION_FIELD = "descriptionField"
        const val CONTRIBUTION_FIELD = "contributionField"
        const val REVIEWS_SECTION = "reviewsSection"
        const val REVIEW_ONE = "reviewOne"
        const val REVIEW_TWO = "reviewTwo"
        const val NO_REVIEWS_ICON = "noReviewsIcon"
        const val NO_REVIEWS_TEXT = "noReviewsText"
        const val NO_REVIEWS_CARD = "noReviewsCard"
      }

      // Shared profile components
      const val PROFILE_PICTURE = "profilePicture"
      const val MANDATORY_SECTION = "mandatorySection"
      const val YOUR_PROFILE_SECTION = "yourProfileSection"
      const val NAME_INPUT_FIELD = "nameInputField"
      const val DOB_INPUT_FIELD = "dobInputField"
      const val DOB_MIN_AGE_TEXT = "dobMinAgeText"
      const val DESCRIPTION_INPUT_FIELD = "descriptionInputField"
      const val SAVE_BUTTON = "saveButton"
    }

    object SettingsScreen {
      const val SCREEN = "settingsScreen"
      const val ACCOUNT_MANAGEMENT_CONTAINER = "accountManagementContainer"
      const val THEME_DROP_DOWN_MENU = "themeDropdownMenu"
      const val PASSWORD_TEXT = "passwordText"
      const val PASSWORD_ICON = "passwordIcon"
      const val SIGN_OUT_TEXT = "signOutText"
      const val SIGN_OUT_ICON = "signOutIcon"
      const val DELETE_ACCOUNT_TEXT = "deleteAccountText"
      const val DELETE_ACCOUNT_ICON = "deleteAccountIcon"
      const val DELETE_ACCOUNT_CARD = "deleteCard"
      const val CARD_EMOJI_ICON = "deleteEmojiIcon"
      const val CARD_TEXT = "deleteText"
      const val DELETE_BUTTON = "deleteButton"
      const val NOT_DELETE_BUTTON = "notDeleteButton"
    }

    /** Constants for tagging UI components in the TimerScreen. */
    object TimerScreen {
      const val SCREEN = "timerScreen"
      const val DISPLAYED_TEXT = "displayedText"
      const val CIRCULAR_PROGRESS_INDICATOR = "circularProgressIndicator"
      const val HOURGLASS = "Hourglass"
      const val START_BUTTON = "Start button"
      const val RESET_BUTTON = "Reset button"
      const val STOP_BUTTON = "Stop button"
      const val USEFUL_TIP = "usefulTip"
      const val USEFUL_TIP_TEXT = "usefulTipText"

      // Displayed texts
      const val DISPLAYED_TEXT_ONE =
          "Start your tampon timer.\n" + "You’ll be reminded to change it !"
      const val DISPLAYED_TEXT_TWO =
          "You’ve got this. Stay strong !\n" + "Don’t forget to stay hydrated !"
    }
  }
}
