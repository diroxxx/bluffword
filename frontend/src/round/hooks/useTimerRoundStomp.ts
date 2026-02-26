import { createStompChannelHook } from "../../lib/CustomStomp";
import type { TimerType } from "../types/timerType";

export const useTimerRoundStomp = (roomCode: string, timerType: TimerType) => {

    if (!roomCode) {
        return { connected: false, messages: [], send: () => {} };
      }

            return createStompChannelHook<number, {}>(  {
                url: "ws://localhost:8080/ws",
                subscribeDestination: `/topic/round/${roomCode}/${timerType}/time`,
                sendDestination: `/app/room/${roomCode}/round/timer`,
                })();
}