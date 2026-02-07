import Button from "@/ui/Button.tsx";
import {NavLink} from "react-router";
import {useTranslation} from "react-i18next";
import {LanguageSwitcher} from "@/components/LanguageSwitcher/LanguageSwitcher";

function LandingHeader() {
    const {t} = useTranslation();

    return (
        <header className="sticky top-0 z-50 border-b-2 border-header-border bg-accent-white flex">
            <div className="container flex items-center justify-between max-w-276">
                <div className="ml-[-0.125rem]">
                    <img
                        src="/logo/Black_Logo.svg"
                        alt="Tandem Logo"
                        draggable="false"
                        className="size-15.5 left"
                    />
                </div>
                <div className="flex items-center gap-4">
                    <LanguageSwitcher/>
                    <NavLink to="/login">
                        <Button
                            variant={"secondary"}
                            size={"sm"}
                        >
                            {t("landing.header.login")}
                        </Button>
                    </NavLink>
                </div>
            </div>
        </header>
    );
}

export default LandingHeader;