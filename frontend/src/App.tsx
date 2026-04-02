import { BrowserRouter, Routes, Route } from "react-router";
import { Provider } from "jotai";
import Landing from "@/pages/LandingPage/Landing.tsx";
import Questionnaire from "@/pages/QuestionnairePage/components/QuestionnairePage/Questionnaire.tsx";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

const queryClient = new QueryClient();

const App = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <ReactQueryDevtools initialIsOpen={false} />
            <Provider>
                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<Landing />} />
                        <Route
                            path="/questionnaire"
                            element={<Questionnaire />}
                        />
                    </Routes>
                </BrowserRouter>
            </Provider>
        </QueryClientProvider>
    );
};

export default App;
