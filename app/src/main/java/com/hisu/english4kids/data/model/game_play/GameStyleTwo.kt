package com.hisu.english4kids.data.model.game_play

import com.hisu.english4kids.data.model.card.Card
import com.hisu.english4kids.data.model.course.Round

class GameStyleTwo(
    playType: Int,
    score: Int,
    var question: String,
    var correctAns: String,
    var cards: List<Card>
): Round(playType, score)