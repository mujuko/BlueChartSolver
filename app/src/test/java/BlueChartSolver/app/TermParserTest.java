package BlueChartSolver.app;

import BlueChartSolver.app.Parser.TermParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TermParserTest {
    private static final TermParser parser = new TermParser();

//    @Test
//    public void test() {
//        assertTrue(parser.canParse("10"));
//        assertTrue(parser.canParse("-5"));
//        assertTrue(parser.canParse("-10"));
//        assertTrue(parser.canParse("-10abc"));
//        assertTrue(parser.canParse("-1abc"));
//        assertTrue(parser.canParse("-10a^5"));
//        assertTrue(parser.canParse("-10a^5b^10"));
//        assertTrue(parser.canParse("a"));
//        assertTrue(parser.canParse("abc"));
//        assertTrue(parser.canParse("a^5"));
//        assertTrue(parser.canParse("-a^5"));
//        assertTrue(parser.canParse("a^5b^10"));
//
//        assertFalse(parser.canParse("1*2+3"));
//        assertFalse(parser.canParse("a^b-5^c"));
//        assertFalse(parser.canParse("--10"));
//        assertFalse(parser.canParse("-0"));
//        assertFalse(parser.canParse("-a^0123"));
//        assertFalse(parser.canParse("a^2b5"));
//    }

    @Test
    public void parseInt() {
        assertEquals(PolynomialFunction.from(1), parser.parse("1"));
        assertEquals(PolynomialFunction.from(999), parser.parse("999"));
        assertEquals(PolynomialFunction.from(-5), parser.parse("-5"));
    }

    @Test
    public void parseVariable() {
        var x = Variable.named('x');
        assertEquals(PolynomialFunction.from(x), parser.parse("x"));

        var h = Variable.named('h');
        var o = Variable.named('o');
        var g = Variable.named('g');
        var e = Variable.named('e');
        assertEquals(h.times(o).times(g).times(e), parser.parse("hoge"));
    }

    @Test
    public void parseVariableWithCoefficient() {
        var x = Variable.named('x');
        assertEquals(x.times(5), parser.parse("5x"));
        assertEquals(x.times(50), parser.parse("50x"));

        assertEquals(x.times(-1), parser.parse("-x"));
        assertEquals(x.times(-1), parser.parse("-1x"));
        assertEquals(x.times(-500), parser.parse("-500x"));

        var y = Variable.named('y');
        assertEquals(x.times(y).times(3), parser.parse("3xy"));
        assertEquals(x.times(y).times(-10), parser.parse("-10xy"));
    }

    @Test
    public void parseVariableWithExponent() {
        var x = Variable.named('x');
        assertEquals(x.powerOf(2), parser.parse("x^2"));

        var y = Variable.named('y');
        assertEquals(x.powerOf(2).times(y), parser.parse("x^2y"));

        var z = Variable.named('z');
        assertEquals(x.powerOf(2).times(y).times(z.powerOf(99)), parser.parse("x^2yz^99"));
    }
}