import { z } from "zod";

export const profileSchema = z.object({
    firstName: z
        .string()
        .min(1, "questionnaire.profile.errors.first-name.required")
        .max(50, "questionnaire.profile.errors.first-name.too-long"),
    lastName: z
        .string()
        .min(1, "questionnaire.profile.errors.last-name.required")
        .max(50, "questionnaire.profile.errors.last-name.too-long"),
    city: z.string().max(100).optional(),
    birthDate: z.string().superRefine((value, ctx) => {
        if (!value) return;

        const [day, month, year] = value.split("/").map(Number);

        const date = new Date(year, month - 1, day);

        if (
            date.getFullYear() !== year ||
            date.getMonth() !== month - 1 ||
            date.getDate() !== day
        ) {
            ctx.addIssue({
                code: "custom",
                message: "questionnaire.profile.errors.birth-date.invalid-date",
            });
            return;
        }

        const today = new Date();

        const age =
            today.getFullYear() -
            year -
            (today < new Date(today.getFullYear(), month - 1, day) ? 1 : 0);

        console.log(age);

        if (age < 16 || age > 120) {
            ctx.addIssue({
                code: "custom",
                message: "questionnaire.profile.errors.birth-date.invalid-date",
            });
        }
    }),
    gender: z.enum(["male", "female", "no-select"]).optional(),
});

export type ProfileFormValues = z.infer<typeof profileSchema>;
