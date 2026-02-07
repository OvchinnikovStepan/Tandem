import Button from "@/ui/Button.tsx";
import {NavLink} from "react-router";
import {useTranslation} from "react-i18next";

function QuestionBlock() {
    const {t} = useTranslation();

    return (
        <section className="pb-25 bg-landing-bg">
            <div className="container h-70 max-w-276 bg-accent-gray rounded-3xl">
                <div className="py-12 text-center">
                    <h2 className="text-[2.675rem] leading-12 font-roboto font-bold text-heading-black mb-5">
                        {t("landing.qtb.title")}
                    </h2>
                    <p className="text-[0.9375rem] leading-6 font-roboto font-normal text-heading-black mb-5 max-w-101 mx-auto">
                        {t("landing.qtb.description")}
                    </p>
                    <NavLink to="/login">
                        <Button>
                            {t("landing.qtb.button")}
                        </Button>
                    </NavLink>
                </div>
            </div>
        </section>
    );
}

export default QuestionBlock;