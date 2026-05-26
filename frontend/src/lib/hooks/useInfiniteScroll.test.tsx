import { render } from "@testing-library/react";
import { useInfiniteScroll } from "./useInfiniteScroll";

class MockIntersectionObserver {
    public callback: IntersectionObserverCallback;
    public observe = vi.fn();
    public disconnect = vi.fn();
    public unobserve = vi.fn();
    public root: Element | null = null;
    public rootMargin = "";
    public thresholds: ReadonlyArray<number> = [];
    private target: Element | null = null;

    constructor(callback: IntersectionObserverCallback) {
        this.callback = callback;
        this.observe.mockImplementation((target: Element) => {
            this.target = target;
        });
    }

    public trigger(isIntersecting = true) {
        if (!this.target) return;
        this.callback(
            [
                {
                    isIntersecting,
                    target: this.target,
                    intersectionRatio: isIntersecting ? 1 : 0,
                    boundingClientRect: this.target.getBoundingClientRect(),
                    intersectionRect: this.target.getBoundingClientRect(),
                    rootBounds: null,
                    time: Date.now(),
                } as IntersectionObserverEntry,
            ],
            this as unknown as IntersectionObserver,
        );
    }
}

const observers: MockIntersectionObserver[] = [];

beforeEach(() => {
    observers.length = 0;
    vi.stubGlobal(
        "IntersectionObserver",
        vi.fn((callback: IntersectionObserverCallback) => {
            const instance = new MockIntersectionObserver(callback);
            observers.push(instance);
            return instance as unknown as IntersectionObserver;
        }),
    );
});

afterEach(() => {
    vi.unstubAllGlobals();
});

function TestComponent({
    hasMore,
    isLoading,
    enabled = true,
    onLoadMore,
}: {
    hasMore: boolean;
    isLoading: boolean;
    enabled?: boolean;
    onLoadMore: () => void;
}) {
    const { sentinelRef } = useInfiniteScroll({
        hasMore,
        isLoading,
        enabled,
        onLoadMore,
    });

    return <div ref={sentinelRef} data-testid="sentinel" />;
}

describe("useInfiniteScroll", () => {
    it("calls onLoadMore when sentinel intersects", () => {
        const onLoadMore = vi.fn();

        render(
            <TestComponent hasMore isLoading={false} onLoadMore={onLoadMore} />,
        );

        observers[0].trigger(true);
        expect(onLoadMore).toHaveBeenCalledTimes(1);
    });

    it("does not call onLoadMore when loading", () => {
        const onLoadMore = vi.fn();

        render(<TestComponent hasMore isLoading onLoadMore={onLoadMore} />);

        observers[0].trigger(true);
        expect(onLoadMore).not.toHaveBeenCalled();
    });

    it("does not observe when hasMore is false", () => {
        const onLoadMore = vi.fn();

        render(
            <TestComponent
                hasMore={false}
                isLoading={false}
                onLoadMore={onLoadMore}
            />,
        );

        expect(observers.length).toBe(0);
    });

    it("does not observe when disabled", () => {
        const onLoadMore = vi.fn();

        render(
            <TestComponent
                hasMore
                isLoading={false}
                enabled={false}
                onLoadMore={onLoadMore}
            />,
        );

        expect(observers.length).toBe(0);
    });

    it("disconnects observer on unmount", () => {
        const onLoadMore = vi.fn();

        const { unmount } = render(
            <TestComponent hasMore isLoading={false} onLoadMore={onLoadMore} />,
        );

        const instance = observers[0];
        unmount();
        expect(instance.disconnect).toHaveBeenCalledTimes(1);
    });
});
