package service;

import model.Epic;
import model.Node;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> customLinkedList = new HashMap<>();

    @Override
    public void add(Task task) {
        if (customLinkedList.containsKey(task.getId())) {
            remove(task.getId());
            linkLast(task);
        } else {
            linkLast(task);
        }
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newTail = new Node<>(oldTail, task, null);
        tail = newTail;
        customLinkedList.put(task.getId(), newTail);
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
    }

    @Override
    public void remove(int id) {
        if (!customLinkedList.isEmpty()) {
            if (customLinkedList.containsKey(id)) {
                Node<Task> node = customLinkedList.get(id);
                Task task = node.data;
                if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    if (!epic.getSubtask().isEmpty()) {
                        for (Subtask subtask : epic.getSubtask().values()) {
                            Node<Task> subtaskNode = customLinkedList.get(subtask.getId());
                            removeNode(subtaskNode);
                            customLinkedList.remove(subtask.getId());
                        }
                    }
                }
                removeNode(node);
                customLinkedList.remove(id);
            }
        } else {
            System.out.println("Удалять нечего, история просмотров пуста");
        }
    }

    @Override
    public List<Task> getHistory() {
        if (getTasks() == null) {
            System.out.println("Вы еще не просмотрели ни одну задачу");
            return new ArrayList<>();
        }
        return getTasks();
    }

    private void removeNode(Node<Task> node) {
        final Node<Task> prevNode = node.prev;
        final Node<Task> nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }

        node.prev = null;
        node.next = null;
        node.data = null;
    }


    private List<Task> getTasks() {
        if (!customLinkedList.isEmpty()) {
            List<Task> taskList = new ArrayList<>();
            Node<Task> curNode = tail;
            while (curNode != null) {
                taskList.add(curNode.data);
                curNode = curNode.prev;
            }
            return taskList;
        }
        return null;
    }
}
