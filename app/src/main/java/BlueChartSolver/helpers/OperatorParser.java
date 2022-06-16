package BlueChartSolver.helpers;

import BlueChartSolver.models.operators.Addition;
import BlueChartSolver.models.operators.Multiply;
import BlueChartSolver.models.operators.Operator;
import BlueChartSolver.models.operators.Subtraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class OperatorParser {
    private static final Logger logger = LoggerFactory.getLogger(OperatorParser.class);
    public Operator parse(String text) {
        logger.info("Started. Input: {}", text == null ? null : "\"" + text + "\"");

        Operator result;
        switch (Objects.requireNonNull(text)) {
            case "+":
                logger.trace("This is addition.");
                result = new Addition();
                break;
            case "-":
                logger.trace("This is subtraction.");
                result = new Subtraction();
                break;
            case "*":
                logger.trace("This is multiply.");
                result = new Multiply();
                break;
            default:
                logger.error("Quit. Input did not match any operator.");
                throw new IllegalArgumentException("Invalid text was given. Expected: (+|-|*)");
        }
        logger.info("End. Result: {}", result.getClass());
        return result;
    }
}
