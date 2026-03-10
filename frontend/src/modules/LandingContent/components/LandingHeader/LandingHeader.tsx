import { Button } from "@/ui";
import {Link} from "react-router";
import {useTranslation} from "react-i18next";
import {LanguageSwitcher} from "@/components/LanguageSwitcher/LanguageSwitcher";

function LandingHeader() {
    const {t} = useTranslation();

    return (
        <header className="sticky top-0 z-50 border-b-2 border-header-border bg-accent-white flex">
            <nav className="container flex items-center justify-between max-w-276">
                <div className="ml-[-0.125rem]">
                    <img
                        src="/logo/Black_Logo.svg"
                        alt="Tandem Logo"
                        draggable="false"
                        className="size-15.5"
                    />
                </div>
                <div className="flex items-center gap-2 sm:gap-4">
                    <LanguageSwitcher/>
                    <Button asChild variant={"secondary"} size={"sm"}>
                        <Link to="/login">
                            {t("landing.header.login")}
                        </Link>
                    </Button>
                </div>
            </nav>
        </header>
    );
}

export default LandingHeader;