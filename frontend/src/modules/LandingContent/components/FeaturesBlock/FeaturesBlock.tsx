import {Shield, Users, ClipboardList, Sparkles} from "lucide-react";
import {useTranslation} from "react-i18next";
import {type JSX} from "react";

type Feature = {
    icon: JSX.Element;
    titleKey: string;
    descriptionKey: string;
};

function FeaturesBlock() {
    const {t} = useTranslation();

    const features: Feature[] = [
        {
            icon: <Shield className="size-7.5 stroke-heading-black fill-icon-gray"/>,
            titleKey: "landing.features.secure.title",
            descriptionKey: "landing.features.secure.description",
        },
        {
            icon: <Users className="size-7.5 stroke-heading-black fill-icon-gray"/>,
            titleKey: "landing.features.interests.title",
            descriptionKey: "landing.features.interests.description",
        },
        {
            icon: <ClipboardList className="size-7.5 stroke-heading-black fill-icon-gray"/>,
            titleKey: "landing.features.questionnaire.title",
            descriptionKey: "landing.features.questionnaire.description",
        },
        {
            icon: <Sparkles className="size-7.5 stroke-heading-black fill-icon-gray"/>,
            titleKey: "landing.features.interface.title",
            descriptionKey: "landing.features.interface.description",
        },
    ];

    return (
        <section className="pb-[6.25rem] bg-landing-bg">
            <div className="container max-w-[69rem]">
                <div className="grid md:grid-cols-2 gap-24">
                    {features.map((feature) => (
                        <div key={feature.titleKey}>
                            <div
                                className="size-16 bg-accent-white rounded-[1.25rem] flex items-center justify-center mr-5 float-left">
                                {feature.icon}
                            </div>
                            <h3 className="text-[1.375rem] leading-7 font-roboto font-medium text-heading-black mb-2">
                                {t(feature.titleKey)}
                            </h3>
                            <p className="text-[0.9375rem] leading-6 font-roboto font-normal text-heading-black">
                                {t(feature.descriptionKey)}
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}

export default FeaturesBlock;