import axios from "axios";
import type { PlayerInfo } from "../../types/PlayerInfo";
import type { GameSettings } from "../types/GameSettings";

export const postCreateRoom = async (nickname: string, settings: GameSettings) => {
    const response = await axios.post<PlayerInfo>("http://localhost:8080/api/gameRoom/create", { nickname, settings });
    return response.data;
};

