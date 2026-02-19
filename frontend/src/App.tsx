import Landing from "@/pages/LandingPage/Landing.tsx";
import Profile from "@/pages/Profile";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Provider } from "jotai";

const App = () => {
    return (
        <Provider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Landing/>} />
                    <Route path="/profile" element={<Profile />} />
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </BrowserRouter>
        </Provider>
    );
}

export default App;
