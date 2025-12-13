import {NavLink} from "react-router";

function Footer() {
    const footerLinks = [
        {name: "Почта", href: "/"},
        {name: "ВКонтакте", href: "/"},
        {name: "Телеграм", href: "/"},
        {name: "Оферта", href: "/"},
        {name: "Сведения об организации", href: "/"},
        {name: "Политика обработки персональных данных", href: "/"}
    ];

    return (
        <footer className="bg-heading-black">
            <div className="container max-w-[69rem] py-5">
                <div className="ml-[-0.125rem]">
                    <img
                        src="@/../public/logo/White_logo.png"
                        alt="Tandem"
                        draggable="false"
                        className="size-[3.875rem] scale-120"
                    />
                </div>
                <div className="w-full  mt-4 mb-2">
                    <h4 className="text-[0.8125rem] font-roboto font-light leading-5 text-white/50">
                        По всем вопросам обращайтесь на&nbsp;
                        <NavLink
                            to="/"
                            className="text-[0.8125rem] font-roboto font-normal leading-5 text-accent-white
                            underline underline-offset-[3px] decoration-decoration-color
                            hover:decoration-accent-white transition-all duration-200"
                        >
                            tandem@t-bang.ru
                        </NavLink>
                    </h4>
                </div>
                <div className="w-[100%] table mt-2 mb-4">
                    <ul className="text-[0.8125rem] text-accent-white w-full">
                        {footerLinks.map((link) => (
                            <li className="float-left mr-6">
                                <NavLink
                                    to={link.href}
                                    className="text-[0.8125rem] font-roboto font-normal leading-5 text-accent-white
                                    underline underline-offset-[3px] decoration-decoration-color
                                    hover:decoration-accent-white transition-all duration-200"
                                >
                                    {link.name}
                                </NavLink>
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