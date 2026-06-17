public class Equation
{
    Token[] equation;
    //Number of tokens the equations has
    int tokenCount;
    //Set to true the moment any error handler below detects a problem
    boolean hasError = false;
    //Human readable description of the error found, if any
    String errorMessage = "";

    //Constructor Method for creating an equation object
    public Equation(Token[] equation, int tokenCount)
    {
        this.equation = equation;
        this.tokenCount = tokenCount;
    }

    //Returns true if an error was found while checking the syntax of the
    //equation or while evaluating it
    public boolean hasError()
    {
        return this.hasError;
    }

    //Returns a description of the error found, if any
    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    //Runs every syntax check on the equation (mismatched parentheses and
    //malformed expressions). After calling this method, hasError() should
    //be checked before calling convertInfixToPrefix or evaluatePrefix.
    public void checkSyntax()
    {
        checkEmptyExpression();

        if (!this.hasError)
        {
            checkMismatchedParentheses();
        }

        if (!this.hasError)
        {
            checkMalformedExpression();
        }
    }

    //Reports an error if the equation has no tokens at all
    private void checkEmptyExpression()
    {
        if (this.tokenCount == 0)
        {
            this.hasError = true;
            this.errorMessage = "Malformed expression: the expression is empty";
        }
    }

    //Reports an error if the parentheses in the equation are not balanced,
    //e.g. "(3 + 12" (missing closing parenthesis) or "3 + 1)" (unexpected
    //closing parenthesis)
    private void checkMismatchedParentheses()
    {
        int balance = 0;
        int i = 0;

        while (i < this.tokenCount && !this.hasError)
        {
            Token currentToken = this.equation[i];

            if (currentToken.getType() == Token.Type.OPERATOR && currentToken.getOperator() == '(')
            {
                balance++;
            }
            else if (currentToken.getType() == Token.Type.OPERATOR && currentToken.getOperator() == ')')
            {
                balance--;

                if (balance < 0)
                {
                    this.hasError = true;
                    this.errorMessage = "Mismatched parentheses: unexpected ')' at token " + i;
                }
            }

            i++;
        }

        if (!this.hasError && balance > 0)
        {
            this.hasError = true;
            this.errorMessage = "Mismatched parentheses: missing " + balance + " closing parenthesis/parentheses";
        }
    }

    //Reports an error if the sequence of tokens does not form a valid
    //expression, e.g. "2 + * 3" (operator followed by operator),
    //"2 3 + 4" (operand followed by operand), "2 +" (ends with an operator),
    //or "()" (empty parentheses)
    private void checkMalformedExpression()
    {
        TokenCategory previous = TokenCategory.START;
        int i = 0;

        while (i < this.tokenCount && !this.hasError)
        {
            TokenCategory current = categorize(this.equation[i]);

            if (!isValidTransition(previous, current))
            {
                this.hasError = true;
                this.errorMessage = "Malformed expression: unexpected token at position " + i;
            }

            previous = current;
            i++;
        }

        if (!this.hasError && !isValidEnding(previous))
        {
            this.hasError = true;
            this.errorMessage = "Malformed expression: expression ends with an incomplete operator or grouping symbol";
        }
    }

    private TokenCategory categorize(Token t)
    {
        TokenCategory category;

        if (t.getType() == Token.Type.OPERAND)
        {
            category = TokenCategory.OPERAND;
        }
        else if (t.getOperator() == '(')
        {
            category = TokenCategory.OPEN_PARENTHESIS;
        }
        else if (t.getOperator() == ')')
        {
            category = TokenCategory.CLOSE_PARENTHESIS;
        }
        else
        {
            category = TokenCategory.OPERATOR;
        }

        return category;
    }

    //Checks whether "current" is allowed to immediately follow "previous"
    private boolean isValidTransition(TokenCategory previous, TokenCategory current)
    {
        boolean valid;

        if (previous == TokenCategory.START)
        {
            //An expression may only start with an operand or an opening parenthesis
            valid = (current == TokenCategory.OPERAND || current == TokenCategory.OPEN_PARENTHESIS);
        }
        else if (previous == TokenCategory.OPERAND)
        {
            //An operand must be followed by an operator or a closing parenthesis
            valid = (current == TokenCategory.OPERATOR || current == TokenCategory.CLOSE_PARENTHESIS);
        }
        else if (previous == TokenCategory.OPEN_PARENTHESIS)
        {
            //An opening parenthesis must be followed by an operand or another opening parenthesis
            valid = (current == TokenCategory.OPERAND || current == TokenCategory.OPEN_PARENTHESIS);
        }
        else if (previous == TokenCategory.CLOSE_PARENTHESIS)
        {
            //A closing parenthesis must be followed by an operator or another closing parenthesis
            valid = (current == TokenCategory.OPERATOR || current == TokenCategory.CLOSE_PARENTHESIS);
        }
        else
        {
            //An operator must be followed by an operand or an opening parenthesis
            valid = (current == TokenCategory.OPERAND || current == TokenCategory.OPEN_PARENTHESIS);
        }

        return valid;
    }

    private boolean isValidEnding(TokenCategory last)
    {
        return (last == TokenCategory.OPERAND || last == TokenCategory.CLOSE_PARENTHESIS);
    }

    public int convertInfixToPrefix()
    {
        Token[] result = new Token[tokenCount];
        Stack operators = new Stack(tokenCount);
        int index = 0;
        //Iterates through the equation in reverse
        for(int i = tokenCount-1; i > -1; i--)
        {
            Token t = this.equation[i];
            // if(this.equation[i].getType() == Token.Type.OPERAND)
            // {
            //     System.out.println(this.equation[i].getOperand());
            // }else{
            //     System.out.println(this.equation[i].getOperator());
            // }
            // if(!operators.stackEmpty())
            // {
            //     System.out.println(operators.top().getOperator() != ')');
            // }
            if (t.getType() == Token.Type.OPERAND)
            {
                result[index] = t;
                index++;
            }else{
                //Checking for ) instead of ( as ) will appear first with a reversed equation
                if (t.getOperator() == ')')
                {
                    operators.push(t);
                    this.tokenCount--;
                }else if (t.getOperator() == '('){
                    while((!operators.stackEmpty()) && (operators.top().getOperator() != ')')){
                        //operators.show();
                        result[index] = operators.pop();
                        index++;
                    }
                    operators.pop();
                    this.tokenCount--;
                }else{
                    int prec1 = t.getPrecedence();
                    int prec2;
                    if(!operators.stackEmpty()){
                        prec2 = operators.top().getPrecedence();
                        while(!operators.stackEmpty()  && (operators.top().getOperator() != ')') && (prec1 < prec2 || prec1 == prec2 && t.isRightAssociative()))
                        {
                            result[index] = operators.pop();
                            index++;
                        }
                    }
                    operators.push(t);
                }
            }
        }

        while (!operators.stackEmpty())
        {
            result[index] = operators.pop();
            index++;
        }

        for (int i = 0; i < tokenCount; i++)
        {
            this.equation[i] = result[tokenCount-1-i];
        }

        return 0;
    }

    public void printEq()
    {
        for (int i = 0; i < this.tokenCount; i++) {
            if(this.equation[i].getType() == Token.Type.OPERAND)
            {
                System.out.print(this.equation[i].getOperand());
            }else{
                System.out.print(this.equation[i].getOperator());
            }
        }
    }

    public int evaluatePrefix()
    {
        Stack operandTokens = new Stack(tokenCount);
        int i;
        int finalResult;

        for (i = tokenCount-1; i > -1 && !this.hasError; i--)
        {
            Token currToken = this.equation[i];
            if (currToken.getType() == Token.Type.OPERAND)
            {
                operandTokens.push(currToken);
            }
            else
            {
                int operand1 = operandTokens.pop().getOperand();
                int operand2 = operandTokens.pop().getOperand();
                Token combined;

                if (currToken.getOperator() == '+')
                {
                    combined = new Token(operand1 + operand2);
                    operandTokens.push(combined);
                }
                else if (currToken.getOperator() == '-')
                {
                    combined = new Token(operand1 - operand2);
                    operandTokens.push(combined);
                }
                else if (currToken.getOperator() == '*')
                {
                    combined = new Token(operand1 * operand2);
                    operandTokens.push(combined);
                }
                else if (currToken.getOperator() == '/')
                {
                    if (operand2 == 0)
                    {
                        this.hasError = true;
                        this.errorMessage = "Division by zero error";
                        combined = new Token(0);
                    }
                    else
                    {
                        combined = new Token(operand1 / operand2);
                    }
                    operandTokens.push(combined);
                }
                else if (currToken.getOperator() == '%')
                {
                    if (operand2 == 0)
                    {
                        this.hasError = true;
                        this.errorMessage = "Division by zero error (modulo by zero)";
                        combined = new Token(0);
                    }
                    else
                    {
                        combined = new Token(operand1 % operand2);
                    }
                    operandTokens.push(combined);
                }
                else
                {
                    combined = new Token((int)Math.pow(operand1, operand2));
                    operandTokens.push(combined);
                }
            }
        }

        if (this.hasError)
        {
            finalResult = 0;
        }
        else
        {
            finalResult = operandTokens.pop().getOperand();
        }

        return finalResult;
    }

}