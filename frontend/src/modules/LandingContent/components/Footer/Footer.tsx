import Link from "@/ui/Link.tsx";
import {FOOTER_LINKS} from "@/modules/LandingContent/constants/constants.ts";
import { useTranslation } from "react-i18next";

function Footer() {
    const { t } = useTranslation();

    return (
        <footer className="bg-heading-black">
            <div className="container max-w-276 py-5">
                <div className="ml-[-0.125rem]">
                    <img
                        src="/logo/White_Logo.svg"
                        alt="Tandem Logo"
                        draggable="false"
                        className="size-15.5"
                    />
                </div>
                <div className="w-full mt-4 mb-2">
                    <h4 className="text-[0.8125rem] font-roboto font-light leading-5 text-white/50">
                        {t("landing.footer.contact")}
                        <Link
                            to="/"
                            variant={"footer"}
                        >
                            tandem@t-bang.ru
                        </Link>
                    </h4>
                </div>
                <div className="table mt-2 mb-4">
                    <ul className="text-[0.8125rem] text-accent-white w-full">
                        {FOOTER_LINKS.map((link) => (
                            <li key={t(link.name)} className="float-left mr-6">
                                <Link
                                    to={link.href}
                                    variant={"footer"}
                                >
                                    {t(link.name)}
                                </Link>
                            </li>
                        ))}
                    </ul>
                </div>
                <div className="border-t border-white/15 pt-4">
                    <p className="text-[0.8125rem] font-roboto font-light leading-5 text-white/50">
                        2025, T-Bang
                    </p>
                </div>
            </div>
        </footer>
    );
}

export default Footer;