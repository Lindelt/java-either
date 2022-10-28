package lindelt.either;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EithersTest {
    private final List<Either<String, Integer>> list = Arrays.asList(
        Either.right(6), Either.left("Foo"),
        Either.right(12), Either.left("Bar")
    );

    // ##################################################
    // # VERSION 0.8 TESTS
    // ##################################################

    @Test
    @DisplayName("Sorting with Comparator")
    void testComparator() {
        List<Either<String, Integer>> sorted = new ArrayList<>(list);

        sorted.sort(Eithers.comparator(false, false));
        assertEquals(Arrays.<Either<String, Integer>>asList(
                Either.left("Bar"), Either.left("Foo"),
                Either.right(6), Either.right(12)), sorted);

        sorted.sort(Eithers.comparator(false, true));
        assertEquals(Arrays.<Either<String, Integer>>asList(
                Either.right(6), Either.right(12),
                Either.left("Bar"), Either.left("Foo")), sorted);

        sorted.sort(Eithers.comparator(true, false));
        assertEquals(Arrays.<Either<String, Integer>>asList(
                Either.left("Foo"), Either.left("Bar"),
                Either.right(12), Either.right(6)), sorted);

        sorted.sort(Eithers.comparator(true, true));
        assertEquals(Arrays.<Either<String, Integer>>asList(
                Either.right(12), Either.right(6),
                Either.left("Foo"), Either.left("Bar")), sorted);

        sorted.add(null);
        assertThrows(NullPointerException.class,
                () -> sorted.sort(Eithers.comparator(false, false)));
    }

    @Test
    @DisplayName("Sorting with Comparator taking null values")
    void testNullableComparator() {
        List<Either<String, Integer>> sorted = new ArrayList<>(list);
        sorted.add(null);

        sorted.sort(Eithers.nullableComparator(false, false));
        assertEquals(Arrays.<Either<String, Integer>>asList(null,
                Either.left("Bar"), Either.left("Foo"),
                Either.right(6), Either.right(12)), sorted);

        sorted.sort(Eithers.nullableComparator(false, true));
        assertEquals(Arrays.<Either<String, Integer>>asList(
                Either.right(6), Either.right(12),
                Either.left("Bar"), Either.left("Foo"),
                null), sorted);

        sorted.sort(Eithers.nullableComparator(true, false));
        assertEquals(Arrays.<Either<String, Integer>>asList(null,
                Either.left("Foo"), Either.left("Bar"),
                Either.right(12), Either.right(6)), sorted);

        sorted.sort(Eithers.nullableComparator(true, true));
        assertEquals(Arrays.<Either<String, Integer>>asList(
                Either.right(12), Either.right(6),
                Either.left("Foo"), Either.left("Bar"),
                null), sorted);
    }

    @Test
    @DisplayName("Create Partition object")
    void testPartition() {
        List<Either<String, Integer>> input = new ArrayList<>(list);
        input.add(null);

        Eithers.Partition<String, Integer> partition = Eithers.partition(input);
        assertEquals(1, partition.getNullCount());
        assertEquals(Arrays.asList("Foo", "Bar"), partition.getLefts());
        assertEquals(Arrays.asList(6, 12), partition.getRights());
    }

    @Test
    @DisplayName("Partition into collections")
    void testPartitionIntoCollections() {
        final List<String> lefts = new ArrayList<>();
        final List<Integer> rights = new ArrayList<>();
        
        final List<Either<String, Integer>> input = new ArrayList<>(list);
        input.add(null);

        assertEquals(1, Eithers.partitionInto(input, lefts, rights));
        assertEquals(lefts, Arrays.asList("Foo", "Bar"));
        assertEquals(rights, Arrays.asList(6, 12));

        assertThrows(NullPointerException.class,
                () -> Eithers.partitionInto(input, null, null));
    }

    @Test
    @DisplayName("Modify Partition object")
    void testPartitionIntoPartition() {
        List<Either<String, Integer>> input = new ArrayList<>(list);
        input.add(null);

        Eithers.Partition<String, Integer> partition = Eithers.partition(null);
        Eithers.partitionInto(input, partition);

        assertEquals(1, partition.getNullCount());
        assertEquals(Arrays.asList("Foo", "Bar"), partition.getLefts());
        assertEquals(Arrays.asList(6, 12), partition.getRights());
        
        assertThrows(NullPointerException.class, () -> Eithers.partitionInto(input, null));
    }
}
