package org.project.backend_kotlin.gameRoom

import org.project.backend_kotlin.round.dto.TimerType

interface TimerExpiryHandler {

    fun onTimerExpired(roomCode: String, timerType: TimerType)

}