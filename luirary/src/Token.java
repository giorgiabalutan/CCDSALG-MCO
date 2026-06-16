public class Token
{
    public enum Type {OPERAND, OPERATOR}
    private Type type;
    private int operand;
    private char operator;
    private int precedence = -1;
    private boolean rightAssociativity = false;

    public Token(int operand)
    {
        type = Type.OPERAND;
        this.operand = operand;
    }

    public Token(char operator)
    {
        type = Type.OPERATOR;
        this.operator = operator;
        switch (operator)
        {
            case '(':
            case ')':
                this.precedence = 4;
            case '^':
                this.precedence = 3;
                rightAssociativity = true;
                break;
            case '*':
            case '/':
            case '%':
                this.precedence = 2;
                break;
            case '+':
            case '-':
                this.precedence = 1;
                break;
            default:
                this.precedence = -2;
        }
    }

    public void setOperand(int operand)
    {
        this.operand = operand;
    }

    public void setOperator(char operator)
    {
        this.operator = operator;
    }

    public Type getType()
    {
        return this.type;
    }

    public int getOperand()
    {
        return this.operand;
    }

    public char getOperator()
    {
        return this.operator;
    }

    public int getPrecedence()
    {
        return this.precedence;
    }

    public boolean isRightAssociative()
    {
        return this.rightAssociativity;
    }
}