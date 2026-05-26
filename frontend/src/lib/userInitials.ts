export function getUserInitials(name: string, maxLength = 2) {
    return name
        .split(" ")
        .filter(Boolean)
        .map((part) => part[0])
        .join("")
        .slice(0, maxLength)
        .toUpperCase();
}
