package BusinessLayer;

import DataAcessLayer.*;

import java.util.List;

public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void addTask(String task) {
        taskRepository.save(task);
    }

    @Override
    public List<String> getAllTasks() {
        return taskRepository.findAll();
    }
}
