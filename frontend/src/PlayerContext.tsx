import { createContext, useContext, useEffect, useState } from "react";

export type PlayerInfo = {
    nickname: string;
    isImpostor: boolean;
    isHost: boolean;
};

type PlayerContextType = {
    player: PlayerInfo | null;
    setPlayer: (player: PlayerInfo) => void;
    loading: boolean;
};

const PlayerContext = createContext<PlayerContextType | undefined>(undefined);

export const PlayerProvider = ({ children }: { children: React.ReactNode }) => {
    const [player, _setPlayer] = useState<PlayerInfo | null>(null);
    const [loading, setLoading] = useState(true);

    // ⏳ Wczytaj dane z sessionStorage przy starcie
    useEffect(() => {
        const stored = sessionStorage.getItem("player");
        if (stored) {
            _setPlayer(JSON.parse(stored));
        }
        setLoading(false);
    }, []);

    const setPlayer = (newPlayer: PlayerInfo) => {
        _setPlayer(newPlayer);
        sessionStorage.setItem("player", JSON.stringify(newPlayer));
    };

    return (
        <PlayerContext.Provider value={{ player, setPlayer, loading }}>
            {children}
        </PlayerContext.Provider>
    );
};

// 🧠 Użycie w komponentach
export const usePlayer = () => {
    const context = useContext(PlayerContext);
    if (!context) {
        throw new Error("usePlayer must be used within a PlayerProvider");
    }
    return context;
};
