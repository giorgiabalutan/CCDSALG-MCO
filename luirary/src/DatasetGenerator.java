import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class DatasetGenerator
{
    public static void main(String[] args)
    {
        long seed = 42L;

        generateCategory1("dataset_category1.txt", seed);
        generateCategory2("dataset_category2.txt", seed);
        generateCategory3("dataset_category3.txt", seed);
        generateCategory4("dataset_category4.txt", seed);

        System.out.println("All dataset files written successfully.");
    }

    private static void generateCategory1(String filename, long seed)
    {
        char[] allOps = { '+', '-', '*', '/', '%', '^' };
        Random rand   = new Random(seed);
        PrintWriter writer = openWriter(filename);
        boolean writerOk   = (writer != null);
        int n = 5;

        while (writerOk && n <= 10001)
        {
            StringBuilder sb    = new StringBuilder();
            int tokensSoFar     = 0;
            //Tracks how many '^' operators were chosen in a row so chained
            //exponentiation (e.g. 9 ^ 7 ^ 9) cannot blow past what a double
            //can represent. Resets whenever a non-'^' operator is chosen.
            int consecutiveCarets = 0;
            //Counts '^' tokens seen anywhere so far in the expression. Since
            //'^' binds tighter than +/-/% chains and groups right-to-left,
            //long expressions can still indirectly accumulate several '^'
            //results that later get multiplied together; capping the total
            //number of '^' tokens keeps every individual sub-result small.
            int totalCarets = 0;
            int maxCarets = 6;

            while (tokensSoFar < n)
            {
                if (tokensSoFar % 2 == 0)
                {
                    //Right after a '^' operator, keep the operand small so the
                    //chain stays representable (e.g. 9 ^ 2, not 9 ^ 9).
                    if (consecutiveCarets > 0)
                    {
                        sb.append(rand.nextInt(2) + 1);
                    }
                    else
                    {
                        sb.append(rand.nextInt(9) + 1);
                    }
                }
                else
                {
                    char chosenOp = allOps[rand.nextInt(allOps.length)];

                    //Disallow two '^' in a row, and stop offering '^' at all
                    //once the expression already contains several of them.
                    while (chosenOp == '^' && (consecutiveCarets >= 1 || totalCarets >= maxCarets))
                    {
                        chosenOp = allOps[rand.nextInt(allOps.length)];
                    }

                    if (chosenOp == '^')
                    {
                        consecutiveCarets++;
                        totalCarets++;
                    }
                    else
                    {
                        consecutiveCarets = 0;
                    }

                    sb.append(' ');
                    sb.append(chosenOp);
                    sb.append(' ');
                }
                tokensSoFar++;
            }

            writer.println(n + "\t" + sb.toString());
            n += 2;
        }

        if (writerOk)
        {
            writer.close();
            System.out.println("Category 1 written to " + filename);
        }
    }

    private static void generateCategory2(String filename, long seed)
    {
        char[] allOps = { '+', '-', '*', '/', '%', '^' };
        Random rand   = new Random(seed);
        PrintWriter writer = openWriter(filename);
        boolean writerOk   = (writer != null);
        int depth = 1;
        //Expressions in this category are built left-to-right with explicit
        //parentheses around every step (e.g. ((1 + 2) * 3)), so the actual
        //evaluation order exactly matches the order tokens are appended here.
        //This lets us track the running result as a double and reject any
        //operator/operand pair that would push it toward overflow, instead
        //of only special-casing repeated '^'.
        double safeLimit = 1.0e15;

        while (writerOk && depth <= 2499)
        {
            StringBuilder sb = new StringBuilder();

            int i = 0;
            while (i < depth)
            {
                sb.append('(');
                i++;
            }

            int firstOperand = rand.nextInt(9) + 1;
            sb.append(firstOperand);
            double runningValue = firstOperand;

            int level = 0;
            while (level < depth)
            {
                char chosenOp = pickSafeOperator(allOps, rand, runningValue, safeLimit);
                int nextOperand = pickSafeOperand(chosenOp, rand, runningValue, safeLimit);

                sb.append(' ');
                sb.append(chosenOp);
                sb.append(' ');
                sb.append(nextOperand);
                sb.append(')');

                runningValue = applyOperator(runningValue, chosenOp, nextOperand);
                level++;
            }

            int tokenCount = 4 * depth + 1;
            writer.println(tokenCount + "\t" + sb.toString());
            depth++;
        }

        if (writerOk)
        {
            writer.close();
            System.out.println("Category 2 written to " + filename);
        }
    }

    //Chooses an operator at random, but re-rolls if applying it (with any
    //operand the corresponding pickSafeOperand could produce) would risk
    //pushing the running value past safeLimit. Falls back to '+' if no
    //random draw lands on a safe operator within a reasonable number of tries.
    private static char pickSafeOperator(char[] allOps, Random rand, double runningValue, double safeLimit)
    {
        char chosen = '+';
        boolean found = false;
        int attempts = 0;

        while (!found && attempts < 20)
        {
            char candidate = allOps[rand.nextInt(allOps.length)];

            if (isOperatorSafe(candidate, runningValue, safeLimit))
            {
                chosen = candidate;
                found = true;
            }

            attempts++;
        }

        return chosen;
    }

    //Returns true if applying this operator to runningValue cannot push the
    //result past safeLimit, regardless of which small operand (1-9) is used.
    private static boolean isOperatorSafe(char op, double runningValue, double safeLimit)
    {
        boolean safe;
        double magnitude = Math.abs(runningValue);

        if (op == '^')
        {
            //Even a small exponent (e.g. 2) on a large base can overflow,
            //so '^' is only considered safe while the running value is small.
            safe = (magnitude <= 1000.0);
        }
        else if (op == '*')
        {
            //Worst case operand is 9
            safe = (magnitude * 9.0 <= safeLimit);
        }
        else
        {
            //+, -, /, % all shrink or only mildly grow the magnitude
            safe = true;
        }

        return safe;
    }

    //Picks an operand appropriate for the chosen operator. For '^', keeps the
    //exponent small so the result stays representable even on a sizeable base.
    private static int pickSafeOperand(char op, Random rand, double runningValue, double safeLimit)
    {
        int operand;

        if (op == '^')
        {
            double magnitude = Math.abs(runningValue);

            if (magnitude > 1.0)
            {
                //Choose the largest safe exponent (at least 1) so the result
                //stays at or below safeLimit: base^exp <= safeLimit
                double maxExponent = Math.log(safeLimit) / Math.log(magnitude);
                int cap = (int) Math.floor(maxExponent);

                if (cap < 1)
                {
                    cap = 1;
                }
                if (cap > 9)
                {
                    cap = 9;
                }

                operand = rand.nextInt(cap) + 1;
            }
            else
            {
                operand = rand.nextInt(9) + 1;
            }
        }
        else
        {
            operand = rand.nextInt(9) + 1;
        }

        return operand;
    }

    //Applies a single operator/operand pair to the running value, mirroring
    //exactly what Equation.evaluatePrefix would compute for that step.
    private static double applyOperator(double runningValue, char op, int operand)
    {
        double result;

        if (op == '+')
        {
            result = runningValue + operand;
        }
        else if (op == '-')
        {
            result = runningValue - operand;
        }
        else if (op == '*')
        {
            result = runningValue * operand;
        }
        else if (op == '/')
        {
            result = runningValue / operand;
        }
        else if (op == '%')
        {
            result = runningValue % operand;
        }
        else
        {
            result = Math.pow(runningValue, operand);
        }

        return result;
    }

    private static void generateCategory3(String filename, long seed)
    {
        char[] safeOps = { '+', '-', '*', '/' };
        Random rand    = new Random(seed);
        PrintWriter writer = openWriter(filename);
        boolean writerOk   = (writer != null);
        int n = 5;

        while (writerOk && n <= 10001)
        {
            StringBuilder sb = new StringBuilder();
            int tokensSoFar  = 0;

            while (tokensSoFar < n)
            {
                if (tokensSoFar % 2 == 0)
                {
                    sb.append(rand.nextInt(900000) + 100000);
                }
                else
                {
                    int spacesBefore = rand.nextInt(8) + 1;
                    int spacesAfter  = rand.nextInt(8) + 1;
                    int s = 0;

                    while (s < spacesBefore)
                    {
                        sb.append(' ');
                        s++;
                    }

                    sb.append(safeOps[rand.nextInt(safeOps.length)]);
                    s = 0;

                    while (s < spacesAfter)
                    {
                        sb.append(' ');
                        s++;
                    }
                }
                tokensSoFar++;
            }

            writer.println(n + "\t" + sb.toString().trim());

            StringBuilder sbNeg = new StringBuilder();
            int bigNum1   = rand.nextInt(900000) + 100000;
            int smallNum  = rand.nextInt(9) + 1;
            int bigNum2   = rand.nextInt(900000) + 100000;

            sbNeg.append(bigNum1);
            sbNeg.append(" / ( ");
            sbNeg.append(smallNum);
            sbNeg.append(" - ");
            sbNeg.append(bigNum2);
            sbNeg.append(" )");

            int extraTokens = n - 9;
            int added       = 0;

            while (added < extraTokens)
            {
                sbNeg.append(' ');
                sbNeg.append(safeOps[rand.nextInt(safeOps.length)]);
                sbNeg.append(' ');
                sbNeg.append(rand.nextInt(900000) + 100000);
                added += 2;
            }

            writer.println(n + "\t" + sbNeg.toString().trim());
            n += 2;
        }

        if (writerOk)
        {
            writer.close();
            System.out.println("Category 3 written to " + filename);
        }
    }

    private static void generateCategory4(String filename, long seed)
    {
        char[] safeOps  = { '+', '-', '*', '/' };
        char[] badChars = { '$', '@', '#', '!', '&' };
        Random rand     = new Random(seed);
        PrintWriter writer = openWriter(filename);
        boolean writerOk   = (writer != null);
        int n = 4;

        while (writerOk && n <= 10000)
        {
            writer.println(n + "\tBEGIN\t"        + buildBeginError(n, rand, safeOps));
            writer.println(n + "\tMIDDLE\t"       + buildMiddleError(n, rand, safeOps));
            writer.println(n + "\tEND\t"          + buildEndError(n, rand, safeOps));
            writer.println(n + "\tMISMATCH\t"     + buildMismatchError(n, rand, safeOps));
            writer.println(n + "\tINVALID_CHAR\t" + buildInvalidCharError(n, rand, safeOps, badChars));
            n += 2;
        }

        if (writerOk)
        {
            writer.close();
            System.out.println("Category 4 written to " + filename);
        }
    }

    private static String buildBeginError(int n, Random rand, char[] safeOps)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(safeOps[rand.nextInt(safeOps.length)]);
        int tokensAdded = 1;

        while (tokensAdded < n)
        {
            sb.append(' ');
            sb.append(rand.nextInt(9) + 1);
            tokensAdded++;

            if (tokensAdded < n)
            {
                sb.append(' ');
                sb.append(safeOps[rand.nextInt(safeOps.length)]);
                tokensAdded++;
            }
        }

        return sb.toString();
    }

    private static String buildMiddleError(int n, Random rand, char[] safeOps)
    {
        StringBuilder sb = new StringBuilder();

        int half = n / 2;
        if (half % 2 == 0)
        {
            half--;
        }
        if (half < 1)
        {
            half = 1;
        }

        int added = 0;
        while (added < half)
        {
            if (added > 0 && added < half)
            {
                sb.append(' ');
                sb.append(safeOps[rand.nextInt(safeOps.length)]);
                sb.append(' ');
                added++;
            }
            if (added < half)
            {
                sb.append(rand.nextInt(9) + 1);
                added++;
            }
        }

        sb.append(' ');
        sb.append(safeOps[rand.nextInt(safeOps.length)]);
        sb.append(' ');
        sb.append(safeOps[rand.nextInt(safeOps.length)]);

        int remaining  = n - half - 2;
        int tailAdded  = 0;

        while (tailAdded < remaining)
        {
            sb.append(' ');
            sb.append(rand.nextInt(9) + 1);
            tailAdded++;

            if (tailAdded < remaining)
            {
                sb.append(' ');
                sb.append(safeOps[rand.nextInt(safeOps.length)]);
                tailAdded++;
            }
        }

        return sb.toString();
    }

    private static String buildEndError(int n, Random rand, char[] safeOps)
    {
        StringBuilder sb = new StringBuilder();

        int baseLen = n - 1;
        if (baseLen % 2 == 0)
        {
            baseLen--;
        }
        if (baseLen < 1)//
        {
            baseLen = 1;
        }

        int added = 0;
        while (added < baseLen)
        {
            if (added > 0 && added < baseLen)
            {
                sb.append(' ');
                sb.append(safeOps[rand.nextInt(safeOps.length)]);
                sb.append(' ');
                added++;
            }
            if (added < baseLen)
            {
                sb.append(rand.nextInt(9) + 1);
                added++;
            }
        }

        sb.append(' ');
        sb.append(safeOps[rand.nextInt(safeOps.length)]);

        return sb.toString();
    }

    private static String buildMismatchError(int n, Random rand, char[] safeOps)
    {
        StringBuilder sb = new StringBuilder();

        sb.append('(');
        int tokensAdded = 1;

        while (tokensAdded < n)
        {
            if (tokensAdded % 2 == 1)
            {
                sb.append(' ');
                sb.append(rand.nextInt(9) + 1);
                tokensAdded++;
            }
            else
            {
                sb.append(' ');
                sb.append(safeOps[rand.nextInt(safeOps.length)]);
                tokensAdded++;
            }
        }

        return sb.toString();
    }

    private static String buildInvalidCharError(int n, Random rand,
                                                char[] safeOps, char[] badChars)
    {
        StringBuilder sb = new StringBuilder();

        int maxOddIdx = n - 2;
        if (maxOddIdx < 1)
        {
            maxOddIdx = 1;
        }
        int injectAt = rand.nextInt(maxOddIdx) + 1;
        if (injectAt % 2 == 0)
        {
            injectAt++;
        }
        if (injectAt >= n)
        {
            injectAt = 1;
        }

        int tokensSoFar = 0;
        while (tokensSoFar < n)
        {
            if (tokensSoFar % 2 == 0)
            {
                if (tokensSoFar > 0)
                {
                    sb.append(' ');
                }
                sb.append(rand.nextInt(9) + 1);
            }
            else
            {
                sb.append(' ');
                if (tokensSoFar == injectAt)
                {
                    sb.append(badChars[rand.nextInt(badChars.length)]);
                }
                else
                {
                    sb.append(safeOps[rand.nextInt(safeOps.length)]);
                }
                sb.append(' ');
            }
            tokensSoFar++;
        }

        return sb.toString().trim();
    }

    private static PrintWriter openWriter(String filename)
    {
        PrintWriter writer = null;
        boolean done       = false;

        while (!done)
        {
            try
            {
                writer = new PrintWriter(new FileWriter(filename));
                done   = true;
            }
            catch (IOException e)
            {
                System.err.println("Could not open " + filename + ": " + e.getMessage());
                done = true;
            }
        }

        return writer;
    }
}