package lindelt.either;

import java.util.*;

/**
 * <p>
 * Container holding a concrete instance of one of two types of values. If an
 * {@code Either} holds a left value then {@code isLeft()} returns {@code true}
 * and {@code isRight()} returns {@code false}, and vice-versa if an
 * {@code Either} holds a right value. {@code Either} objects are not permitted
 * to hold {@code null} values.
 * </p>
 * 
 * <p>
 * {@code Either} can be broadly thought of in three ways:
 * </p>
 * 
 * <ol type='1'>
 * <li>A data-focused alternative to error handling that allows
 * non-{@code Throwable} signals and doesn’t unwind the call stack.</li>
 * <li>A rich {@link Optional} that provides contextual information with the
 * empty case.</li>
 * <li>A generalized binary summation type containing equally valid values.</li>
 * </ol>
 * 
 * <p>
 * An {@code Either} used as in points (1) or (2) conventionally has a valid
 * value stored in a right-holding {@code Either} (mnemonic: right is correct).
 * </p>
 * 
 * @author Andrew Bilsborough
 * @since 1.0
 */
public abstract class Either<L, R> {

    /**
     * Creates a new {@code Either} holding a right value.
     * 
     * @param <L>   Type of the non-held left value.
     * @param <R>   Type of the held right value.
     * @param right Right value held by the {@code Either}.
     * @return A new {@code Either} holding a right value.
     * @throws NullPointerException If the provided value is {@code null}.
     */
    public static <L, R> Either<L, R> right(final R right) {
        Objects.requireNonNull(right);
        return new Either<L, R>() {
            @Override
            public boolean isRight() {
                return true;
            }

            @Override
            public R getRight() throws NoSuchElementException {
                return right;
            }

            @Override
            public L getLeft() throws NoSuchElementException {
                throw new NoSuchElementException("Either does not hold a left value");
            }
        };
    }

    /**
     * Creates a new {@code Either} holding a right value.
     * 
     * @param <L>      Type of the non-held left value.
     * @param <R>      Type of the held right value.
     * @param right    Right value held by the {@code Either}.
     * @param leftType For type hinting.
     * @return A new {@code Either} holding a right value.
     * @throws NullPointerException If the provided value is {@code null}.
     */
    public static <L, R> Either<L, R> right(final R right, final Class<L> leftType) throws NullPointerException {
        return Either.right(right);
    }

    /**
     * Creates a new {@code Either} holding a left value.
     * 
     * @param <L>  Type of the held left value.
     * @param <R>  Type of the non-held right value.
     * @param left Left value held by the {@code Either}.
     * @return A new {@code Either} holding a left value.
     * @throws NullPointerException If the provided value is {@code null}.
     */
    public static <L, R> Either<L, R> left(final L left) {
        Objects.requireNonNull(left);
        return new Either<L, R>() {
            @Override
            public boolean isRight() {
                return false;
            }

            @Override
            public R getRight() throws NoSuchElementException {
                throw new NoSuchElementException("Either does not hold a right value");
            }

            @Override
            public L getLeft() throws NoSuchElementException {
                return left;
            }
        };
    }

    /**
     * Creates a new {@code Either} holding a left value.
     * 
     * @param <L>       Type of the held left value.
     * @param <R>       Type of the non-held right value.
     * @param left      Left value held by the {@code Either}.
     * @param rightType For type hinting.
     * @return A new {@code Either} object holding a left value.
     * @throws NullPointerException If the provided value is {@code null}.
     */
    public static <L, R> Either<L, R> left(final L left, final Class<R> rightType) throws NullPointerException {
        return Either.left(left);
    }

    /**
     * Empty constructor for an {@code Either}.
     */
    protected Either() {
    }

    /**
     * Gets whether this {@code Either} holds a right value.
     * 
     * @return {@code true} if this {@code Either} holds a right value,
     *         {@code false} otherwise.
     */
    public abstract boolean isRight();

    /**
     * Gets whether this {@code Either} holds a left value.
     * 
     * @return {@code true} if this {@code Either} holds a left value,
     *         {@code false} otherwise.
     */
    public boolean isLeft() {
        return !isRight();
    }

    /**
     * Gets the right value held by this {@code Either}.
     * 
     * @return The right value held by this {@code Either}.
     * @throws NoSuchElementException If this {@code Either} holds a left value.
     */
    public abstract R getRight() throws NoSuchElementException;

    /**
     * Gets the left value held by this {@code Either}.
     * 
     * @return The left value held by this {@code Either}.
     * @throws NoSuchElementException If this {@code Either} holds a right value.
     */
    public abstract L getLeft() throws NoSuchElementException;

    /**
     * Compares the specified object with this {@code Either} for equality.
     * Returns true if and only if the specified object is also an
     * {@code Either}, both {@code Either} objects are of the same direction
     * (left/right), and the values held by both {@code Either} objects are
     * <em>equal</em>. (Two values {@code v1} and {@code v2} are <em>equal</em>
     * if {@code v1.equals(v2)}).
     * 
     * @param obj The object to be compared for equality with this {@code Either}.
     * @return {@code true} if the specified object is equal to this {@code Either}.
     * @see #hashCode()
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Either<?, ?> other = (Either<?, ?>) obj;
        if (isRight() && other.isRight())
            return getRight().equals(other.getRight());
        if (isLeft() && other.isLeft())
            return getLeft().equals(other.getLeft());
        return false;
    }

    /**
     * <p>
     * Returns the hash code value for this {@code Either}. The hash code
     * of an {@code Either} is defined to be the result of the following
     * calculation:
     * </p>
     * 
     * <pre>{@code
     * int hashCode = 1;
     * hashCode = 31 * hashCode + (isLeft() ? getLeft().hashCode() : 0);
     * hashCode = 31 * hashCode + (isRight() ? getRight().hashCode() : 0);
     * }</pre>
     * 
     * <p>
     * This ensures that {@code a.equals(b)} implies {@code a.hashcode()
     * == b.hashCode()} for any two {@code Either} objects, {@code a} and
     * {@code b}, as required by the general contract of {@link Object#hashCode()}.
     * 
     * @return The hash code value for this {@code Either}.
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isLeft() ? getLeft().hashCode() : 0);
        result = prime * result + (isRight() ? getRight().hashCode() : 0);
        return result;
    }

    /**
     * Returns a string representation of this {@code Either}. The string
     * representation consists of a leading {@code "Either.Left"} or
     * {@code "Either.Right"}, followed by the string representation of the
     * held value in square brackets ({@code "[]"}).
     * 
     * @return A string representation of this {@code Either}.
     */
    @Override
    public String toString() {
        return new StringBuilder().append("Either.")
                .append(isLeft() ? "Left[" : "Right[")
                .append(isLeft() ? getLeft() : getRight())
                .append("]")
                .toString();
    }
}
