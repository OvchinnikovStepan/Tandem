import { cn } from "./utils";

describe("cn utility", () => {
  it("объединяет classNames", () => {
    expect(cn("foo", "bar")).toBe("foo bar");
  });

  it("обрабатывает условные классы", () => {
    expect(cn("foo", false && "bar", "baz")).toBe("foo baz");
  });

  it("обрабатывает undefined", () => {
    expect(cn("foo", undefined, "bar")).toBe("foo bar");
  });

  it("обрабатывает null", () => {
    expect(cn("foo", null, "bar")).toBe("foo bar");
  });

  it("объединяет tailwind классы корректно", () => {
    expect(cn("px-2 py-1", "px-4")).toBe("py-1 px-4");
  });

  it("мержит конфликтующие tailwind классы", () => {
    expect(cn("text-red-500", "text-blue-500")).toBe("text-blue-500");
  });

  it("обрабатывает массивы", () => {
    expect(cn(["foo", "bar"])).toBe("foo bar");
  });

  it("обрабатывает объекты", () => {
    expect(cn({ foo: true, bar: false, baz: true })).toBe("foo baz");
  });

  it("обрабатывает пустой вызов", () => {
    expect(cn()).toBe("");
  });

  it("обрабатывает смешанные типы", () => {
    expect(cn("foo", ["bar"], { baz: true })).toBe("foo bar baz");
  });

  it("мержит размеры", () => {
    expect(cn("w-4", "w-8")).toBe("w-8");
  });

  it("мержит padding", () => {
    expect(cn("p-2", "p-4")).toBe("p-4");
  });

  it("сохраняет разные направления padding", () => {
    expect(cn("px-2", "py-4")).toBe("px-2 py-4");
  });
});
