package com.hisu.imastermatcher.model.pair_matching

data class PairMatchingModel(
    var id: Int,
    var pairId: Int,
    var imageUrl: Int,
    var answer: String,
    var isAudioWordPairs: Boolean = false,
    var audio: String?= null
)