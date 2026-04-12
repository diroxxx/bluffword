import { createStompChannelHook } from "../../lib/CustomStomp";

export const useStartGame = (roomCode?: string) => {
  if (!roomCode) {
    return { connected: false, messages: [], send: () => {} };
  }

  return createStompChannelHook<never, string>({
    url: "ws://localhost:8080/ws",
    subscribeDestination: `/topic/room/${roomCode}/state`,
    sendDestination: `/app/room/${roomCode}/start`,
  })();
};