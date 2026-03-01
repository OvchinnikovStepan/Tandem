export default {
    meta: {
        type: "problem",
        docs: {
            description:
                "Disallow raw text in JSX; use localization (e.g. t('key')) instead",
        },
        fixable: "code",
        schema: [],
    },
    create: function (context) {
        function getParentJsxElementName(node) {
            const ancestors = context.sourceCode.getAncestors(node);
            const parent = ancestors[ancestors.length - 1];

            if (!parent || parent.type !== "JSXElement") return null;

            const name = parent.openingElement?.name;
            if (!name) return null;
            // JSXIdentifier = обычный тег (<div>, <Button>)
            if (name.type === "JSXIdentifier") return name.name;
            // JSXMemberExpression = <Foo.Bar>
            if (name.type === "JSXMemberExpression") {
                return context.getSourceCode().getText(name);
            }
            return null;
        }

        return {
            JSXText(node) {
                const value = node.value.trim();
                const tagName = getParentJsxElementName(node);
                if (value.length > 0) {
                    context.report({
                        node: node,
                        message: tagName
                            ? `Необходимо заменить: "${value}" в <{{tagName}}>`
                            : `Необходимо заменить: "${value}"`,
                        data: tagName ? { tagName } : {},
                    });
                }
            },
        };
    },
};
