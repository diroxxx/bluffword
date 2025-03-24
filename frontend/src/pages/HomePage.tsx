import {ChangeEvent, useState} from "react";
import axios from "axios";

type GameRoomResponse = {
    id: number;
    code: string;
    gameMode: string;
    roundsTotal: number;
};
function HomePage() {

    const [code, setCode] = useState<string>("");

    const createRoom = async () => {
        try {
            const res = await axios.post<GameRoomResponse>(
                "http://localhost:8080/api/rooms",
                null,
                {
                    params: {
                        mode: "STATIC_IMPOSTOR",
                        rounds: 5,
                    },
                }
            );
            alert(`Kod pokoju: ${res.data.code}`);
            // TODO: Przejdź do lobby pokoju
        } catch (e) {
            console.error(e);
            alert("Błąd przy tworzeniu pokoju.");
        }
    };

    const joinRoom = () => {
        if (!code) return alert("Wpisz kod pokoju!");
        alert(`Dołączasz do pokoju: ${code}`);
    };

    const handleCodeChange = (e: ChangeEvent<HTMLInputElement>) => {
        setCode(e.target.value.toUpperCase());
    };

    return (
        <div style={{ padding: "2rem", textAlign: "center" }}>
            <h1>🎮 BluffWord</h1>

            <div style={{ marginTop: "2rem" }}>
                <button onClick={createRoom}>Stwórz nowy pokój</button>
            </div>

            <div style={{ marginTop: "2rem" }}>
                <input
                    type="text"
                    placeholder="Wpisz kod pokoju"
                    value={code}
                    onChange={handleCodeChange}
                />
                <button onClick={joinRoom}>Dołącz</button>
            </div>
        </div>
    );

}
export default HomePage;
