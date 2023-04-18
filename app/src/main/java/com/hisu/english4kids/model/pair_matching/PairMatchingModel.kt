package com.hisu.english4kids.model.pair_matching

data class PairMatchingModel(
    var id: Int,
    var pairId: Int,
    var url: String, //could be image_url or audio_url
    var answer: String,
    var isAudioQuestion: Boolean = false
)