import React, { createContext, useContext, useEffect, useState } from "react";

export type PlayerInfo = {
    nickname: string;
    isImpostor: boolean;
    isHost: boolean;
};

type PlayerContextType = {
    player: PlayerInfo | null;
    setPlayer: (player: PlayerInfo) => void;
    players: PlayerInfo[];
    setPlayers: (players: PlayerInfo[]) => void;
    loading: boolean;
};

const PlayerContext = createContext<PlayerContextType | undefined>(undefined);

export const PlayerProvider = ({ children }: { children: React.ReactNode }) => {
    const [player, _setPlayer] = useState<PlayerInfo | null>(null);
    const [players, setPlayers] = useState<PlayerInfo[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const stored = sessionStorage.getItem("player");
        if (stored) {
            try {
                const parsed = JSON.parse(stored);
                _setPlayer(parsed);
            } catch (e) {
                console.error("Error parsing player from sessionStorage", e);
            }
        }
        setLoading(false);
    }, []);

    const setPlayer = (newPlayer: PlayerInfo) => {
        _setPlayer(newPlayer);
        sessionStorage.setItem("player", JSON.stringify(newPlayer));
    };

    return (
        <PlayerContext.Provider
            value={{ player, setPlayer, players, setPlayers, loading }}
        >
            {!loading && children}
        </PlayerContext.Provider>
    );
};

export const usePlayer = () => {
    const context = useContext(PlayerContext);
    if (!context) {
        throw new Error("usePlayer must be used within a PlayerProvider");
    }
    return context;
};
