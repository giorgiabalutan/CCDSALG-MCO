public class Equation
{
    Token[] equation;
    //Number of tokens the equations has
    int tokenCount;

    //Constructor Method for creating an equation object
    public Equation(Token[] equation, int tokenCount)
    {
        this.equation = equation;
        this.tokenCount = tokenCount;
    }

    //Return type int
    //Return 0 if successful, other numbers for different error handlers?
    public int convertInfixToPrefix()
    {
        Token[] result = new Token[tokenCount];
        Stack operators = new Stack(tokenCount);
        int index = 0;
        //Iterates through the equation in reverse
        for(int i = tokenCount-1; i > -1; i--)
        {
            Token t = this.equation[i];
            if (t.getType() == Token.Type.OPERAND)
            {
                result[index] = t;
                index++;
            }else{
                //Checking for ) instead of ( as ) will appear first with a reversed equation
                if (t.getOperator() == ')')
                {
                    operators.push(t);
                }else if (t.getOperator() == '('){
                    while(operators.top().getOperator() != ')'){
                        result[index] = operators.pop();
                        index++;
                    }
                    operators.pop();
                }else{
                    int prec1 = t.getPrecedence();
                    int prec2;
                    if(!operators.stackEmpty()){
                        prec2 = operators.top().getPrecedence();
                        while(!operators.stackEmpty() && (prec1 < prec2 || prec1 == prec2 && t.isRightAssociative()))
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
        Token result;

        for (i = tokenCount-1; i > -1; i--)
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
                    combined = new Token(operand1 / operand2);
                    operandTokens.push(combined);
                }
                else if (currToken.getOperator() == '^')
                {
                    combined = new Token((int)Math.pow(operand1, operand2));
                    operandTokens.push(combined);
                }
            }
        }

        return operandTokens.pop().getOperand();
    }

}