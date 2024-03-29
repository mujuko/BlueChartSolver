package blue_chart_solver.helpers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static blue_chart_solver.helpers.ReadResult.ReaderState.CLOSE_PARENTHESIS_FOUND;
import static blue_chart_solver.helpers.ReadResult.ReaderState.DIGIT_FOUND;
import static blue_chart_solver.helpers.ReadResult.ReaderState.HAT_FOUND;
import static blue_chart_solver.helpers.ReadResult.ReaderState.MINUS_FOUND;
import static blue_chart_solver.helpers.ReadResult.ReaderState.OPEN_PARENTHESIS_FOUND;
import static blue_chart_solver.helpers.ReadResult.ReaderState.OPERATOR_FOUND;
import static blue_chart_solver.helpers.ReadResult.ReaderState.OTHER;
import static blue_chart_solver.helpers.ReadResult.ReaderState.WHITE_SPACE_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringReaderTest {
    @Nested
    class ProgressiveReadingTest {
        @Test
        void readReadsNextCharacterWhileIncrementingIndex() {
            var reader = new StringReader("-5a(a + b)^10");
            assertEquals(MINUS_FOUND, reader.read().state);
            assertEquals(DIGIT_FOUND, reader.read().state);
            assertEquals(OTHER, reader.read().state);
            assertEquals(OPEN_PARENTHESIS_FOUND, reader.read().state);
            assertEquals(OTHER, reader.read().state);
            assertEquals(WHITE_SPACE_FOUND, reader.read().state);
            assertEquals(OPERATOR_FOUND, reader.read().state);
            assertEquals(WHITE_SPACE_FOUND, reader.read().state);
            assertEquals(OTHER, reader.read().state);
            assertEquals(CLOSE_PARENTHESIS_FOUND, reader.read().state);
            assertEquals(HAT_FOUND, reader.read().state);
            assertEquals(DIGIT_FOUND, reader.read().state);
            assertEquals(DIGIT_FOUND, reader.read().state);

            var result1 = reader.read();
            assertEquals(13, result1.at);
            assertEquals(ReadResult.ReaderState.END_OF_STRING, result1.state);

            // After the end of the string is reached, the increment stops.
            var result2 = reader.read();
            assertEquals(13, result2.at);
            assertEquals(ReadResult.ReaderState.END_OF_STRING, result2.state);
        }

        @Test
        void readBackwardsReadsPreviousCharacterWhileDecrementingIndex() {
            var reader = new StringReader("123");

            // The index is not decremented before starting read.
            var result1 = reader.readBackwards();
            assertEquals(Optional.empty(), result1.found());
            assertEquals(-1, result1.at);
            assertEquals(ReadResult.ReaderState.BEGINNING_OF_STRING, result1.state);

            assertEquals(Optional.of('1'), reader.read().found());
            assertEquals(Optional.of('2'), reader.read().found());
            assertEquals(Optional.of('3'), reader.read().found());
            assertEquals(Optional.of('2'), reader.readBackwards().found());
            assertEquals(Optional.of('1'), reader.readBackwards().found());

            var result2 = reader.readBackwards();
            assertEquals(-1, result2.at);
            assertEquals(ReadResult.ReaderState.BEGINNING_OF_STRING, result2.state);

            // After the beginning of the string is reached, the decrement stops.
            var result3 = reader.readBackwards();
            assertEquals(-1, result3.at);
            assertEquals(ReadResult.ReaderState.BEGINNING_OF_STRING, result3.state);
        }
    }

    @Nested
    class HighSpeedReadingTest {
        private final String testString = "03(1234)5678";
        @Test
        void readUntil() {
            var reader = new StringReader(testString);

            // Reads "03(" and stops because the condition is met.
            var result1 = reader.readUntil(OPEN_PARENTHESIS_FOUND);
            assertEquals(2, result1.at);
            assertEquals(Optional.of('('), result1.found());

            // Reads "1234)" and stops because the condition is met.
            var result2 = reader.readUntil(CLOSE_PARENTHESIS_FOUND);
            assertEquals(7, result2.at);
            assertEquals(Optional.of(')'), result2.found());
        }

        @Test
        void readUntilGoesToEndOfString_whenExpectedConditionNeverMatches() {
            var reader = new StringReader(testString);

            var result = reader.readUntil(HAT_FOUND);
            assertEquals(ReadResult.ReaderState.END_OF_STRING, result.state);
            assertEquals(12, result.at);
            assertEquals(Optional.empty(), result.found());
        }

        @Test
        void readWhile() {
            var reader = new StringReader(testString);

            // Don't move because the next is not match the condition.
            var result0 = reader.readWhile(OPEN_PARENTHESIS_FOUND);
            assertEquals(-1, result0.at);
            assertEquals(Optional.empty(), result0.found());

            // Read "03" and return the final digit = '3'.
            var result1 = reader.readWhile(DIGIT_FOUND);
            assertEquals(1, result1.at);
            assertEquals(Optional.of('3'), result1.found());

            // Don't move because the next is not match the condition.
            var result2 = reader.readWhile(DIGIT_FOUND);
            assertEquals(1, result2.at);
            assertEquals(Optional.of('3'), result2.found());

            reader.read(); // Skip '('.

            // Read "1234" and return the final digit = '4'.
            var result3 = reader.readWhile(DIGIT_FOUND);
            assertEquals(6, result3.at);
            assertEquals(Optional.of('4'), result3.found());

            // Don't move because the next is not match the condition.
            var result4 = reader.readWhile(DIGIT_FOUND);
            assertEquals(6, result4.at);
            assertEquals(Optional.of('4'), result4.found());

            reader.read(); // Skip ')'.

            // Read "5678" and return the final digit = '8'.
            var result5 = reader.readWhile(DIGIT_FOUND);
            assertEquals(11, result5.at);
            assertEquals(Optional.of('8'), result5.found());
        }

        @Test
        void skipWhiteSpace() {
            var reader = new StringReader("a     b");
            reader.read();

            var result = reader.skipWhiteSpace();
            assertTrue(reader.isIn(OTHER));
            assertEquals(6, result.at);
            assertEquals(Optional.of('b'), result.found());
        }
    }

    @Nested
    class MarkingTest {
        @Test
        void markLeft() {
            var reader = new StringReader("hello world!"); // "←hello world"
            reader.read(); // 'h'
            reader.markLeft(); // put a mark on the left side of 'h'.
            reader.read(); // 'e'
            reader.read(); // 'l'
            reader.read(); // 'l'
            reader.read(); // 'o'
            reader.read(); // ' '
            reader.markLeft(); // put a mark on the left side of ' '.
            assertEquals("hello", reader.getMarked());
        }

        @Test
        void markRight() {
            var reader = new StringReader("hello world!");
            reader.markRight(); // put a mark on the right side of '' (= start position).
            reader.read(); // 'h'
            reader.read(); // 'e'
            reader.read(); // 'l'
            reader.read(); // 'l'
            reader.read(); // 'o'
            reader.markRight(); // put a mark on the right side of 'o'.
            assertEquals("hello", reader.getMarked());
        }

        @Test
        void throwsIllegalStateException_whenTextIsNotMarkedEnough() {
            var reader = new StringReader("hello world!");
            assertThrows(IllegalStateException.class, reader::getMarked);

            reader.markRight();
            assertThrows(IllegalStateException.class, reader::getMarked);

            reader.read();
            reader.markRight();
            assertEquals("h", reader.getMarked());
        }

        @Test
        void usedMarksIsDeletedWhenGetMarkedIsCalled() {
            var reader = new StringReader("hello world!");
            reader.markRight();
            reader.read();
            reader.markRight();
            assertEquals("h", reader.getMarked());
            assertThrows(IllegalStateException.class, reader::getMarked);
        }
    }

    @Test
    void stateJudgement() {
        var reader = new StringReader("1");
        reader.read();
        assertTrue(reader.isIn(DIGIT_FOUND));
        assertFalse(reader.isNotIn(DIGIT_FOUND));

        assertFalse(reader.isIn(HAT_FOUND));
        assertTrue(reader.isNotIn(HAT_FOUND));

        assertTrue(reader.isInAny(DIGIT_FOUND, HAT_FOUND));
        assertFalse(reader.isInAny(WHITE_SPACE_FOUND, HAT_FOUND));
    }
}
