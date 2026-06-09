public class Queue
{
    Token queue[];
    int head = 0;
    int tail = 0;
    int size;

    //Constructor Method for creating the queue with size input
    public Queue(int setSize)
    {
        this.queue = new Token[setSize];
        this.size = setSize;
    }

    //Checks if the queue is empty
    public boolean queueEmpty()
    {
        return (head == tail);
    }

    //Checks if the queue is full
    public boolean queueFull()
    {
        return (head == (tail+1)%size);
    }

    //Enqueues an element to the tail of the queue
    public void enqueue(Token element)
    {
        //Throws an exception if it's full
        if(this.queueFull()){
            throw new IllegalStateException("Can't enqueue, the queue is currently full");
        }else{
            //Enqueues the element
            this.queue[tail] = element;
            tail = (tail+1)%size;
        }
    }

    //Dequeues an element from the head of the queue and returns it
    public Token dequeue()
    {
        //Throws an exception if it's empty
        if(this.queueEmpty()){
            throw new IllegalStateException("Can't dequeue, the queue is currently empty");
        }else{
            //Dequeues and returns the element
            Token ret = this.queue[head];
            this.queue[head] = null;
            head++;
            return ret;
        }
    }

    //Returns the value at the Head of the queue
    public Token queueHead()
    {
        //Throws an exception if the queue is empty
        if(this.queueEmpty()){
            throw new IllegalStateException("Can't dequeue, the queue is currently empty");
        }else{
            //Returns the element
            return this.queue[head];
        }
    }

    //Returns the value at the Tail of the queue
    public Token queueTail()
    {
        //Throws an exception if the queue is empty
        if(this.queueEmpty()){
            throw new IllegalStateException("Can't dequeue, the queue is currently empty");
        }else{
            //Returns the element
            return this.queue[tail-1];
        }
    }

    //Prints the queue as it would appear in an array
    public void show()
    {
        System.out.print("|");
        for (int i = 0; i < size; i++)
        {
            System.out.print(this.queue[i]);
            System.out.print("|");
        }
        System.out.println("");
    }

    //Prints the queue adjusted to start printing at the head and end at the tail
    public void showAdjusted()
    {
        System.out.print("Head |");
        if(head < tail){
            for (int i = head; i < tail; i++)
            {
                System.out.print(this.queue[i]);
                System.out.print("|");
            }
        }else{
            for (int i = head; i < size; i++)
            {
                System.out.print(this.queue[i]);
                System.out.print("|");
            }
            for (int i = 0; i < tail; i++){
                System.out.print(this.queue[i]);
                System.out.print("|");
            }
        }
        System.out.println(" Tail");
    }
}