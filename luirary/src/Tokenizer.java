//Converts a String expression into an array of Tokens.
//Reports an error if the string contains a character that
//does not belong to the supported token set (digits, whitespace,
//and the operators/grouping symbols + - * / % ^ ( )).
public class Tokenizer
{
    private boolean hasError;
    private String errorMessage;
    private int tokenCount;

    public Tokenizer()
    {
        this.hasError = false;
        this.errorMessage = "";
        this.tokenCount = 0;
    }

    public boolean hasError()
    {
        return this.hasError;
    }

    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    public int getTokenCount()
    {
        return this.tokenCount;
    }

    public Token[] tokenize(String expression)
    {
        Token[] tokens = new Token[expression.length()];
        int length = expression.length();
        int position = 0;
        int count = 0;

        while (position < length && !this.hasError)
        {
            char currentChar = expression.charAt(position);

            if (Character.isWhitespace(currentChar))
            {
                position++;
            }
            else if (Character.isDigit(currentChar))
            {
                int numberStart = position;

                while (position < length && Character.isDigit(expression.charAt(position)))
                {
                    position++;
                }

                int value = Integer.parseInt(expression.substring(numberStart, position));
                tokens[count] = new Token(value);
                count++;
            }
            else if (isSupportedOperator(currentChar))
            {
                tokens[count] = new Token(currentChar);
                count++;
                position++;
            }
            else
            {
                this.hasError = true;
                this.errorMessage = "Invalid character '" + currentChar + "' at position " + position;
            }
        }

        this.tokenCount = count;

        return tokens;
    }
    
    private boolean isSupportedOperator(char c)
    {
        boolean isSupported;

        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '(' || c == ')')
        {
            isSupported = true;
        }
        else
        {
            isSupported = false;
        }

        return isSupported;
    }
}
