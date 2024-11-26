package DataAcessLayer;

import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryImpl implements TaskRepository {
    private final List<String> tasks = new ArrayList<>();

    @Override
    public void save(String task) {
        tasks.add(task);
    }

    @Override
    public List<String> findAll() {
        return new ArrayList<>(tasks);
    }
}
