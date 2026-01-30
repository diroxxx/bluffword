import axios from "axios";
import type { PlayerInfo } from "../../types/PlayerInfo";
import type { GameSettings } from "../types/GameSettings";

export const postCreateRoom = async (nickname: string, game: GameSettings) => {
    const response = await axios.post<PlayerInfo>("http://localhost:8080/api/gameRoom/create", { nickname, game });
    return response.data;
};