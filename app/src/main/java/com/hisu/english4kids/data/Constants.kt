package com.hisu.english4kids.data

//Status code - Permissions code
const val STATUS_OK = 200
const val STATUS_NOT_FOUND = 404
const val STATUS_SERVER_DOWN = 500

const val BUNDLE_LESSON_DATA = "BUNDLE_LESSON_DATA"
const val BUNDLE_LESSON_ID_DATA = "BUNDLE_LESSON_ID_DATA"
const val BUNDLE_COURSE_ID_DATA = "BUNDLE_COURSE_ID_DATA"
const val BUNDLE_ROUND_PLAYED_DATA = "BUNDLE_ROUND_PLAYED_DATA"

//Content type
const val CONTENT_TYPE_JSON = "application/json"

const val PATH_INTERNET_TIME = "api/timezone/Asia/Ho_Chi_Minh/"

//public - no token needed
const val PATH_SEARCH_USER_BY_PHONE = "public/player/search"

//course - token needed
const val PATH_GET_COURSE = "course/player"

//lesson - token needed
const val PATH_GET_LESSON_BY_COURSE_ID = "lession/player/{courseId}"

//auth
const val PATH_AUTH_LOGIN = "auth/player/login"
const val PATH_AUTH_REGISTER = "auth/player/register"
const val PATH_AUTH_LOGOUT = "auth/player/logout"
const val PATH_AUTH_CHANGE_PASSWORD = "user/player/change-password"
const val PATH_AUTH_FORGOT_PASSWORD = "user/player/forget-password"
const val CHECK_SSO = "auth/player/checkSSO"

//diary
const val PATH_UPDATE_USER_DIARY = "diary/player"

//user player
const val PATH_UPDATE_USER_INFO = "user/player"
const val PATH_BUY_HEART = "user/player/hearts"
const val PATH_UPDATE_GOLDS = "user/player/updateGolds"
const val PATH_GET_WEEKLY_RANK = "user/player/scoreBoard"
const val PATH_GET_USER_INFO = "user/player"
const val PATH_DAILY = "user/player/login-rewards"
const val PATH_UPDATE_HEART = "user/player/update-hearts"