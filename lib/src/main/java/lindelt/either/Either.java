package lindelt.either;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

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
 * @since 0.1
 */
public abstract class Either<L, R> {

    // ##################################################
    // # CREATION METHODS
    // ##################################################

    /**
     * Creates a new {@code Either} holding a right value.
     * 
     * @param <L>   Type of the non-held left value.
     * @param <R>   Type of the held right value.
     * @param right Right value held by the {@code Either}.
     * @return An {@code Either} holding a right value.
     * @throws NullPointerException If the provided value is {@code null}.
     */
    public static <L, R> Either<L, R> right(final R right) throws NullPointerException {
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
     * @return An {@code Either} holding a right value.
     * @throws NullPointerException If the provided value is {@code null}.
     */
    public static <L, R> Either<L, R> right(final R right, final Class<L> leftType)
    throws NullPointerException {
        return Either.right(right);
    }

    /**
     * Creates a new {@code Either} holding a left value.
     * 
     * @param <L>  Type of the held left value.
     * @param <R>  Type of the non-held right value.
     * @param left Left value held by the {@code Either}.
     * @return An {@code Either} holding a left value.
     * @throws NullPointerException If the provided value is {@code null}.
     */
    public static <L, R> Either<L, R> left(final L left) throws NullPointerException {
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
     * @return An {@code Either} holding a left value.
     * @throws NullPointerException If the provided value is {@code null}.
     */
    public static <L, R> Either<L, R> left(final L left, final Class<R> rightType)
    throws NullPointerException {
        return Either.left(left);
    }

    /**
     * Creates a new {@code Either} using an {@code Optional} and a fallback
     * value.
     * 
     * @param <L>      Type of the left value.
     * @param <R>      Type of the right value.
     * @param maybe    {@code Optional} that may hold a right value.
     * @param fallback Fallback left value.
     * @return A right-holding {@code Either} if the {@code Optional} holds a
     *         value, otherwise a left-holding {@code Either}.
     * @throws NullPointerException If the {@code Optional} is {@code null}, or
     *                              the {@code Optional} is empty and the fallback
     *                              is {@code null}.
     * @since 0.2
     */
    public static <L, R> Either<L, R> of(final Optional<R> maybe, final L fallback)
    throws NullPointerException {
        return maybe.isPresent() ? right(maybe.get()) : left(fallback);
    }

    /**
     * Creates a new {@code Either} using an {@code Optional} and a fallback
     * supplier.
     * 
     * @param <L>      Type of the left value.
     * @param <R>      Type of the right value.
     * @param maybe    {@code Optional} that may hold a right value.
     * @param fallback Fallback {@code Supplier} for a left value.
     * @return A right-holding {@code Either} if the {@code Optional} holds a
     *         value, otherwise a left-holding {@code Either}.
     * @throws NullPointerException If the {@code Optional} is {@code null}, or
     *                              the {@code Optional} is empty and the fallback
     *                              is {@code null} or returns {@code null}.
     * @since 0.2
     */
    public static <L, R> Either<L, R> of(final Optional<R> maybe, final Supplier<L> fallback)
    throws NullPointerException {
        return maybe.isPresent() ? right(maybe.get()) : left(fallback.get());
    }

    /**
     * Creates a new right-holding {@code Either} using the value returned by
     * the {@code Supplier}, or a left-holding {@code Either} containing an
     * {@code Exception} if the {@code Supplier} throws.
     * 
     * @param <R>      Type of the right value.
     * @param supplier {@code Supplier} producing a right value.
     * @return An {@code Either} holding a right value if the {@code Supplier}
     *         successfully returns a non-null value, otherwise a left-holding
     *         {@code Either} containing an {@code Exception}.
     * @since 0.2
     */
    public static <R> Either<Exception, R> of(final Supplier<R> supplier) {
        Either<Exception, R> result;
        try {
            result = right(supplier.get());
        } catch (Exception e) {
            result = left(e);
        }
        return result;
    }

    /**
     * Creates a new right-holding {@code Either} using the value returned by
     * the {@code Supplier}, or a left-holding {@code Either} containing a
     * {@code Throwable} if the {@code Supplier} throws. Similar to
     * {@link #of(Supplier)}, but capable of holding {@code Error} or other
     * throwable types.
     * 
     * @param <R>      Type of the right value.
     * @param supplier {@code Supplier} producing a right value.
     * @return An {@code Either} holding a right value if the {@code Supplier}
     *         successfully returns a non-null value, otherwise a left-holding
     *         {@code Either} containing an {@code Exception}.
     * @since 0.2
     */
    public static <R> Either<Throwable, R> ofChecked(final Supplier<R> supplier) {
        Either<Throwable, R> result;
        try {
            result = right(supplier.get());
        } catch (Throwable e) {
            result = left(e);
        }
        return result;
    }

    /**
     * Empty constructor for an {@code Either}.
     */
    protected Either() {
    }

    // ##################################################
    // # CHECK METHODS
    // ##################################################

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

    // ##################################################
    // # EXTRACTION METHODS
    // ##################################################

    /**
     * Gets the right value held by this {@code Either}.
     * 
     * @return The right value held by this {@code Either}.
     * @throws NoSuchElementException If this {@code Either} holds a left value.
     */
    public abstract R getRight() throws NoSuchElementException;

    /**
     * Gets the right value held by this {@code Either} or a fallback value
     * if this {@code Either} holds a left value.
     * 
     * @param fallback Fallback value to be returned if this {@code Either}
     *                 holds a left value.
     * @return The right value held by this {@code Either} or a fallback.
     * @since 0.3
     */
    public R getRightOr(R fallback) {
        return isRight() ? getRight() : fallback;
    }

    /**
     * Gets the right value held by this {@code Either} or a fallback value
     * if this {@code Either} holds a left value.
     * 
     * @param fallback Fallback supplier to be used if this {@code Either}
     *                 holds a left value.
     * @return The right value held by this {@code Either} or a fallback.
     * @since 0.3
     */
    public R getRightOrElse(Supplier<? extends R> fallback) throws NullPointerException {
        return isRight() ? getRight() : fallback.get();
    }

    /**
     * Gets the right value held by this {@code Either}, or throws a supplied
     * exception if this {@code Either} holds a left value.
     * 
     * @param <X>      Type of the exception to be thrown.
     * @param supplier Supplier for an exception.
     * @return The right value held by this {@code Either}.
     * @throws NullPointerException If this {@code Either} holds a left value
     *                              and the supplier is {@code null}.
     * @throws X                    If this {@code Either} holds a left value.
     * @since 0.3
     */
    public <X extends Throwable> R getRightOrThrow(Supplier<? extends X> supplier)
    throws NullPointerException, X {
        if (isRight()) {
            return getRight();
        }
        throw supplier.get();
    }

    /**
     * Gets an {@code Optional} describing the right value held by this
     * {@code Either}, or an empty {@code Optional} if this {@code Either}
     * holds a left value.
     * 
     * @return An {@code Optional} describing the right value held by this
     *         {@code Either}, or an empty {@code Optional}.
     * @since 0.4
     */
    public Optional<R> maybeRight() {
        return isRight() ? Optional.of(getRight()) : Optional.empty();
    }

    /**
     * Gets the left value held by this {@code Either}.
     * 
     * @return The left value held by this {@code Either}.
     * @throws NoSuchElementException If this {@code Either} holds a right value.
     */
    public abstract L getLeft() throws NoSuchElementException;

    /**
     * Gets the left value held by this {@code Either} or a fallback value
     * if this {@code Either} holds a right value.
     * 
     * @param fallback Fallback value to be returned if this {@code Either}
     *                 holds a right value.
     * @return The left value held by this {@code Either} or a fallback.
     * @since 0.3
     */
    public L getLeftOr(L fallback) {
        return isLeft() ? getLeft() : fallback;
    }

    /**
     * Gets the left value held by this {@code Either} or a fallback value
     * if this {@code Either} holds a right value.
     * 
     * @param fallback Fallback supplier to be used if this {@code Either}
     *                 holds a right value.
     * @return The left value held by this {@code Either} or a fallback.
     * @since 0.3
     */
    public L getLeftOrElse(Supplier<? extends L> fallback) throws NullPointerException {
        return isLeft() ? getLeft() : fallback.get();
    }

    /**
     * Gets the left value held by this {@code Either}, or throws a supplied
     * exception if this {@code Either} holds a right value.
     * 
     * @param <X>      Type of the exception to be thrown.
     * @param supplier Supplier for an exception.
     * @return The left value held by this {@code Either}.
     * @throws NullPointerException If this {@code Either} holds a right value
     *                              and the supplier is {@code null}.
     * @throws X                    If this {@code Either} holds a right value.
     * @since 0.3
     */
    public <X extends Throwable> L getLeftOrThrow(Supplier<? extends X> supplier)
    throws NullPointerException, X {
        if (isLeft()) {
            return getLeft();
        }
        throw supplier.get();
    }

    /**
     * Gets an {@code Optional} describing the left value held by this
     * {@code Either}, or an empty {@code Optional} if this {@code Either}
     * holds a right value.
     * 
     * @return An {@code Optional} describing the left value held by this
     *         {@code Either}, or an empty {@code Optional}.
     * @since 0.4
     */
    public Optional<L> maybeLeft() {
        return isLeft() ? Optional.of(getLeft()) : Optional.empty();
    }

    // ##################################################
    // # MAPPING METHODS
    // ##################################################

    /**
     * Unconditionally maps a function onto the value held by this
     * {@code Either}. Valid calls require {@code T} to be a supertype of
     * both the left and right types of this {@code Either} (i.e. the mapping
     * function must be contravariant to both {@code L} and {@code R}).
     * 
     * @param <T>    Parameter type of the mapping function
     * @param <U>    Return type of the mapping function
     * @param mapper Mapping function to be applied to the value held by this
     *               {@code Either}.
     * @return A left-holding {@code Either} if this {@code Either} holds a left
     *         value, or a right-holding {@code Either} if this {@code Either}
     *         holds a right value. The value held by the returned {@code Either}
     *         is the result of applying the mapping function to the value held
     *         by this {@code Either}.
     * @throws ClassCastException   If the value held by this {@code Either}
     *                              cannot be cast to {@code T}.
     * @throws NullPointerException If the mapping function is {@code null} or
     *                              returns {@code null}.
     */
    public <T, U> Either<U, U> map(Function<? super T, ? extends U> mapper)
    throws ClassCastException, NullPointerException {
        U result = mapper.apply(foldCast());
        return isRight() ? right(result) : left(result);
    }

    /**
     * Gets a right-holding {@code Either} holding the result of applying
     * the given mapping function to the right value held by this {@code Either}
     * (as if by {@link #right(Object)}) if this {@code Either} holds a right
     * value, otherwise gets a left-holding {@code Either} holding the left
     * value of this {@code Either} (as if by {@link #left(Object)}).
     * 
     * @param <T>    Return type of the mapping function.
     * @param mapper Mapping function to apply to the right value held by this
     *               {@code Either}, if this {@code Either} holds a right value.
     * @return A right-holding {@code Either} holding the result of applying
     *         the mapping function to the right value of this {@code Either},
     *         or a left-holding {@code Either} holding the left value of this
     *         {@code Either}.
     * @throws NullPointerException If this {@code Either} holds a right value
     *                              and the mapping function is {@code null}
     *                              or returns {@code null}.
     * @since 0.5
     */
    @SuppressWarnings("unchecked")
    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> mapper) throws NullPointerException {
        return isRight() ? right(mapper.apply(getRight())) : (Either<L, T>) this;
    }

    /**
     * Gets a left-holding {@code Either} holding the result of applying
     * the given mapping function to the left value held by this {@code Either}
     * (as if by {@link #left(Object)}) if this {@code Either} holds a left
     * value, otherwise gets a right-holding {@code Either} holding the right
     * value of this {@code Either} (as if by {@link #right(Object)}).
     * 
     * @param <T>    Return type of the mapping function.
     * @param mapper Mapping function to apply to the left value held by this
     *               {@code Either}, if this {@code Either} holds a left value.
     * @return A left-holding {@code Either} holding the result of applying
     *         the mapping function to the left value of this {@code Either},
     *         or a right-holding {@code Either} holding the right value of this
     *         {@code Either}.
     * @throws NullPointerException If this {@code Either} holds a left value
     *                              and the mapping function is {@code null}
     *                              or returns {@code null}.
     * @since 0.5
     */
    @SuppressWarnings("unchecked")
    public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper) throws NullPointerException {
        return isLeft() ? left(mapper.apply(getLeft())) : (Either<T, R>) this;
    }

    // ##################################################
    // # APPLICATIVE METHODS
    // ##################################################

    /**
     * Gets a right-holding {@code Either} holding the result of mapping the
     * function held by the provided {@code Either} to the value held by this
     * {@code Either} if both {@code Either} objects are right-holding,
     * otherwise gets a left-holding {@code Either} holding either the left
     * value of the provided {@code Either} if it is left-holding, or the left
     * value of this {@code Either} if the provided {@code Either} is
     * right-holding.
     * 
     * @param <T>         Return type of the applicative function.
     * @param applicative An {@code Either} which may hold an applicative
     *                    function.
     * @return A right-holding {@code Either} if both {@code Either} objects
     *         are right-holding, otherwise a left-holding {@code Either}
     *         holding the left value of the provided {@code Either} or this
     *         {@code Either}, in that order.
     * @throws NullPointerException If the provided {@code Either} is
     *                              {@code null}.
     * @since 0.6
     */
    @SuppressWarnings("unchecked")
    public <T> Either<L, T> applyRight(Either<? extends L, Function<? super R, ? extends T>> applicative)
    throws NullPointerException {
        Either<L, T> result;
        if (applicative.isLeft()) {
            result = (Either<L, T>) applicative;
        } else if (isLeft()) {
            result = (Either<L, T>) this;
        } else {
            result = right(applicative.getRight().apply(getRight()));
        }
        return result;
    }

    /**
     * Gets a left-holding {@code Either} holding the result of mapping the
     * function held by the provided {@code Either} to the value held by this
     * {@code Either} if both {@code Either} objects are left-holding,
     * otherwise gets a right-holding {@code Either} holding either the right
     * value of the provided {@code Either} if it is right-holding, or the right
     * value of this {@code Either} if the provided {@code Either} is
     * left-holding.
     * 
     * @param <T>         Return type of the applicative function.
     * @param applicative An {@code Either} which may hold an applicative
     *                    function.
     * @return A left-holding {@code Either} if both {@code Either} objects
     *         are left-holding, otherwise a right-holding {@code Either}
     *         holding the right value of the provided {@code Either} or this
     *         {@code Either}, in that order.
     * @throws NullPointerException If the provided {@code Either} is
     *                              {@code null}.
     * @since 0.6
     */
    @SuppressWarnings("unchecked")
    public <T> Either<T, R> applyLeft(Either<Function<? super L, ? extends T>, ? extends R> applicative)
    throws NullPointerException {
        Either<T, R> result;
        if (applicative.isRight()) {
            result = (Either<T, R>) applicative;
        } else if (isRight()) {
            result = (Either<T, R>) this;
        } else {
            result = left(applicative.getLeft().apply(getLeft()));
        }
        return result;
    }

    // ##################################################
    // # BINDING METHODS
    // ##################################################

    /**
     * Gets the result of applying the mapping function to the right value held
     * by this {@code Either} if this {@code Either} holds a right value,
     * otherwise gets a left-holding {@code Either} containing the left value
     * held by this {@code Either}.
     * 
     * @param <T>    Right type of the {@code Either} produced by the mapping
     *               function.
     * @param mapper Mapping function to apply to the right value held by this
     *               {@code Either}.
     * @return The result of applying the mapping function to the right value
     *         held by this {@code Either}, or a left-holding {@code Either}
     *         containing the left value held by this {@code Either}.
     * @throws NullPointerException If this {@code Either} holds a right value
     *                              and the mapping function is {@code null}.
     * @since 0.6
     */
    @SuppressWarnings("unchecked")
    public <T> Either<L, T> flatMapRight(Function<? super R, ? extends Either<? extends L, ? extends T>> mapper)
    throws NullPointerException {
        return (Either<L, T>) (isRight() ? mapper.apply(getRight()) : this);
    }

    /**
     * Gets the result of applying the mapping function to the left value held
     * by this {@code Either} if this {@code Either} holds a left value,
     * otherwise gets a right-holding {@code Either} containing the right value
     * held by this {@code Either}.
     * 
     * @param <T>    Left type of the {@code Either} produced by the mapping
     *               function.
     * @param mapper Mapping function to apply to the left value held by this
     *               {@code Either}.
     * @return The result of applying the mapping function to the left value
     *         held by this {@code Either}, or a right-holding {@code Either}
     *         containing the right value held by this {@code Either}.
     * @throws NullPointerException If this {@code Either} holds a left value
     *                              and the mapping function is {@code null}.
     * @since 0.6
     */
    @SuppressWarnings("unchecked")
    public <T> Either<T, R> flatMapLeft(Function<? super L, ? extends Either<? extends T, ? extends R>> mapper)
    throws NullPointerException {
        return (Either<T, R>) (isLeft() ? mapper.apply(getLeft()) : this);
    }

    // ##################################################
    // # CONSUMPTION METHODS
    // ##################################################

    /**
     * Unconditionally performs an action using the value held by this
     * {@code Either}. Valid calls require {@code T} to be a supertype of
     * both the left and right types of this {@code Either} (i.e. the consuming
     * function must be contravariant to both {@code L} and {@code R}).
     * 
     * @param <T>      Parameter type of the action to perform.
     * @param consumer Action to perform.
     * @throws ClassCastException   If the value held by this {@code Either}
     *                              cannot be cast to {@code T}.
     * @throws NullPointerException If {@code consumer} is {@code null}.
     * @since 0.7
     */
    public <T> void consume(Consumer<? super T> consumer)
    throws ClassCastException, NullPointerException{
        consumer.accept(this.<T>foldCast());
    }

    /**
     * Conditionally performs an action using the value held by this
     * {@code Either}.
     * 
     * @param leftConsumer  Action to perform if this {@code Either} holds a
     *                      left value.
     * @param rightConsumer Action to perform if this {@code Either} holds a
     *                      right value.
     * @throws NullPointerException If this {@code Either} holds a left value
     *                              and {@code leftConsumer} is {@code null},
     *                              or this {@code Either} holds a right value
     *                              and {@code rightConsumer} is {@code null}.
     * @since 0.7
     */
    public void consume(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer)
    throws NullPointerException {
        if (isRight()) {
            rightConsumer.accept(getRight());
        } else {
            leftConsumer.accept(getLeft());
        }
    }

    /**
     * Performs an action using the right value held by this {@code Either} if
     * this {@code Either} holds a right value, otherwise does nothing.
     * 
     * @param consumer Action to perform.
     * @throws NullPointerException If this {@code Either} holds a right value
     *                              and {@code consumer} is {@code null}.
     * @since 0.7
     */
    public void consumeRight(Consumer<? super R> consumer) throws NullPointerException {
        consume((l) -> {}, consumer);
    }

    /**
     * Performs an action using the left value held by this {@code Either} if
     * this {@code Either} holds a left value, otherwise does nothing.
     * 
     * @param consumer Action to perform.
     * @throws NullPointerException If this {@code Either} holds a left value
     *                              and {@code consumer} is {@code null}.
     * @since 0.7
     */
    public void consumeLeft(Consumer<? super L> consumer) throws NullPointerException {
        consume(consumer, (r) -> {});
    }

    // ##################################################
    // # STREAM EXTRACTION METHODS
    // ##################################################

    /**
     * Creates a {@code Stream} holding the right value held by this
     * {@code Either} if this {@code Either} holds a right value, otherwise
     * creates an empty {@code Stream}.
     * 
     * @return A {@code Stream} containing the right value held by this
     *         {@code Either}, or an empty {@code Stream}.
     * @since 0.7
     */
    public Stream<R> streamRight() {
        Stream.Builder<R> builder = Stream.builder();
        if (isRight()) {
            builder.accept(getRight());
        }
        return builder.build();
    }

    /**
     * Creates a {@code Stream} holding the left value held by this
     * {@code Either} if this {@code Either} holds a left value, otherwise
     * creates an empty {@code Stream}.
     * 
     * @return A {@code Stream} containing the left value held by this
     *         {@code Either}, or an empty {@code Stream}.
     * @since 0.7
     */
    public Stream<L> streamLeft() {
        Stream.Builder<L> builder = Stream.builder();
        if (isLeft()) {
            builder.accept(getLeft());
        }
        return builder.build();
    }

    // ##################################################
    // # FOLDING METHODS
    // ##################################################

    /**
     * Unconditionally folds this {@code Either} into a single value via
     * a folding function. Valid calls require {@code T} be a supertype
     * of both the left and right types of this {@code Either} (i.e. the
     * folding function must be contravariant to both {@code L} and {@code R}).
     * 
     * @param <T>    Parameter type of the folding function.
     * @param <U>    Return type of the folding function.
     * @param folder Folding function to be used.
     * @return The result of applying the folding function to the value held by
     *         this {@code Either}.
     * @throws ClassCastException   If the value held by this {@code Either}
     *                              cannot be cast to {@code T}
     * @throws NullPointerException If the folding function is {@code null}.
     * @since 0.4
     */
    public <T, U> U fold(Function<? super T, ? extends U> folder)
    throws ClassCastException, NullPointerException {
        return folder.apply(foldCast());
    }

    /**
     * Conditionally folds this {@code Either} into a single value via
     * left and right folding functions.
     * 
     * @param <T>         Return type of the folding operation.
     * @param leftFolder  Function used if this {@code Either} holds a left value.
     * @param rightFolder Function used if this {@code Either} holds a right value.
     * @return The result of applying the appropriate folding function to the
     *         value held by this {@code Either}.
     * @throws NullPointerException If the selected folding function is
     *                              {@code null}.
     * @since 0.4
     */
    public <T> T fold(Function<? super L, ? extends T> leftFolder,
                      Function<? super R, ? extends T> rightFolder)
    throws NullPointerException {
        return isRight() ? rightFolder.apply(getRight()) : leftFolder.apply(getLeft());
    }

    /**
     * <p>
     * Unconditionally folds this {@code Either} into a single value via
     * type-cast operation.
     * </p>
     * 
     * <p>
     * This method is intended for internal applications where the result of the
     * cast will be immediately used. For public access or delayed use, see
     * {@link #foldCast(Class)}, which throws a {@link ClassCastException} on
     * illegal casts.
     * </p>
     * 
     * @param <T> Type to cast the value held by this {@code Either} to.
     * @return The value held by this {@code Either} cast to {@code T}.
     * @since 0.4
     */
    @SuppressWarnings("unchecked")
    protected <T> T foldCast() {
        return (T) (isRight() ? getRight() : getLeft());
    }

    /**
     * Unconditionally folds the {@code Either} into a single value via
     * type-cast operation.
     * 
     * @param <T>      Type to cast the value held by this {@code Either} to.
     * @param castType Type to cast the value held by this {@code Either} to.
     * @return The value held by this {@code Either} cast to {@code T}.
     * @throws ClassCastException If the value held by this {@code Either}
     *                            cannot be cast to {@code T}.
     * @since 0.4
     */
    public <T> T foldCast(Class<T> castType) throws ClassCastException, NullPointerException {
        return castType.cast(isRight() ? getRight() : getLeft());
    }

    // ##################################################
    // # INHERITED FROM OBJECT
    // ##################################################

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
