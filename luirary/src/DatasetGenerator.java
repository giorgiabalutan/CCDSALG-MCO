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

            while (tokensSoFar < n)
            {
                if (tokensSoFar % 2 == 0)
                {
                    sb.append(rand.nextInt(9) + 1);
                }
                else
                {
                    sb.append(' ');
                    sb.append(allOps[rand.nextInt(allOps.length)]);
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

        while (writerOk && depth <= 2499)
        {
            StringBuilder sb = new StringBuilder();

            int i = 0;
            while (i < depth)
            {
                sb.append('(');
                i++;
            }

            sb.append(rand.nextInt(9) + 1);

            int level = 0;
            while (level < depth)
            {
                sb.append(' ');
                sb.append(allOps[rand.nextInt(allOps.length)]);
                sb.append(' ');
                sb.append(rand.nextInt(9) + 1);
                sb.append(')');
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
        if (baseLen < 1)
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
