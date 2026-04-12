import { createStompChannelHook } from "../../lib/CustomStomp";

export const useCategoryChoices = (roomCode: string, playerId: string, roundNumber: number) => {
    return createStompChannelHook<string[], string>({
        url: "ws://localhost:8080/ws",
        subscribeDestination: `/topic/room/${roomCode}/player/${playerId}/category/choices`,
        sendDestination: `/app/room/${roomCode}/round/${roundNumber}/player/${playerId}/category/request`,
    })();
};