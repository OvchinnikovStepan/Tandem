import {
    QuestionBlock,
    FeaturesBlock,
    LandingFooter,
    LandingHeader,
    LandingHero,
} from "@/modules/LandingContent";

export default function Landing() {
    return (
        <div className="min-h-screen">
            <LandingHeader />
            <LandingHero />
            <FeaturesBlock />
            <QuestionBlock />
            <LandingFooter />
        </div>
    );
}
