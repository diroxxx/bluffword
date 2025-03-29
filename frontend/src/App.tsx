// src/App.tsx
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { StompSessionProvider } from "react-stomp-hooks";
import { PlayerProvider } from "./PlayerContext";
import HomePage from "./pages/HomePage";
import EnterNicknamePage from "./pages/EnterNicknamePage";
import RoomLobby from "./pages/RoomLobby";

function App() {
    return (
        <PlayerProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/enter-name" element={<EnterNicknamePage />} />
                    <Route
                        path="/room/:code"
                        element={
                            <StompSessionProvider url="ws://localhost:8080/ws/websocket">
                                <RoomLobby />
                            </StompSessionProvider>
                        }
                    />
                </Routes>
            </Router>
        </PlayerProvider>
    );
}

export default App;
