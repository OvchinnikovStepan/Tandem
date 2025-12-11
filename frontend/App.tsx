import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./src/pages/Login";
import Register from "./src/pages/Register";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
