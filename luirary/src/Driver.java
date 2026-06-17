public class Driver
{
    public static void main(String[] args)
    {
        //A small set of sample expressions, including each of the four
        //error categories required by the project specification:
        //  - mismatched parentheses
        //  - invalid characters/operators
        //  - malformed expressions
        //  - division by zero
        String[] testExpressions = new String[]
                {
            "(1+(2*5)/3)^2*2+1",
            "(3 + 12",
            "3 + 1)",
            "23 $ 1",
            "2 + * 3",
            "2 3 + 4",
            "(2 + 3",
            "10 / (5 - 5)",
            "20 % (4 - 4)",
            "100 / (2 - 7)",
        };

        int i = 0;
        while (i < testExpressions.length)
        {
            System.out.println("Input: " + testExpressions[i]);
            processExpression(testExpressions[i]);
            System.out.println();
            i++;
        }
    }

    public static void processExpression(String expression)
    {
        Tokenizer tokenizer = new Tokenizer();
        Token[] tokens = tokenizer.tokenize(expression);

        if (tokenizer.hasError())
        {
            System.out.println("Error: " + tokenizer.getErrorMessage());
        }
        else
        {
            Equation equation = new Equation(tokens, tokenizer.getTokenCount());
            equation.checkSyntax();

            if (equation.hasError())
            {
                System.out.println("Error: " + equation.getErrorMessage());
            }
            else
            {
                evaluateValidEquation(equation);
            }
        }
    }

    private static void evaluateValidEquation(Equation equation)
    {
        try
        {
            System.out.print("Infix:  ");
            equation.printEq();
            System.out.println();

            equation.convertInfixToPrefix();

            System.out.print("Prefix: ");
            equation.printEq();
            System.out.println();

            int result = equation.evaluatePrefix();

            if (equation.hasError())
            {
                System.out.println("Error: " + equation.getErrorMessage());
            }
            else
            {
                System.out.println("Result: " + result);
            }
        }
        catch (IllegalStateException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
