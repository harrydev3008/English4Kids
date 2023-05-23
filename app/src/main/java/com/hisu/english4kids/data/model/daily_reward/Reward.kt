package com.hisu.english4kids.data.model.daily_reward

data class Reward(
    var reward: Int,
    var isClaimed: Int = -1
    /**
     * -1: locked
     *  0: claimable
     *  1: claimed
     */
)