import { Shield, Users, ClipboardList, Sparkles } from "lucide-react";
import { useTranslation } from "react-i18next";
import { type JSX } from "react";

type Feature = {
    icon: JSX.Element;
    titleKey: string;
    descriptionKey: string;
};

function FeaturesBlock() {
    const { t } = useTranslation();

    const features: Feature[] = [
        {
            icon: (
                <Shield className="size-6 md:size-7.5 stroke-heading-black fill-icon-gray" />
            ),
            titleKey: "landing.features.secure.title",
            descriptionKey: "landing.features.secure.description",
        },
        {
            icon: (
                <Users className="size-6 md:size-7.5 stroke-heading-black fill-icon-gray" />
            ),
            titleKey: "landing.features.interests.title",
            descriptionKey: "landing.features.interests.description",
        },
        {
            icon: (
                <ClipboardList className="size-6 md:size-7.5 stroke-heading-black fill-icon-gray" />
            ),
            titleKey: "landing.features.questionnaire.title",
            descriptionKey: "landing.features.questionnaire.description",
        },
        {
            icon: (
                <Sparkles className="size-6 md:size-7.5 stroke-heading-black fill-icon-gray" />
            ),
            titleKey: "landing.features.interface.title",
            descriptionKey: "landing.features.interface.description",
        },
    ];

    return (
        <section className="pb-12 md:pb-20 lg:pb-25 bg-landing-bg">
            <div className="container max-w-276">
                <div className="grid items-center justify-center md:grid-cols-2 gap-10 md:gap-16 lg:gap-24">
                    {features.map((feature) => (
                        <div
                            key={feature.titleKey}
                            className="flex items-center gap-5"
                        >
                            <div>
                                <div className="size-14 md:size-16 bg-accent-white rounded-icon flex items-center justify-center">
                                    {feature.icon}
                                </div>
                            </div>
                            <div className="flex flex-col">
                                <h3 className="text-xl leading-snug md:text-xl md:leading-7 font-roboto font-medium text-heading-black mb-2">
                                    {t(feature.titleKey)}
                                </h3>
                                <p className="text-sm leading-relaxed md:text-md md:leading-6 font-roboto font-normal text-heading-black max-w-95">
                                    {t(feature.descriptionKey)}
                                </p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}

export default FeaturesBlock;
