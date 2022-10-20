package lindelt.either;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EitherTest {
    private final Either<String, Integer> left = Either.left("Foo");
    private final Either<String, Integer> right = Either.right(10);

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
}
