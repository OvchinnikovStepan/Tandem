import Button from "@/ui/Button.tsx";
import {NavLink} from "react-router";


function LandingHeader() {
    return (
        <header className="sticky top-0 z-50 border-b-2 border-header-border bg-accent-white flex" >
            <div className="container flex items-center justify-between max-w-[69rem]">
                <div className="ml-[-0.125rem]">
                    <img
                        src="@/../public/logo/Black_logo.png"
                        alt="Tandem"
                        draggable="false"
                        className="size-[3.875rem] scale-120"
                    />
                </div>
                <div className="flex items-center gap-4">
                    <NavLink to="/login">
                        <Button
                            variant={"secondary"}
                            size={"sm"}
                        >
                            Войти
                        </Button>
                    </NavLink>
                </div>
            </div>
        </header>
    );
}

export default LandingHeader;