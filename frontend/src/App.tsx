import {
    BrowserRouter,
    Navigate,
    Route,
    Routes,
    useNavigate,
} from "react-router";
import { Provider } from "jotai";
import { useAtomValue } from "jotai";
import { AppStubPage } from "@/pages/AppStubPage/AppStubPage.tsx";
import { AppShellLayout } from "@/modules/AppShell";
import { LeftSidebar } from "@/modules/LeftSidebar";
import {
    AuthBootstrap,
    EntryRoute,
    ProtectedRoute,
    PublicOnlyRoute,
    authAtom,
    useAuthActions,
} from "@/modules/Auth";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { lazy } from "react";

const Landing = lazy(() => import("@/pages/LandingPage/Landing"));
const Profile = lazy(() => import("@/pages/ProfilePage/Profile.tsx"));
const Login = lazy(() => import("@/pages/LoginPage/Login"));
const Register = lazy(() => import("@/pages/RegisterPage/Register"));
const Chats = lazy(
    () => import("@/pages/ChatsPage/components/ChatsPage/Chats"),
);
const Feed = lazy(() => import("@/pages/FeedPage/Feed"));
const Friends = lazy(() => import("@/pages/FriendsPage/Friends"));
const Questionnaire = lazy(
    () =>
        import("@/pages/QuestionnairePage/components/QuestionnairePage/Questionnaire"),
);
const ForgotPassword = lazy(
    () => import("@/pages/ForgotPasswordPage/ForgotPassword"),
);

const queryClient = new QueryClient();

function LeftSidebarWithLogout() {
    const navigate = useNavigate();
    const { logout } = useAuthActions();
    const auth = useAtomValue(authAtom);

    const handleLogout = async () => {
        await logout();
        navigate("/", { replace: true });
    };

    const currentUser = auth.user?.name
        ? {
            name: auth.user.name,
        }
        : undefined;

    return <LeftSidebar currentUser={currentUser} onLogout={handleLogout} />;
}

const App = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <ReactQueryDevtools
                buttonPosition="top-left"
                initialIsOpen={false}
            />
            <Provider>
                <AuthBootstrap>
                    <BrowserRouter>
                        <Routes>
                            <Route
                                path="/"
                                element={
                                    <EntryRoute
                                        unauthenticatedElement={<Landing />}
                                    />
                                }
                            />
                            <Route element={<PublicOnlyRoute />}>
                                <Route path="" element={<Landing />} />
                                <Route path="/login" element={<Login />} />
                                <Route
                                    path="/register"
                                    element={<Register />}
                                />
                                <Route
                                    path="/forgot-password"
                                    element={<ForgotPassword />}
                                />
                            </Route>
                            <Route element={<ProtectedRoute />}>
                                <Route
                                    path="/questionnaire"
                                    element={<Questionnaire />}
                                />
                                <Route
                                    element={
                                        <AppShellLayout
                                            leftSidebar={
                                                <LeftSidebarWithLogout />
                                            }
                                        />
                                    }
                                >
                                    <Route
                                        // index
                                        element={
                                            <Navigate to="/feed" replace />
                                        }
                                    />
                                    <Route path="/chats" element={<Chats />} />
                                    <Route path="/feed" element={<Feed />} />
                                    <Route
                                        path="/friends"
                                        element={<Friends />}
                                    />
                                    <Route
                                        path="/profile"
                                        element={<Profile />}
                                    />
                                    <Route
                                        path="/settings"
                                        element={
                                            <AppStubPage
                                                title="Настройки"
                                                description="Заглушка страницы настроек."
                                            />
                                        }
                                    />
                                    <Route
                                        path="/help"
                                        element={
                                            <AppStubPage
                                                title="Помощь"
                                                description="Заглушка страницы помощи."
                                            />
                                        }
                                    />
                                </Route>
                            </Route>
                        </Routes>
                    </BrowserRouter>
                </AuthBootstrap>
            </Provider>
        </QueryClientProvider>
    );
};

export default App;
