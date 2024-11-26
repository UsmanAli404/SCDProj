package DataAcessLayer;

import java.util.ArrayList;
import java.util.List;

public interface TaskRepository {
    void save(String task);
    List<String> findAll();
}

