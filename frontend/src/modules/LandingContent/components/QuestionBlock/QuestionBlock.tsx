import { Button } from "@/ui";
import {Link} from "react-router";
import {useTranslation} from "react-i18next";

function QuestionBlock() {
    const {t} = useTranslation();
    return (
        <section className="pb-12 md:pb-20 lg:pb-25 bg-landing-bg">
            <div className="container max-w-276">
                <div className="px-4 py-10 md:py-12 text-center bg-accent-gray rounded-3xl flex flex-col gap-5 items-center">
                    <h2 className="text-2xl sm:text-4xl leading-tight md:text-[2.675rem] md:leading-12 font-roboto font-bold text-heading-black">
                        {t("landing.qtb.title")}
                    </h2>
                    <p className="text-sm leading-relaxed md:text-[0.9375rem] md:leading-6 font-roboto font-normal text-heading-black max-w-[35ch] sm:max-w-101 mx-auto">
                        {t("landing.qtb.description")}
                    </p>
                    <Button asChild>
                        <Link to="/login">
                            {t("landing.qtb.button")}
                        </Link>
                    </Button>
                </div>
            </div>
        </section>
    );
}

export default QuestionBlock;