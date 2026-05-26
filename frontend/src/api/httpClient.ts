type RefreshHandler = () => Promise<boolean>;
type AccessTokenGetter = () => string | null;

type ApiFetchOptions = RequestInit & {
    auth?: boolean;
    retryOnUnauthorized?: boolean;
};

let refreshHandler: RefreshHandler | null = null;
let accessTokenGetter: AccessTokenGetter = () => null;

export function setAuthRefreshHandler(handler: RefreshHandler | null) {
    refreshHandler = handler;
}

export function setAccessTokenGetter(getter: AccessTokenGetter | null) {
    accessTokenGetter = getter ?? (() => null);
}

export async function apiFetch(
    input: RequestInfo | URL,
    options: ApiFetchOptions = {},
): Promise<Response> {
    const {
        auth = false,
        retryOnUnauthorized = true,
        headers,
        credentials,
        ...rest
    } = options;

    const requestHeaders = new Headers(headers);
    const token = accessTokenGetter();
    if (auth && token) {
        requestHeaders.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(input, {
        ...rest,
        headers: requestHeaders,
        credentials: credentials ?? "include",
    });

    if (
        response.status !== 401 ||
        !auth ||
        !retryOnUnauthorized ||
        !refreshHandler
    ) {
        return response;
    }

    const refreshed = await refreshHandler();
    if (!refreshed) {
        return response;
    }

    const retryHeaders = new Headers(headers);
    const newToken = accessTokenGetter();
    if (newToken) {
        retryHeaders.set("Authorization", `Bearer ${newToken}`);
    }

    return fetch(input, {
        ...rest,
        headers: retryHeaders,
        credentials: credentials ?? "include",
    });
}
