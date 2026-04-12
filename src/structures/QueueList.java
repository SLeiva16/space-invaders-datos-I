package structures;
// Se usa QueueList en vez de List porque los jugadores deben ser emparejados
// en orden de llegada, el primero que espero, primero en jugar (FIFO).
// Ademas, agregar y sacar jugadores de la cola es más eficiente con Queue
// ya que no necesita recorrer toda la lista para encontrar el siguiente jugador.

public class QueueList<E> {

    private Node<E> front;
    private Node<E> rear;
    private int size;

    private static class Node<E> {
        E data;
        Node<E> next;
        Node(E data) {
            this.data = data;
            this.next = null;
        }
    }

    public QueueList() {
        front = rear = null;
        size = 0; // ← inicializar
    }

    public void enqueue(E element) {
        Node<E> newNode = new Node<>(element);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++; // ← sumar
    }

    public E dequeue() {
        if (isEmpty()) throw new RuntimeException("Queue vacía");
        E data = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--; // ← restar
        return data;
    }

    public E peek() {
        if (isEmpty()) throw new RuntimeException("Queue vacía");
        return front.data;
    }

    public boolean isEmpty() { return front == null; }
    public int size()        { return size; }
}