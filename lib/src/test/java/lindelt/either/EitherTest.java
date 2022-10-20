package lindelt.either;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EitherTest {
    private final Either<String, Integer> right = Either.right(10);
    private final Either<String, Integer> left = Either.left("Foo");
    final int rightHashCode = 971; // derived through manual calculation
    final int leftHashCode = 2196443; // derived through manual calculation

    // ##################################################
    // # VERSION 0.1 TESTS
    // ##################################################

    @Test
    @DisplayName("Object equality")
    void testEquals() {
        assertTrue(right.equals(Either.right(10)));
        assertTrue(left.equals(Either.left("Foo")));
        assertFalse(right.equals(left));
        assertFalse(right.equals(Either.right(15)));
        assertFalse(left.equals(right));
        assertFalse(left.equals(Either.left("Bar")));
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
        assertTrue(Either.of(() -> Integer.valueOf("A", 16)).equals(right));
        assertTrue(NumberFormatException.class.isInstance(Either.of(() -> Integer.valueOf("X", 16)).getLeft()));
    }

    @Test
    @DisplayName("Creation from Supplier storing Throwables")
    void testOfChecked() {
        assertTrue(Either.of(() -> Integer.valueOf("A", 16)).equals(right));
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
    @DisplayName("Casting fold to invalid type")
    void testFoldCast() {
        assertThrows(ClassCastException.class, () -> right.foldCast(AutoCloseable.class));
        assertThrows(ClassCastException.class, () -> left.foldCast(Number.class));
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
    @DisplayName("Unconditional folding")
    void testFoldUnconditional() {
        assertEquals(10, right.fold(Object::hashCode));
        assertEquals("Foo".hashCode(), left.fold(Object::hashCode));
        assertThrows(ClassCastException.class, () -> right.fold(Thread::getName));
        assertThrows(NullPointerException.class, () -> left.fold(null));
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
    @DisplayName("Unconditional mapping")
    void testMap() {
        assertEquals("10", right.map((Object::toString)).getRight());
        assertEquals("Foo".hashCode(), left.map(Object::hashCode).getLeft());
        assertThrows(ClassCastException.class, () -> right.map(Thread::getName));
        assertThrows(ClassCastException.class, () -> left.map(Thread::getName));
        assertThrows(NullPointerException.class, () -> right.map(null));
        assertThrows(NullPointerException.class, () -> left.map(null));
    }

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

}
