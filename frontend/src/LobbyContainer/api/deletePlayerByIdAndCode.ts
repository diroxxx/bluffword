import axios from "axios"

export const deletePlayerByIdAndCode = async (playerId: number, roomCode: string) => {

    const response = axios.delete('/api/rooms/players', { data: { playerId, roomCode } });
    return response;
}