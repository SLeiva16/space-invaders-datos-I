package structures;

public class List<T> {

    private Node<T> head;
    private int size;

    private static class Node<T> {
        T data;  //dato guardado
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;  //cuando se crea no apunta a nadie
        }
    }

    public List() {
        head = null;
        size = 0;
    }

    //Agrega un elemento al final
    public void add(T element) {
        Node<T> newNode = new Node<>(element);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;  //avanza hasta el ultimo nodo
            }
            current.next = newNode;
        }
        size++;
    }

    //Busca por la posicion
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next; //avanza desde el inicio i veces
        }
        return current.data;
    }

    //Eliminar por posicion
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        T removed;
        if (index == 0) {
            removed = head.data;
            head = head.next;  //segundo pasa al primero
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            removed = current.next.data;
            current.next = current.next.next; //salta nodo eliminado
        }
        size--;
        return removed;
    }

    //Elimina por el valor
    public boolean remove(T element) {
        if (head == null) return false;
        if (head.data.equals(element)) {
            head = head.next;
            size--;
            return true;
        }
        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(element)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    //Busca si existe
    public boolean contains(T element) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(element)) return true;
            current = current.next;
        }
        return false;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    //Imprime lista
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
