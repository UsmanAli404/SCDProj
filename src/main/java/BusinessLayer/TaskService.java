package BusinessLayer;

import DataAcessLayer.TaskRepository;

import java.util.List;

public interface TaskService {
    void addTask(String task);
    List<String> getAllTasks();
}

