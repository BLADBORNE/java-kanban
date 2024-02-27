package service.managers;

import model.customnode.Node;
import model.tasks.Epic;
import model.tasks.Subtask;
import model.tasks.Task;
import service.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            oldTail.setNext(newTail);
        }
    }

    @Override
    public void remove(int id) {
        if (!customLinkedList.isEmpty()) {
            if (customLinkedList.containsKey(id)) {
                Node<Task> node = customLinkedList.get(id);
                Task task = node.getData();
                if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    if (!epic.getSubtask().isEmpty()) {
                        for (Subtask subtask : epic.getSubtask().values()) {
                            if (customLinkedList.containsKey(subtask.getId())) {
                                Node<Task> subtaskNode = customLinkedList.get(subtask.getId());
                                removeNode(subtaskNode);
                                customLinkedList.remove(subtask.getId());
                            }
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
            return new ArrayList<>();
        }
        return getTasks();
    }

    private void removeNode(Node<Task> node) {
        final Node<Task> prevNode = node.getPrev();
        final Node<Task> nextNode = node.getNext();

        if (prevNode != null) {
            prevNode.setNext(nextNode);
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.setPrev(prevNode);
        } else {
            tail = prevNode;
        }

        node.setNext(null);
        node.setData(null);
        node.setPrev(null);
    }

    private List<Task> getTasks() {
        if (!customLinkedList.isEmpty()) {
            List<Task> taskList = new ArrayList<>();
            Node<Task> curNode = tail;
            while (curNode != null) {
                taskList.add(curNode.getData());
                curNode = curNode.getPrev();
            }
            return taskList;
        }
        return null;
    }
}