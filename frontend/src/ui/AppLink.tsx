import {cva, type VariantProps} from "class-variance-authority";
import {cn} from "@/lib/utils.ts";

const linkVariants = cva(
    "font-roboto font-normal leading-5 underline underline-offset-[3px] transition-all duration-200",
    {
        variants: {
            variant: {
                default: "text-heading-black decoration-decoration-color hover:decoration-heading-black",
                footer: "text-accent-white decoration-decoration-color hover:decoration-accent-white",
            },
        },
        defaultVariants: {
            variant: "default",
        },
    }
);

function AppLink({
    children,
    variant,
    className,
    asBlank,
    ...props
}: React.ComponentProps<"a"> &
    VariantProps<typeof linkVariants> & {
    asBlank?: boolean
}){
    return (
        <a
            className={cn(linkVariants({ variant, className }))}
            {...asBlank ? {rel: "noreferrer", target: "_blank"} : null}
            {...props}
        >
            {children}
        </a>
    );
}

export default AppLink;
