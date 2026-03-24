import { describe, expect, it } from "vitest";
import { formatDate } from "./date";

describe("formatDate", () => {
    it("возвращает только цифры при длине до 2 символов", () => {
        expect(formatDate("1")).toBe("1");
        expect(formatDate("12")).toBe("12");
    });

    it("вставляет первый слеш для диапазона от 3 до 4 цифр", () => {
        expect(formatDate("123")).toBe("12/3");
        expect(formatDate("1234")).toBe("12/34");
    });

    it("форматирует полную дату в формате DD/MM/YYYY", () => {
        expect(formatDate("22052001")).toBe("22/05/2001");
    });

    it("игнорирует нецифровые символы", () => {
        expect(formatDate("22a/05-2001")).toBe("22/05/2001");
    });

    it("обрезает всё после 8 цифр", () => {
        expect(formatDate("220520011234")).toBe("22/05/2001");
    });
});
