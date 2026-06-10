public class Stack
{
    Token stack[];
    int top = 0;
    int size;

    //Constructor Method for creating the stack with size input
    public Stack(int setSize)
    {
        this.stack = new Token[setSize];
        this.size = setSize;
    }

    //Checks if the stack is empty
    public boolean stackEmpty()
    {
        return (top == 0);
    }

    //Checks if the stack is full
    public boolean stackFull()
    {
        return (top == size);
    }

    //Adds an element to the top of the stack
    public void push(Token element)
    {
        //If the stack is full, throws an exception that can be caught in the main program using catch
        if(this.stackFull()){
            throw new IllegalStateException("Can't push, the stack is currently full");
        }else{
            //Adds the element
            this.stack[top] = element;
            top++;
        }
    }

    //Removes an element from the top of the stack
    public Token pop()
    {
        //If the stack is empty, throws an exception that can be caught in the main program using catch
        if(this.stackEmpty()){
            throw new IllegalStateException("Can't pop, the stack is currently empty");
        }else{
            //Removes and Returns the element
            top--;
            Token ret = this.stack[top];
            this.stack[top] = null;
            return ret;
        }
    }

    //Returns the top element without removing it
    public Token top()
    {
        //If the stack is empty, throws an exception that can be caught in the main program using catch
        if(this.stackEmpty()){
            throw new IllegalStateException("Nothing to return, the stack is currently empty");
        }else{
            //Returns the element
            return this.stack[top-1];
        }
        
    }

    //Prints the elements in the stack from the bottom to the top
    public void show()
    {
        System.out.print("Bottom |");
        for (int i = 0; i < top; i++)
        {
            //System.out.print(this.stack[i]);
            if(this.stack[i].getType() == Token.Type.OPERAND)
            {
                System.out.print(this.stack[i].getOperand());
            }else{
                System.out.print(this.stack[i].getOperator());
            }
            System.out.print("|");
        }
        System.out.println(" Top");
    }
    
    //Prints the elements including empty spaces (which are 0 for integers by default in java)
    public void showWithEmpty()
    {
        System.out.print("Bottom |");
        for (int i = 0; i < size; i++)
        {
            System.out.print(this.stack[i]);
            System.out.print("|");
        }
        System.out.println(" Top");
    }

    public Stack remove(Stack S, int n)
    {
        Stack tempS1 = new Stack(20);
        Queue tempQ1 = new Queue(20);
        Token x1;
        int j;

        j = 0;

        while (j<n-1)
        {
            x1 = S.pop();
            tempS1.push(x1);
            j++;
        }

        S.pop();

        while (!tempS1.stackEmpty())
        {
            x1 = tempS1.pop();
            S.push(x1);
        }

        return S;
    }
}