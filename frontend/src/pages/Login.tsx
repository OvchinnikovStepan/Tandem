import { QuestionBlock } from "@/modules/LoginContent";
import HeroBlock from "@/components/HeroBlock/HeroBlock";
import Header from "@/components/Header/Header";

export default function Login() {
  return (
    <div className="min-h-screen bg-white">
      <Header showClose={false} />
      <div className="flex h-[calc(100vh-73px)]">
        <HeroBlock variant="login" />
        <QuestionBlock />
      </div>
    </div>
  );
}
