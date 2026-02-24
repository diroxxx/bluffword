import { createStompChannelHook } from "../../lib/CustomStomp";

export const useTimerRoundStomp = (roomCode?: string, playerId?: string) => {

    if (!roomCode) {
        return { connected: false, messages: [], send: () => {} };
      }

            return createStompChannelHook<number, {}>(  {
                url: "ws://localhost:8080/ws",
                subscribeDestination: `/topic/round/${roomCode}/time`,
                sendDestination: `/app/room/${roomCode}/round/timer`,
                })();
}