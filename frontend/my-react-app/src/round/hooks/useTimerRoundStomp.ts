import { createStompChannelHook } from "../../lib/CustomStomp";

export const useTimerRoundStomp = (roomCode?: string) => {

    if (!roomCode) {
        return { connected: false, messages: [], send: () => {} };
      }

            return createStompChannelHook<number[], {}>(  {
                url: "ws://localhost:8080/ws",
                subscribeDestination: `/topic/room/${roomCode}/round/timer`,
                sendDestination: `/app/room/${roomCode}/round/timer`,
                })();
}