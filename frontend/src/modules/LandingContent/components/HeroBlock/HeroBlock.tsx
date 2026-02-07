import Button from "@/ui/Button.tsx";
import {NavLink} from "react-router";
import {useTranslation} from "react-i18next";

function HeroBlock() {
    const {t} = useTranslation();

    return (
        <section className="py-25 bg-landing-bg">
            <div className="container max-w-276">
                <div className="flex justify-between">
                    <div className="space-y-8">
                        <div className="space-y-4">
                            <h1 className="text-[2.71875rem] leading-12 font-roboto font-bold text-heading-black">
                                {t("landing.hero.title")}
                            </h1>
                            <p className="text-[0.9375rem] font-roboto font-normal leading-6 text-base-black">
                                {t("landing.hero.description")}
                            </p>
                        </div>
                        <NavLink to="/login">
                            <Button>
                                {t("landing.hero.cta")}
                            </Button>
                        </NavLink>
                    </div>
                    <div className="w-full">
                        <img
                            src="/hero/Hero_Image.png"
                            alt="Hero Image"
                            draggable="false"
                            className="w-77.5 h-75 float-right"
                        />
                    </div>
                </div>
            </div>
        </section>
    );
}

export default HeroBlock;