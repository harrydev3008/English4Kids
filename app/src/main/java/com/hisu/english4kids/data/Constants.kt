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
const val PATH_SEARCH_USER_BY_PHONE = "public/search"

//course - token needed
const val PATH_GET_COURSE = "course/"

//lesson - token needed
const val PATH_GET_LESSON_BY_COURSE_ID = "lession/{courseId}"

//auth
const val PATH_AUTH_LOGIN = "auth/login"
const val PATH_AUTH_REGISTER = "auth/register"
const val PATH_AUTH_LOGOUT = "auth/logout"
const val CHECK_SSO = "auth/checkSSO"

//diary - token needed
const val PATH_UPDATE_USER_DIARY = "diary"

//user player - token needed
const val PATH_UPDATE_USER_INFO = "user"
const val PATH_GET_USER_INFO = "user"
const val PATH_BUY_HEART = "user/hearts"
const val PATH_UPDATE_GOLDS = "user/updateGolds"
const val PATH_UPDATE_SCORE_AND_GOLDS = "user/update-score-and-golds"
const val PATH_GET_WEEKLY_RANK = "user/scoreBoard"

const val PATH_DAILY = "user/login-rewards"
const val PATH_UPDATE_HEART = "user/update-hearts"
const val PATH_AUTH_CHANGE_PASSWORD = "user/change-password"
const val PATH_AUTH_FORGOT_PASSWORD = "user/forget-password"
const val PATH_GET_EXAM = "test"

const val ATTACHMENT_TYPE_NONE = "NONE"
const val ATTACHMENT_TYPE_AUDIO = "AUDIO"
const val ATTACHMENT_TYPE_IMAGE = "IMAGE"

const val PLAY_STATUS_NONE = "NONE"
const val PLAY_STATUS_DONE = "DONE"
const val PLAY_STATUS_FAIL = "FAILED"