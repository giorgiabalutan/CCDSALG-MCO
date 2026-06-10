public class Driver
{
    public static void main(String[] args) {
        Token[] eq = new Token[] {
            new Token(1),
            new Token('+'),
            new Token(2)
        };
        Equation equation = new Equation(eq,3);
        
        equation.printEq();
        equation.convertInfixToPrefix();
        System.out.println();
        equation.printEq();
    }
}