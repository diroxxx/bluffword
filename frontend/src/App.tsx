import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import RoomLobby from "./pages/RoomLobby";
import EnterNicknamePage from "./pages/EnterNicknamePage.tsx";
import {StompSessionProvider} from "react-stomp-hooks";

function App() {
    return (
        // <StompSessionProvider url="ws://localhost:8080/ws/websocket">
        //     <Router>
        //         <Routes>
        //             <Route path="/" element={<HomePage />} />
        //             <Route path="/enter-name" element={<EnterNicknamePage />} />
        //             <Route path="/room/:code" element={<RoomLobby />} />
        //         </Routes>
        //     </Router>
        // </StompSessionProvider>

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

    );
}


export default App;
