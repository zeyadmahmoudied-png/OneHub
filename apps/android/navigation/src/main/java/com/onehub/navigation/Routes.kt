package com.onehub.navigation

object Routes {
    const val HOME = "home"
    const val HUB_APPS = "hub/apps"
    const val HUB_APP_DETAIL = "hub/app/{appId}"
    const val FEATURE_SLOTS = "features"
    const val FEATURE_DETAIL = "features/{featureId}"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ACCESS_OWNER_SETUP = "access/owner-setup"
    const val ACCESS_OWNER_LOGIN = "access/owner-login"
    const val ACCESS_ADMIN = "access/admin"
    const val ACCESS_REDEEM = "access/redeem"

    fun hubApp(appId: String) = "hub/app/$appId"
    fun feature(featureId: String) = "features/$featureId"
}
