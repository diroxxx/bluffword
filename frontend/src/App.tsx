// src/App.tsx
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { StompSessionProvider } from "react-stomp-hooks";
import { PlayerProvider } from "./PlayerContext";
import HomePage from "./pages/HomePage";
import EnterNicknamePage from "./pages/EnterNicknamePage";
import RoomLobby from "./pages/RoomLobby";
import GameRoundPage from "./pages/GameRoundPage.tsx";

function App() {
    return (
    <StompSessionProvider url="ws://localhost:8080/ws/websocket">
        <PlayerProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/enter-name" element={<EnterNicknamePage />} />
                    <Route path="/room/:code" element={<RoomLobby />} />
                    <Route path="/room/:code/round" element={<GameRoundPage />} />

                </Routes>
            </Router>
        </PlayerProvider>
    </StompSessionProvider>
    );
}

export default App;
