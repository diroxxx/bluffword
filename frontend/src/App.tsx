import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import RoomLobby from "./pages/RoomLobby";
import EnterNicknamePage from "./pages/EnterNicknamePage.tsx";

function App() {

  return (
      <Router>
        <Routes>
            <Route  path="/" element={<HomePage />} />
            <Route path="/enter-name" element={<EnterNicknamePage />} />
          <Route path="/room/:code" element={<RoomLobby />} />
        </Routes>
      </Router>
  );

    // return <h1>Hello world</h1>;

}

export default App;
