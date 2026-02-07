import Landing from "@/pages/LandingPage/Landing.tsx";
import { BrowserRouter, Routes, Route } from "react-router";
import { Provider } from "jotai";
import Questionnaire from "@/pages/QuestionnairePage/components/QuestionnairePage/Questionnaire.tsx";

const App = () => {
    return (
        <Provider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Landing/>} />
                    <Route path="/questionnaire" element={<Questionnaire/>} />
                </Routes>
            </BrowserRouter>
        </Provider>
    );
}

export default App;
