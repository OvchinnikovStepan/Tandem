import js from "@eslint/js";
import globals from "globals";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import tseslint from "typescript-eslint";
import { defineConfig, globalIgnores } from "eslint/config";
import eslintConfigPrettier from "eslint-config-prettier/flat";
import noRawJsxText from "./eslint-rules/no-raw-jsx-text.js";
import pluginQuery from "@tanstack/eslint-plugin-query";

export default defineConfig([
    globalIgnores(["dist"]),
    ...pluginQuery.configs["flat/recommended"],
    {
        files: ["**/*.{ts,tsx}"],
        extends: [
            js.configs.recommended,
            tseslint.configs.recommended,
            reactHooks.configs.flat.recommended,
            reactRefresh.configs.vite,
        ],
        languageOptions: {
            ecmaVersion: "latest",
            globals: globals.browser,
        },
        plugins: {
            local: {
                rules: {
                    "no-raw-jsx-text": noRawJsxText,
                },
            },
        },
        rules: {
            "local/no-raw-jsx-text": "error",
        },
    },
    eslintConfigPrettier,
]);
