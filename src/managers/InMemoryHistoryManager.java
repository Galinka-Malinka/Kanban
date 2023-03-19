package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node firstNode = null;
    private Node lastNode = null;
    private Map<Integer, Node> map = new HashMap<>();

    @Override
    public void add(Task task) {       // Добавление задачи в список истории просмотров
        Node node = new Node(task);
        if (map.containsKey(task.getId())) {
            Node oldNode = map.get(task.getId());
            removeNode(oldNode);
            map.replace(task.getId(), node);
        } else {
            map.put(task.getId(), node);
        }
        linkLast(node);
    }

    @Override
    public void remove(int id) {  // Удаление задачи из списка просмотренных задач
        Node node = map.get(id);
        removeNode(node);
        map.remove(id);
    }

    @Override
    public List<Task> getHistory() {  // Получение списка просмотренных задач
        List<Task> history = new ArrayList<>();
        Node node = firstNode;
        while (node != null) {
            history.add(node.getData());
            node = node.getNext();
        }
        return history;
    }

    private void linkLast(Node node) {  // Добавление узла в двусвязный список
        if (lastNode != null) {
            lastNode.setNext(node);
            node.setPrev(lastNode);
        }
        lastNode = node;
        if (firstNode == null) {
            firstNode = node;
        }
    }

    private void removeNode(Node node) {  // Удаление узла из двусвязного списка
        Node prevNode = node.getPrev();
        Node nextNode = node.getNext();
        if (prevNode != null) {
            prevNode.setNext(nextNode);
        } else {
            firstNode = nextNode;
        }
        if (nextNode != null) {
            nextNode.setPrev(prevNode);
        } else {
            lastNode = prevNode;
        }
    }

    @Override
    public void removeHistory(){
        for (Node node : map.values()) {
            removeNode(node);
        }
        map.clear();
    }
}