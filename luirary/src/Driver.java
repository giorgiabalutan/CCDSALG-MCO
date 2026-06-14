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
        String[] testExpressions = new String[] {
            "(1+(2*5)/3)^2*2+1",   //valid expression
            "(3 + 12",             //mismatched parentheses (missing ')')
            "3 + 1)",              //mismatched parentheses (extra ')')
            "23 $ 1",              //invalid character
            "2 + * 3",             //malformed expression (operator after operator)
            "2 3 + 4",             //malformed expression (operand after operand)
            "(2 + 3",              //malformed/mismatched (ends mid-expression)
            "10 / (5 - 5)",        //division by zero
            "20 % (4 - 4)",        //modulo by zero
            "100 / (2 - 7)",       //valid, negative intermediate result
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

    //Tokenizes, validates, converts, and evaluates a single expression,
    //printing the result or an explicit error message describing what
    //went wrong.
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

    //Converts and evaluates an equation that has already passed all syntax
    //checks, printing the prefix form and the final result. Still guards
    //against a division/modulo by zero error, which can only be detected
    //while evaluating.
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
