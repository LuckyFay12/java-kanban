package manager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void initManager() {
        this.taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}




