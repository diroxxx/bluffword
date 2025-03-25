import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";

function EnterNicknamePage() {
    const [nickname, setNickname] = useState("");
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const mode = searchParams.get("mode");
    const existingCode = searchParams.get("code");
    console.log("mode =", mode);
    console.log("code =", existingCode);

    const handleSubmit = async () => {
        if (!nickname.trim()) {
            alert("Please enter a nickname");
            return;
        }
        try {
            let roomCode = existingCode;

            if (mode === "CREATE") {
                const res = await axios.post<{ code: string }>(
                    "http://localhost:8080/api/gameRoom/create",
                    {
                        nickname: nickname.trim(),
                    }
                );
                roomCode = res.data.code;
                console.log(roomCode);
            }

            localStorage.setItem("nickname", nickname.trim());
            navigate(`/room/${roomCode}`);
        } catch (err) {
            console.error(err);
            alert("Something went wrong");
        }
    };

    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gray-900 text-white px-4">
            <h1 className="text-3xl font-bold mb-4">Enter your nickname</h1>
            <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                className="px-4 py-2 rounded bg-gray-800 text-white w-full max-w-xs mb-4"
                placeholder="Your name..."
            />
            <button
                onClick={handleSubmit}
                className="bg-blue-600 px-6 py-2 rounded hover:bg-blue-700 transition"
            >
                Continue
            </button>
        </div>
    );
}

export default EnterNicknamePage;
