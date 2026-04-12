export async function startGame(roomCode: string, playerId: string): Promise<void> {
    await fetch(`http://localhost:8080/api/round/room/${roomCode}/start?playerId=${encodeURIComponent(playerId)}`, {
        method: "POST",
    });
}