import type { PlayerInfo } from "./PlayerInfo.ts";

export type GameRoomDto = {
    roomCode: string;
    players: PlayerInfo[];
    currentRound?: number;
    totalRounds?: number;
};