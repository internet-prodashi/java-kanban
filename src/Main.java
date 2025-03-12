

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        // Метод для задач. Создание. Сам объект должен передаваться в качестве параметра.
        Task task1 = new Task("Задачи 1", "Подробное описание задачи 1");
        manager.addTask(task1);
        Task task2 = new Task("Задачи 2", "Подробное описание задачи 2");
        manager.addTask(task2);
        Task task3 = new Task("Задачи 3", "Подробное описание задачи 3");
        manager.addTask(task3);

        System.out.println("=".repeat(7) + "1" + "=".repeat(7));

        // Метод для задач. Получение списка всех задач
        System.out.println(manager.getAllTasks());

        System.out.println("=".repeat(7) + "2" + "=".repeat(7));

        // Метод для задач. Получение по идентификатору
        System.out.println(manager.getTaskByID(2));
        System.out.println(manager.getHistory());


        System.out.println("=".repeat(7) + "3" + "=".repeat(7));

        // Метод для задач. Удаление по идентификатору
        manager.deleteTaskByID(2);
        manager.deleteTaskByID(15);
        manager.deleteTaskByID(0);
        System.out.println(manager.getAllTasks());

        System.out.println("=".repeat(7) + "4" + "=".repeat(7));

        // Метод для задач. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Task task4 = new Task(3, "Задача 3 новый", "Подробное описание задачи 3 новый", Status.IN_PROGRESS);
        manager.updateTaskByID(task4);
        Task task5 = new Task(15, "Задача 15 новый", "Подробное описание задачи 15 новый", Status.IN_PROGRESS);
        manager.updateTaskByID(task5);
        System.out.println(manager.getAllTasks());

        System.out.println("=".repeat(7) + "5" + "=".repeat(7));

        // Метод для задач. Удаление всех задач.
        manager.deleteAllTask();
        System.out.println(manager.getAllTasks());

        // Метод для эпиков. Создание. Сам объект должен передаваться в качестве параметра.
        Epic epic1 = new Epic("Заголовок эпика 1", "Подробное описание эпика 1");
        manager.addEpic(epic1);
        Epic epic2 = new Epic("Заголовок эпика 2", "Подробное описание эпика 2");
        manager.addEpic(epic2);

        System.out.println("=".repeat(7) + "6" + "=".repeat(7));

        // Метод для эпиков. Получение списка всех эпиков
        System.out.println(manager.getAllEpics());

        System.out.println("=".repeat(7) + "7" + "=".repeat(7));

        // Метод для эпиков. Получение по идентификатору
        System.out.println(manager.getEpicByID(5));
        System.out.println(manager.getEpicByID(15));

        System.out.println("=".repeat(7) + "8" + "=".repeat(7));

        // Метод для эпиков. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Epic epic3 = new Epic(5, "Заголовок эпика 2 новый", "Подробное описание эпика 2 новый");
        manager.updateEpicByID(epic3);
        Epic epic4 = new Epic(15, "Заголовок эпика 15 новый", "Подробное описание эпика 15 новый");
        manager.updateEpicByID(epic4);
        System.out.println(manager.getAllEpics());

        // Метод для подзадач. Создание. Сам объект должен передаваться в качестве параметра.
        Subtask subtask1 = new Subtask("Заголовок подзадачи 1 эпика 2", "Подробное описание подзадачи 1 эпика 2", 5);
        manager.addSubtask(5, subtask1);
        Subtask subtask2 = new Subtask("Заголовок подзадачи 2 эпика 2", "Подробное описание подзадачи 2 эпика 2", 5);
        manager.addSubtask(5, subtask2);
        Subtask subtask3 = new Subtask("Заголовок подзадачи 1 эпика 15", "Подробное описание подзадачи 1 эпика 15", 15);
        manager.addSubtask(15, subtask3);

        System.out.println("=".repeat(7) + "9" + "=".repeat(7));

        // Метод для подзадач. Получение списка всех подзадач
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllEpics());

        System.out.println("=".repeat(7) + "10" + "=".repeat(7));

        // Метод для подзадач. Получение по идентификатору
        System.out.println(manager.getSubtaskByID(7));
        System.out.println(manager.getSubtaskByID(15));

        System.out.println("=".repeat(7) + "11" + "=".repeat(7));

        // Метод для подзадач. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Subtask subtask4 = new Subtask(7, "Заголовок подзадачи 1 эпика 2 новая", "Подробное описание подзадачи 1 эпика 2 новая", Status.IN_PROGRESS, 5);
        manager.updateSubtaskByID(subtask4);
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllEpics());

        System.out.println("=".repeat(7) + "12" + "=".repeat(7));

        // Метод для подзадач. Удаление по идентификатору
        manager.deleteSubtaskByID(6);
        manager.deleteSubtaskByID(15);
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllEpics());

        System.out.println("=".repeat(7) + "13" + "=".repeat(7));

        // Метод для эпиков. Удаление по идентификатору
        manager.deleteEpicByID(4);
        manager.deleteEpicByID(15);
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllEpics());

        System.out.println("=".repeat(7) + "14" + "=".repeat(7));

        // Метод для эпиков. Получение списка всех подзадач определённого эпика.
        System.out.println(manager.getSubtaskListByEpicId(5));

        System.out.println("=".repeat(7) + "15" + "=".repeat(7));

        // Метод для подзадач. Удаление всех подзадач.
        manager.deleteAllSubtasks();
        System.out.println(manager.getAllSubtasks());

        System.out.println("=".repeat(7) + "16" + "=".repeat(7));

        // Метод для эпиков. Удаление всех эпиков.
        manager.deleteAllEpic();
        System.out.println(manager.getAllEpics());

        System.out.println("=".repeat(7) + "17" + "=".repeat(7));

        // Тест истории просмотров
        for (int i = 8; i < 19; i++) {
            Task task = new Task("Задача " + i, "Подробное описание задачи " + i);
            manager.addTask(task);
            System.out.println(manager.getTaskByID(i));
        }
        System.out.println(manager.getHistory());


    }

}
