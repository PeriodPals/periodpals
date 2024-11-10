package com.android.periodpals.resources

/** The `C` object contains nested objects that define constants used throughout the application. */
object C {

  /**
   * The `Tag` object contains nested objects that define constants for tagging UI components in
   * various screens of the application.
   */
  object Tag {

    /** Constants for tagging UI components in the CreateAlertScreen. */
    object CreateAlertScreen {
      const val SCREEN = "screen"
      const val INSTRUCTION_TEXT = "instructionText"
      const val PRODUCT_FIELD = "productField"
      const val URGENCY_FIELD = "urgencyField"
      const val DROPDOWN_ITEM = "dropdownItem"
      const val LOCATION_FIELD = "locationField"
      const val MESSAGE_FIELD = "messageField"
      const val SUBMIT_BUTTON = "submitButton"
    }

    /** Constants for tagging UI components in the AlertListsScreen. */
    object AlertListsScreen {
      const val SCREEN = "screen"
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

      object MyAlertItem {
        const val MY_ALERT = "myAlert"
        const val MY_EDIT_BUTTON = "myAlertEditButton"
        const val MY_EDIT_ICON = "myAlertEditIcon"
        const val MY_EDIT_TEXT = "myAlertEditText"
      }

      object PalsAlertItem {
        const val PAL_ALERT = "palsAlert"
        const val PAL_NAME = "palsName"
        const val PAL_DIVIDER = "palDivider"
        const val PAL_MESSAGE = "palMessage"
        const val PAL_BUTTONS = "palButtons"
        const val PAL_ACCEPT_BUTTON = "palAcceptButton"
        const val PAL_ACCEPT_ICON = "palAcceptIcon"
        const val PAL_ACCEPT_TEXT = "palAcceptText"
        const val PAL_DECLINE_BUTTON = "palDeclineButton"
        const val PAL_DECLINE_ICON = "palDeclineIcon"
        const val PAL_DECLINE_TEXT = "palDeclineText"
      }
    }

    /** Constants for tagging UI components in the authentication screens. */
    object AuthenticationScreens {
      /** Constants for tagging UI components in the SignInScreen. */
      object SignInScreen {
        const val SCREEN = "screen"
        const val INSTRUCTION_TEXT = "instructionText"
        const val SIGN_IN_BUTTON = "signInButton"
        const val CONTINUE_WITH_TEXT = "continueWith"
        const val GOOGLE_BUTTON = "googleButton"
        const val NOT_REGISTERED_BUTTON = "notRegisteredButton"
      }

      /** Constants for tagging UI components in the SignUpScreen. */
      object SignUpScreen {
        const val SCREEN = "screen"
        const val INSTRUCTION_TEXT = "instructionText"
        const val CONFIRM_PASSWORD_TEXT = "confirmText"
        const val CONFIRM_PASSWORD_FIELD = "confirmPasswordField"
        const val CONFIRM_PASSWORD_VISIBILITY_BUTTON = "confirmPasswordVisibilityButton"
        const val CONFIRM_PASSWORD_ERROR_TEXT = "confirmPasswordErrorText"
        const val SIGN_UP_BUTTON = "signUpButton"
      }

      // shared authentication components
      const val PASSWORD_ERROR_TEXT = "passwordErrorText"
      const val EMAIL_ERROR_TEXT = "emailErrorText"
      const val BACKGROUND = "background"
      const val WELCOME_TEXT = "titleText"
      const val EMAIL_FIELD = "emailField"
      const val PASSWORD_FIELD = "passwordField"
      const val PASSWORD_VISIBILITY_BUTTON = "passwordVisibilityButton"
    }

    /** Constants for tagging UI components in the MapScreen. */
    object MapScreen {
      const val SCREEN = "screen"
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
      const val EDIT_BUTTON = "editButton"
      const val TITLE_TEXT = "titleText"
    }

    /** Constants for tagging UI components in the CreateProfileScreen. */
    object CreateProfileScreen {
      const val SCREEN = "screen"
      const val PROFILE_PICTURE = "profilePicture"
      const val MANDATORY_TEXT = "mandatoryText"
      const val DOB_FIELD = "dobField"
      const val PROFILE_TEXT = "profileText"
      const val NAME_FIELD = "nameField"
      const val DESCRIPTION_FIELD = "descriptionField"
      const val SAVE_BUTTON = "saveButton"
    }

    /** Constants for tagging UI components in the EditProfileScreen. */
    object EditProfileScreen {
      const val SCREEN = "screen"
      const val PROFILE_PICTURE = "profilePicture"
      const val EDIT_ICON = "editIcon"
      const val MANDATORY_SECTION = "mandatorySection"
      const val NAME_FIELD = "nameField"
      const val DOB_FIELD = "dobField"
      const val YOUR_PROFILE_SECTION = "yourProfileSection"
      const val DESCRIPTION_FIELD = "descriptionField"
      const val SAVE_BUTTON = "saveButton"
    }

    /** Constants for tagging UI components in the ProfileScreen. */
    object ProfileScreen {
      const val SCREEN = "screen"
      const val PROFILE_PICTURE = "profilePicture"
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

    /** Constants for tagging UI components in the TimerScreen. */
    object TimerScreen {
      const val SCREEN = "screen"
      const val TIMER_TEXT = "timerText"
    }
  }
}
