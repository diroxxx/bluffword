import axios from "axios";
import type { PlayerInfo } from "../../types/PlayerInfo";
import type { JoinGameRoomRequestDto } from "../../dtos/JoinGameRoomRequestDto";

export const postJoinRoom = async (nickname: string, roomCode: string) => {
    
    const dto: JoinGameRoomRequestDto = { nickname, roomCode };
    const response = await axios.post<PlayerInfo>("http://localhost:8080/api/gameRoom/join", dto);
    return response.data;
};