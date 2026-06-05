public class Queue
{
    int queue[];
    int head = 0;
    int tail = 0;
    int size;

    //Constructor Method for creating the queue with size input
    public Queue(int setSize)
    {
        this.queue = new int[setSize];
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
    public void enqueue(int element)
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
    public int dequeue()
    {
        //Throws an exception if it's empty
        if(this.queueEmpty()){