import {Shield, Users, ClipboardList, Sparkles } from "lucide-react";

function FeaturesBlock() {
    return (
        <section className="pb-[6.25rem] bg-landing-bg">
            <div className="container max-w-[69rem]">
                <div className="grid md:grid-cols-2 gap-24">
                    {/* Фича1 Безопасность */}
                    <div>
                        <div className="size-16 bg-accent-white rounded-[1.25rem] flex items-center justify-center mr-5 float-left">
                            <Shield  className="size-7.5 stroke-heading-black fill-icon-gray"/>
                        </div>
                        <h3 className="text-[1.375rem] leading-7 font-roboto font-medium text-heading-black mb-2">
                            Безопасная связь
                        </h3>
                        <p className="text-[0.9375rem] leading-6 font-roboto font-normal text-heading-black">
                            Сквозное шифрование для всех ваших чатов.
                        </p>
                    </div>

                    {/* Фича2 Интересы */}
                    <div>
                        <div className="size-16 bg-accent-white rounded-[1.25rem] flex items-center justify-center mr-5 float-left">
                            <Users  className="size-7.5 stroke-heading-black fill-icon-gray"/>
                        </div>
                        <h3 className="text-[1.375rem] leading-7 font-roboto font-medium text-heading-black mb-2">
                            Группы по интересам
                        </h3>
                        <p className="text-[0.9375rem] leading-6 font-roboto font-normal text-heading-black">
                            Создавай, вступай, организуй события — всё в одном приложении.
                        </p>
                    </div>

                    {/* Фича3 Анкета */}
                    <div>
                        <div className="size-16 bg-accent-white rounded-[1.25rem] flex items-center justify-center mr-5 float-left">
                            <ClipboardList  className="size-7.5 stroke-heading-black fill-icon-gray"/>
                        </div>
                        <h3 className="text-[1.375rem] leading-7 font-roboto font-medium text-heading-black mb-2">
                            Интерактивная анкета
                        </h3>
                        <p className="text-[0.9375rem] leading-6 font-roboto font-normal text-heading-black">
                            Заполните небольшую анкету — и познакомьтесь с людьми, которые разделяют ваши увлечения.
                        </p>
                    </div>

                    {/* Фича4 Интерфейс */}
                    <div>
                        <div className="size-16 bg-accent-white rounded-[1.25rem] flex items-center justify-center mr-5 float-left">
                            <Sparkles  className="size-7.5 stroke-heading-black fill-icon-gray"/>
                        </div>
                        <h3 className="text-[1.375rem] leading-7 font-roboto font-medium text-heading-black mb-2">
                            Ничего лишнего
                        </h3>
                        <p className="text-[0.9375rem] leading-6 font-roboto font-normal text-heading-black">
                            Легко освоить с первого запуска — простой и интуитивный интерфейс.
                        </p>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default FeaturesBlock;