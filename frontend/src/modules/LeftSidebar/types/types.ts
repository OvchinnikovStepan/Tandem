import type { ElementType } from "react";

export type NavItem = {
    icon: ElementType;
    label: string;
    path: string;
    badge?: number;
};

export type CurrentUser = {
    name: string;
    avatarUrl?: string;
};
