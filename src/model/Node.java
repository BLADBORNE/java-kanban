package model;

public class Node<E> {
    public E data;
    public Node<E> next;
    public Node<E> prev;
    //public int id;

    public Node(Node<E> prev, E data, Node<E> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
