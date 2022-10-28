package lindelt.either;

import java.util.*;

/**
 * Utility class providing methods for working with {@link Either} objects,
 * primarily for the purposes of partitioning and sorting collections of
 * {@code Either} objects.
 * 
 * @author Andrew Bilsborough
 * @since 0.8
 */
public final class Eithers {

    /**
     * Creates a {@code Comparator} imposing a total ordering on {@code Either}
     * objects containing {@code Comparable} types. When both {@code boolean}
     * parameters are {@code false}, the returned {@code Comparator} imposes
     * a <em>natural ordering</em> upon {@code Either} objects such that
     * Left-holding {@code Either} objects are always <em>less than</em>
     * right-holding {@code Either} objects, and {@code Either} objects with
     * the same directionality are ordered based upon the <em>natural
     * ordering</em> of their held values. The {@code Comparator} will throw a
     * {@link NullPointerException} if it attempts to compare a {@code null}
     * value.
     * 
     * @param <L>              Upper bound for the left type of the {@code Either}
     *                         objects to be compared.
     * @param <R>              Upper bound for the right type of the {@code Either}
     *                         objects to be compared.
     * @param reverseContents  Reverses the ordering of the values within a
     *                         direction if {@code true}.
     * @param reverseDirection Reverses the ordering of the directions if
     *                         {@code true}.
     * @return A {@code Comparator} for ordering {@code Either} objects.
     * @see #nullableComparator(boolean, boolean)
     */
    public static <L extends Comparable<? super L>, R extends Comparable<? super R>>
    Comparator<Either<? extends L, ? extends R>>
    comparator(boolean reverseContents, boolean reverseDirection) {
        int dirMult = reverseDirection ? -1 : 1;
        int valMult = reverseContents ? -1 : 1;
        return (first, second) -> {
            Objects.requireNonNull(first);
            Objects.requireNonNull(second);
            int res;
            if (first.isRight() != second.isRight()) {
                res = dirMult * (first.isRight() ? 1 : -1);
            } else if (first.isRight()) {
                res = valMult * first.getRight().compareTo(second.getRight());
            } else {
                res = valMult * first.getLeft().compareTo(second.getLeft());
            }
            return res;
        };
    }

    /**
     * Creates a {@code Comparator} imposing a total ordering on {@code Either}
     * objects containing {@code Comparable} types. Behaves identically to
     * {@link #comparator(boolean, boolean)} with the exception that {@code null}
     * is treated as a third direction that is <em>less than</em> concrete
     * instances under <em>natural ordering</em>.
     * 
     * @param <L>              Upper bound for the left type of the {@code Either}
     *                         objects to be compared.
     * @param <R>              Upper bound for the right type of the {@code Either}
     *                         objects to be compared.
     * @param reverseContents  Reverses the ordering of the values within a
     *                         direction if {@code true}.
     * @param reverseDirection Reverses the ordering of the directions if
     *                         {@code true}.
     * @return A {@code Comparator} for ordering nullable {@code Either} objects.
     */
    public static <L extends Comparable<? super L>, R extends Comparable<? super R>>
    Comparator<Either<? extends L, ? extends R>>
    nullableComparator(boolean reverseContents, boolean reverseDirection) {
        Comparator<Either<? extends L, ? extends R>> nonNull = comparator(reverseContents, reverseDirection);
        return (first, second) -> {
            int res;
            if (first == null || second == null) {
                if (first == second) {
                    res = 0;
                } else {
                    res = ((reverseDirection ? second : first) == null) ? -1 : 1;
                }
            } else {
                res = nonNull.compare(first, second);
            }
            return res;
        };
    }

    /**
     * Partitions a collection of {@code Either} objects based upon directionality,
     * returning an empty partition if the provided collection is {@code null}.
     * 
     * @param <L>     Left type of the {@code Either} objects to be partitioned.
     * @param <R>     Right type of the {@code Either} objects to be partitioned.
     * @param eithers Collection of {@code Either} objects to be partitioned.
     * @return A {@code Partition} object containing the result of partitioning
     *         the collection of {@code Either} objects into its left and
     *         right values, or an empty partition if
     */
    public static <L, R> Partition<L, R> partition(Collection<Either<L, R>> eithers)
    throws NullPointerException {
        Partition<L, R> res = new Partition<>();
        if (eithers != null) {
            res.nullCount = partitionInto(eithers, res.getLefts(), res.getRights());
        }
        return res;
    }

    /**
     * Partitions a collection of {@code Either} objects into the provided
     * {@code Partition} based upon directionality. Does nothing if the
     * provided collection of {@code Either} objects is {@code null}.
     * 
     * @param <L>       Left type of the {@code Either} objects to be partitioned.
     * @param <R>       Right type of the {@code Either} objects to be partitioned.
     * @param eithers   Collection of {@code Either} objects to be partitioned.
     * @param partition {@code Partition} object to partition the left and
     *                  right values into.
     * @throws NullPointerException If {@code eithers} is not {@code null} and
     *                              {@code partition} is {@code null}.
     */
    public static <L, R> void partitionInto(Collection<Either<L, R>> eithers,
                                            Partition<? super L, ? super R> partition)
    throws NullPointerException {
        if (eithers != null) {
            Objects.requireNonNull(partition);
            partition.nullCount += partitionInto(eithers, partition.getLefts(), partition.getRights());
        }
    }

    /**
     * Partitions a collection of {@code Either} objects into the provided
     * collections based upon directionality and returns the number of
     * {@code null} values encountered. Does nothing if the provided collection
     * of {@code Either} objects is {@code null}. May leave the left or right
     * value collections in an invalid state if the
     * {@link Collection#add(Object)} operation throws on any of the values
     * held by the collection of {@code Either} objects.
     * 
     * @param <L>     Left type of the {@code Either} objects to be partitioned.
     * @param <R>     Right type of the {@code Either} objects to be partitioned.
     * @param eithers Collection of {@code Either} objects to be partitioned.
     * @param lefts   Collection to partition the left values into.
     * @param rights  Collection to partition the right values into.
     * @return The number of {@code null} values in {@code eithers}, or {@code 0}
     *         if {@code eithers} is {@code null}.
     * @throws NullPointerException          If {@code eithers} is not {@code null}
     *                                       and {@code lefts} or {@code rights} are
     *                                       null.
     * @throws UnsupportedOperationException If either {@code lefts} or
     *                                       {@code rights} does not support
     *                                       the {@link Collection#add(Object)}
     *                                       operation.
     * @throws IllegalArgumentException      If some property of a value held by
     *                                       {@code eithers} prevents it from being
     *                                       added to the appropriate collection.
     * @throws IllegalStateException         If a value held by {@code eithers}
     *                                       cannot be added to the appropriate
     *                                       collection due to insertion
     *                                       restrictions.
     */
    public static <L, R> int partitionInto(Collection<Either<L, R>> eithers,
                                           Collection<? super L> lefts,
                                           Collection<? super R> rights)
    throws NullPointerException, UnsupportedOperationException,
           IllegalArgumentException, IllegalStateException
    {
        int res[] = {0};
        if (eithers == null) {
            return res[0];
        }
        Objects.requireNonNull(lefts);
        Objects.requireNonNull(rights);

        eithers.forEach(either -> {
            if (either == null) {
                res[0] += 1;
            } else if (either.isRight()) {
                rights.add(either.getRight());
            } else {
                lefts.add(either.getLeft());
            }
        });
        return res[0];
    }

    /**
     * Class holding directionally-partitioned values from a collection of
     * {@code Either} objects.
     */
    public static class Partition<L, R> {
        private ArrayList<L> leftList;
        private ArrayList<R> rightList;
        private int nullCount;

        /**
         * Creates a {@code Partition} with two empty lists.
         */
        protected Partition() {
            leftList = new ArrayList<>();
            rightList = new ArrayList<>();
            nullCount = 0;
        }

        /**
         * Gets a list containing the left values of the {@code Partition}.
         * 
         * @return A list containing the left values of the {@code Partition}.
         */
        public ArrayList<L> getLefts() {
            return leftList;
        }

        /**
         * Gets a list containing the right values of the {@code partition}.
         * 
         * @return A list containing the right values of the {@code partition}.
         */
        public ArrayList<R> getRights() {
            return rightList;
        }

        /**
         * Gets the number of {@code null} values encountered during the
         * partitioning process.
         * @return The number of {@code null} values encountered during the
         * partitioning process.
         */
        public int getNullCount() {
            return nullCount;
        }
    }
}
