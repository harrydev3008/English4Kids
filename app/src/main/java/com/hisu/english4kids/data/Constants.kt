package com.hisu.english4kids.data

//Status code - Permissions code
const val STATUS_OK = 200
const val STATUS_NOT_FOUND = 404
const val STATUS_SERVER_DOWN = 500

//Content type
const val CONTENT_TYPE_JSON = "application/json"

const val PATH_INTERNET_TIME = "api/timezone/Asia/Ho_Chi_Minh/"

//public - no token needed
const val PATH_SEARCH_USER_BY_PHONE = "public/player/search"

//auth
const val PATH_AUTH_LOGIN = "auth/player/login"
const val PATH_AUTH_REGISTER = "auth/player/register"
const val PATH_AUTH_LOGOUT = "auth/player/logout"