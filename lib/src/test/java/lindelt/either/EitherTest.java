package lindelt.either;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EitherTest {
    private final Either<String, Integer> right = Either.right(10);
    private final Either<String, Integer> left = Either.left("Foo");
    private final Either<String, Integer> secondRight = Either.right(15);
    private final Either<String, Integer> secondLeft = Either.left("Bar");
    private final Function<String, Integer> parseHex = s -> Integer.valueOf(s, 16);
    private final Function<Integer, Integer> assertPositive = i -> { assert i > 0; return i; };
    private final int rightHashCode = 971; // derived through manual calculation
    private final int leftHashCode = 2196443; // derived through manual calculation


    // ##################################################
    // # VERSION 0.1 TESTS
    // ##################################################

    @Test
    @DisplayName("Object equality")
    void testEquals() {
        assertTrue(right.equals(Either.right(10)));
        assertTrue(left.equals(Either.left("Foo")));
        assertFalse(right.equals(left));
        assertFalse(right.equals(secondRight));
        assertFalse(left.equals(right));
        assertFalse(left.equals(secondLeft));
    }

    @Test
    @DisplayName("Left value retrieval")
    void testGetLeft() {
        assertEquals("Foo", left.getLeft());
        assertThrows(NoSuchElementException.class, () -> right.getLeft());
    }

    @Test
    @DisplayName("Right value retrieval")
    void testGetRight() {
        assertEquals(10, right.getRight());
        assertThrows(NoSuchElementException.class, () -> left.getRight());
    }

    @Test
    @DisplayName("Is a left value")
    void testIsLeft() {
        assertTrue(left.isLeft());
        assertFalse(right.isLeft());
    }

    @Test
    @DisplayName("Is a right value")
    void testIsRight() {
        assertTrue(right.isRight());
        assertFalse(left.isRight());
    }

    @Test
    @DisplayName("Hash code generation")
    void testHashCode() {
        // magic numbers derived through manual calculation.
        assertEquals(leftHashCode, left.hashCode());
        assertEquals(rightHashCode, right.hashCode());
    }

    @Test
    @DisplayName("Left creation with null parameter")
    void testLeft() {
        assertThrows(NullPointerException.class, () -> Either.left(null));
    }

    @Test
    @DisplayName("Right creation with null parameter")
    void testRight() {
        assertThrows(NullPointerException.class, () -> Either.right(null));
    }

    @Test
    @DisplayName("String representation")
    void testToString() {
        assertEquals("Either.Right[10]", right.toString());
        assertEquals("Either.Left[Foo]", left.toString());
    }

    // ##################################################
    // # VERSION 0.2 TESTS
    // ##################################################

    @Test
    @DisplayName("Creation from Optional and value")
    void testOfOptionalValue() {
        assertTrue(Either.of(Optional.of(10), "Foo").equals(right));
        assertTrue(Either.of(Optional.empty(), "Foo").equals(left));
        assertThrows(NullPointerException.class, () -> Either.of(null, "Foo"));
        assertThrows(NullPointerException.class, () -> Either.of(Optional.empty(), (Object) null));
    }

    @Test
    @DisplayName("Creation from Optional and Supplier")
    void testOfOptionalSupplier() {
        assertTrue(Either.of(Optional.of(10), () -> "Foo").equals(right));
        assertTrue(Either.of(Optional.empty(), () -> "Foo").equals(left));
        assertThrows(NullPointerException.class, () -> Either.of(Optional.empty(), (Supplier<Object>) null));
    }

    @Test
    @DisplayName("Creation from Supplier storing Exceptions")
    void testOfSupplier() {
        assertTrue(Either.of(() -> parseHex.apply("A")).equals(right));
        assertTrue(NumberFormatException.class.isInstance(Either.of(() -> parseHex.apply("X")).getLeft()));
    }

    @Test
    @DisplayName("Creation from Supplier storing Throwables")
    void testOfChecked() {
        assertTrue(Either.of(() -> parseHex.apply("A")).equals(right));
        assertTrue(AssertionError.class.isInstance(Either.ofChecked(() -> { assert false; return 0; }).getLeft()));
    }

    // ##################################################
    // # VERSION 0.3 TESTS
    // ##################################################

    @Test
    @DisplayName("Left value retrieval with fallback value")
    void testGetLeftOr() {
        assertEquals("Foo", left.getLeftOr("Bar"));
        assertEquals("Bar", right.getLeftOr("Bar"));
    }

    @Test
    @DisplayName("Left value retrieval with fallback Supplier")
    void testGetLeftOrElse() {
        assertEquals("Foo", left.getLeftOrElse(() -> "Bar"));
        assertEquals("Bar", right.getLeftOrElse(() -> "Bar"));
        assertThrows(NullPointerException.class, () -> right.getLeftOrElse(null));
    }

    @Test
    @DisplayName("Left value retrieval with throw from Supplier on failure")
    void testGetLeftOrThrow() {
        assertEquals("Foo", left.getLeftOrThrow(Error::new));
        assertThrows(Error.class, () -> right.getLeftOrThrow(Error::new));
        assertThrows(NullPointerException.class, () -> right.getLeftOrThrow(null));
        assertThrows(NullPointerException.class, () -> right.getLeftOrThrow(() -> null));
    }

    @Test
    @DisplayName("Right value retrieval with fallback value")
    void testGetRightOr() {
        assertEquals(10, right.getRightOr(15));
        assertEquals(15, left.getRightOr(15));
    }

    @Test
    @DisplayName("Right value retrieval with fallback Supplier")
    void testGetRightOrElse() {
        assertEquals(10, right.getRightOrElse(() -> 15));
        assertEquals(15, left.getRightOrElse(() -> 15));
        assertThrows(NullPointerException.class, () -> left.getRightOrElse(null));
    }

    @Test
    @DisplayName("Right value retrieval with throw from Supplier on failure")
    void testGetRightOrThrow() {
        assertEquals(10, right.getRightOrThrow(Error::new));
        assertThrows(Error.class, () -> left.getRightOrThrow(Error::new));
        assertThrows(NullPointerException.class, () -> left.getRightOrThrow(null));
        assertThrows(NullPointerException.class, () -> left.getRightOrThrow(() -> null));
    }

    // ##################################################
    // # VERSION 0.4 TESTS
    // ##################################################

    @Test
    @DisplayName("Casting fold to valid and invalid types")
    void testFoldCast() {
        assertTrue(() -> right.foldCast(Number.class).isPresent());
        assertFalse(() -> left.foldCast(Number.class).isPresent());
        assertThrows(NullPointerException.class, () -> right.foldCast(null));
    }

    @Test
    @DisplayName("Conditional folding")
    void testFoldConditional() {
        Function<Integer, Character> toHexUpper = i -> Character.toUpperCase(Character.forDigit(i, 16));
        Function<String, Character> firstChar = s -> s.charAt(0);
        assertEquals('A', right.fold(firstChar, toHexUpper));
        assertEquals('F', left.fold(firstChar, toHexUpper));
        assertThrows(NullPointerException.class, () -> right.fold(firstChar, null));
        assertThrows(NullPointerException.class, () -> left.fold(null, toHexUpper));
    }

    @Test
    @DisplayName("Optional left value retrieval")
    void testMaybeLeft() {
        assertEquals(Optional.of("Foo"), left.maybeLeft());
        assertEquals(Optional.empty(), right.maybeLeft());
    }

    @Test
    @DisplayName("Optional right value retrieval")
    void testMaybeRight() {
        assertEquals(Optional.of(10), right.maybeRight());
        assertEquals(Optional.empty(), left.maybeRight());
    }

    // ##################################################
    // # VERSION 0.5 TESTS
    // ##################################################

    @Test
    @DisplayName("Mapping onto the left value")
    void testMapLeft() {
        assertEquals(15.0, right.mapRight(x -> x * 1.5).getRight());
        assertEquals("Foo", left.mapRight(x -> x * 1.5).getLeft());
        assertThrows(NullPointerException.class, () -> right.mapRight(null));
    }

    @Test
    @DisplayName("Mapping onto the left value")
    void testMapRight() {
        assertEquals("FOO", left.mapLeft(String::toUpperCase).getLeft());
        assertEquals(10, right.mapLeft(String::toUpperCase).getRight());
        assertThrows(NullPointerException.class, () -> left.mapLeft(null));
    }

    // ##################################################
    // # VERSION 0.6 TESTS
    // ##################################################

    @Test
    @DisplayName("Application with a left value")
    void testApplyLeft() {
        Function<Integer, Function<Integer, Integer>> curryAdd = x -> y -> x + y;
        assertEquals(25, right.applyRight(secondRight.mapRight(curryAdd)).getRight());
        assertEquals("Bar", right.applyRight(secondLeft.mapRight(curryAdd)).getLeft());
        assertEquals("Foo", left.applyRight(secondRight.mapRight(curryAdd)).getLeft());
        assertEquals("Bar", left.applyRight(secondLeft.mapRight(curryAdd)).getLeft());
        assertThrows(NullPointerException.class, () -> right.applyRight(null));
        assertThrows(NullPointerException.class, () -> left.applyRight(null));
    }

    @Test
    @DisplayName("Application with a right value")
    void testApplyRight() {
        Function<String, Function<String, String>> curryConcat = x -> y -> x.concat(y);
        assertEquals("BarFoo", left.applyLeft(secondLeft.mapLeft(curryConcat)).getLeft());
        assertEquals(15, left.applyLeft(secondRight.mapLeft(curryConcat)).getRight());
        assertEquals(10, right.applyLeft(left.mapLeft(curryConcat)).getRight());
        assertEquals(15, right.applyLeft(secondRight.mapLeft(curryConcat)).getRight());
        assertThrows(NullPointerException.class, () -> left.applyLeft(null));
        assertThrows(NullPointerException.class, () -> right.applyLeft(null));
    }

    @Test
    @DisplayName("Flat mapping with a left value")
    void testFlatMapLeft() {
        Function<String, Either<Character, Integer>> charOrCodePoint = s -> {
            Character c = s.charAt(0);
            if (Character.isSurrogate(c)) {
                return Either.right(s.codePointAt(0));
            } else {
                return Either.left(c);
            }
        };

        assertEquals('F', left.flatMapLeft(charOrCodePoint).getLeft());
        assertEquals(10, right.flatMapLeft(charOrCodePoint).getRight());
        assertThrows(NullPointerException.class, () -> left.flatMapLeft(null));
    }

    @Test
    @DisplayName("Flat mapping with a right value")
    void testFlatMapRight() {
        Function<Integer, Either<String, Double>> circleAreaFromRadius = r -> {
            if (r <= 0) {
                return Either.left("Negative radius");
            } else {
                return Either.right(r * r * Math.PI);
            }
        };

        assertEquals(100 * Math.PI, right.flatMapRight(circleAreaFromRadius).getRight());
        assertEquals("Foo", left.flatMapRight(circleAreaFromRadius).getLeft());
        assertThrows(NullPointerException.class, () -> right.flatMapRight(null));
    }

    // ##################################################
    // # VERSION 0.7 TESTS
    // ##################################################

    @Test
    @DisplayName("Conditional consumption")
    void testConsume() {
        List<Integer> rights = new ArrayList<>();
        List<String> lefts = new ArrayList<>();
        Consumer<Integer> rightConsumer = i -> rights.add(i);
        Consumer<String> leftConsumer = s -> lefts.add(s);

        right.consume(leftConsumer, rightConsumer);
        assertEquals(1, rights.size());
        assertEquals(0, lefts.size());
        assertEquals(10, rights.get(0));

        left.consume(leftConsumer, rightConsumer);
        assertEquals(1, rights.size());
        assertEquals(1, lefts.size());
        assertEquals("Foo", lefts.get(0));

        assertThrows(NullPointerException.class, () -> right.consume(leftConsumer, null));
        assertThrows(NullPointerException.class, () -> left.consume(null, rightConsumer));
    }

    @Test
    @DisplayName("Consume left value")
    void testConsumeLeft() {
        List<String> list = new ArrayList<>();
        Consumer<String> consumer = s -> list.add(s);

        right.consumeLeft(consumer);
        assertEquals(0, list.size());

        left.consumeLeft(consumer);
        assertEquals(1, list.size());
        assertEquals("Foo", list.get(0));

        assertThrows(NullPointerException.class, () -> left.consumeLeft(null));
    }

    @Test
    @DisplayName("Consume right value")
    void testConsumeRight() {
        List<Integer> list = new ArrayList<>();
        Consumer<Integer> consumer = i -> list.add(i);

        left.consumeRight(consumer);
        assertEquals(0, list.size());

        right.consumeRight(consumer);
        assertEquals(1, list.size());
        assertEquals(10, list.get(0));

        assertThrows(NullPointerException.class, () -> right.consumeRight(null));
    }

    @Test
    @DisplayName("Stream left value")
    void testStreamLeft() {
        assertEquals(0, right.streamLeft().toArray().length);
        assertEquals(1, left.streamLeft().toArray().length);
        assertEquals("Foo", left.streamLeft().toArray()[0]);
    }

    @Test
    @DisplayName("Stream right value")
    void testStreamRight() {
        assertEquals(0, left.streamRight().toArray().length);
        assertEquals(1, right.streamRight().toArray().length);
        assertEquals(10, right.streamRight().toArray()[0]);
    }

    // ##################################################
    // # VERSION 1.0 TESTS
    // ##################################################

    @Test
    @DisplayName("Lift a function")
    void testLift() {
        assertEquals(0xBABE, Either.lift(parseHex).apply("BABE").getRight());
        assertTrue(NumberFormatException.class.isInstance(Either.lift(parseHex).apply("FEAR").getLeft()));
        assertThrows(NullPointerException.class, () -> Either.lift(null));
        assertThrows(AssertionError.class, () -> Either.lift(assertPositive).apply(-1));
    }

    @Test
    @DisplayName("Lift a checked function")
    void testLiftChecked() {
        assertEquals(1, Either.liftChecked(assertPositive).apply(1).getRight());
        assertTrue(AssertionError.class.isInstance(Either.liftChecked(assertPositive).apply(-1).getLeft()));
        assertThrows(NullPointerException.class, () -> Either.liftChecked(null));
    }

    @Test
    @DisplayName("Perform a calculation using lift, apply, and map")
    void multiMethodTest() {
        Either<Exception, Integer> calculation = Either.lift(parseHex).apply("BABE")
                .applyRight(Either.lift(parseHex).apply("CAFE").mapRight(x -> y -> x + y));
        assertEquals(0x0001_85BC, calculation.getRight());
    }
}
