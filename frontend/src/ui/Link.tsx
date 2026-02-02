import {NavLink, type NavLinkProps} from "react-router";
import {cva, type VariantProps} from "class-variance-authority";
import {cn} from "@/lib/utils.ts";


const linkVariants = cva(
    "text-[0.8125rem] font-roboto font-normal leading-5 underline underline-offset-[3px] transition-all duration-200",
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

function Link({
    children,
    variant,
    className,
    ...props }: NavLinkProps & VariantProps<typeof linkVariants>
    ){
    return (
        <NavLink
            className={cn(linkVariants({ variant, className }))}
            {...props}
        >
            {children}
        </NavLink>
    );
}

export default Link;
