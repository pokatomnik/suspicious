package tk.pokatomnik.suspicious.utils.matchers;

public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
