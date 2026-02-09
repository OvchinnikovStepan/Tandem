import Button from "@/ui/Button.tsx";
import {Link} from "react-router";
import {useTranslation} from "react-i18next";

function HeroBlock() {
    const {t} = useTranslation();

    return (
        <section className="bg-landing-bg py-12 md:py-20 lg:py-25">
            <div className="container max-w-276">
                <div className="flex flex-col items-center gap-10 lg:gap-0 md:flex-row md:justify-between">
                    <div className="space-y-8">
                        <div className="space-y-4">
                            <h1 className="text-2xl leading-tight sm:text-3xl md:text-[2.71875rem] md:leading-12 font-roboto font-bold text-heading-black">
                                {t("landing.hero.title")}
                            </h1>
                            <p className="text-sm font-roboto font-normal sm:text-[0.9375rem] md:leading-6 text-base-black">
                                {t("landing.hero.description")}
                            </p>
                        </div>
                        <Button asChild>
                            <Link to="/login">
                                {t("landing.hero.cta")}
                            </Link>
                        </Button>
                    </div>
                    <div className="w-full flex justify-center md:justify-end">
                        <img
                            src="/hero/Hero_Image.png"
                            alt="Hero Image"
                            draggable="false"
                            className="max-w-77.5"
                        />
                    </div>
                </div>
            </div>
        </section>
    );
}

export default HeroBlock;