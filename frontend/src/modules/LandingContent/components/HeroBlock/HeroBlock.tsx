import Button from "@/ui/Button.tsx";
import {NavLink} from "react-router";


function HeroBlock() {
    return (
        <section className="py-[6.25rem] bg-landing-bg">
            <div className="container max-w-[69rem]">
                <div className="flex justify-between">
                    <div className="space-y-8">
                        <div className="space-y-4">
                            <h1 className="text-[2.71875rem] leading-12 font-roboto font-bold text-heading-black">
                                Tandem: ваш корпоративный мессенджер для интересного общения
                            </h1>
                            <p className="text-[0.9375rem] font-roboto font-normal leading-6 text-base-black">
                                Работа — это не только задачи.
                                Это люди, с которыми хочется общаться, учиться, расти и весело проводить время.
                            </p>
                        </div>
                        <NavLink to="/login">
                            <Button>
                                Попробовать бесплатно
                            </Button>
                        </NavLink>
                    </div>

                    <div className="w-full">
                        <img
                            src="@/../public/hero/Hero_Image.png"
                            alt="Hero"
                            draggable="false"
                            className="w-[19.375rem] h-[18.75rem] float-right"
                        />
                    </div>
                </div>
            </div>
        </section>
    );
}

export default HeroBlock;