import Landing from "@/pages/LandingPage/Landing.tsx";
import {BrowserRouter, Routes, Route} from "react-router";


const App = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Landing/>} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
