import type { GameRoomState } from "../LobbyContainer/types/gameRoomState.ts";
import { createStompChannelHook } from "../lib/CustomStomp.ts";

export const useGameStateGetSocket = (roomCode: string | undefined) => {

  if (!roomCode) {
    return { connected: false, messages: [], send: () => {} };
  }

  return createStompChannelHook<GameRoomState, {}>(  {
    url: "ws://localhost:8080/ws",
    subscribeDestination: `/topic/room/${roomCode}/state`,
    sendDestination: `/app/room/${roomCode}/state/get`,
  })();
};