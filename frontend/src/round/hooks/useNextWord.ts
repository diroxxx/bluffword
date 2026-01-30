import { createStompChannelHook } from "../../lib/CustomStomp";
import type { PlayerInfo } from "../../types/PlayerInfo";

export const useNextWord = (roomCode?: string, playerId?: number, ) => {

if (!roomCode) {
    return { connected: false, messages: [], send: () => {} };
  }


      return createStompChannelHook<PlayerInfo[], {}>(  {
        url: "ws://localhost:8080/ws",
        subscribeDestination: `/topic/room/${roomCode}/round/player/${playerId}/word`,
        sendDestination: `/app/room/${roomCode}/round/player/${playerId}/word`,
      })();
}