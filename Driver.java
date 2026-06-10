public class Driver
{
    public static void main(String[] args) {
        Token[] eq = new Token[] {
            new Token(1),
            new Token('+'),
            new Token(2),
            new Token('*'),
            new Token(5)
        };
        Equation equation = new Equation(eq,5);
        
        equation.printEq();
        System.out.println();
        equation.convertInfixToPrefix();
        System.out.println("Prefix equivalent: ");
        equation.printEq();
        System.out.println();

        int result = equation.evaluatePrefix();
        System.out.println("Evaluation result: " + result);
    } 
}