import axios from "axios"
import type { GameSettings } from "../types/GameSettings";

export const getGameSettings = async (roomCode: string) => {
    if
    (!roomCode) {
        throw new Error("Room code is required to fetch game settings.");
    }

    const result = await axios.get<GameSettings>("http://localhost:8080/api/gameRoom/settings?roomCode=" + roomCode);
    return result.data;
}