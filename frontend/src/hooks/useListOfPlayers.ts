
import { createStompChannelHook } from "../lib/CustomStomp.ts";
import type { PlayerInfo } from "../types/PlayerInfo.ts";

export const useListOfPlayers = (roomCode: string | undefined) => {

  if (!roomCode) {
    return { connected: false, messages: [], send: () => {} };
  }


  return createStompChannelHook<PlayerInfo[], {}>(  {
    url: "ws://localhost:8080/ws",
    subscribeDestination: `/topic/room/${roomCode}/players`,
    sendDestination: `/app/room/${roomCode}/players`,
  })();
};