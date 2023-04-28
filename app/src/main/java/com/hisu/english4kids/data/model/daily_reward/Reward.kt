package com.hisu.english4kids.data.model.daily_reward

data class Reward(
    var id: Int,
    var reward: Int,
    var bonus: Int = 0,
    var isClaimed: Boolean = false, //đã nhận hay chưa
    var isClaimable: Boolean = false //hiện tại có thể nhận
    /**
     * Ví dụ: hôm nay ngày 2 thì:
     *  + ngày 1 [ isClaimed = true/false, isClaimable = false ]
     *  + ngày 2 [isClaimed = false, isClaimable = true]
     */
)