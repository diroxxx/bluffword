export type GameSettings = {
     roomCode: string;
     roundTotal: number;
     maxPlayers: number;   
     minPlayers: number;
     timeLimitAnswer: number;
     timeLimitVote: number;
     gameMode: "STATIC_IMPOSTOR" | "ROUND_IMPOSTOR";
    
};