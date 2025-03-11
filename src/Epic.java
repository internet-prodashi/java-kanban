import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subtaskIdList;

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIdList = new ArrayList<>();
    }

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        this.subtaskIdList = new ArrayList<>();
    }

    public Epic(int id, String title, String description) {
        super(id, title, description);
        this.subtaskIdList = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtaskIdList = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status, List<Integer> subtaskIdList) {
        super(id, title, description, status);
        this.subtaskIdList = subtaskIdList;
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(List<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubtaskIdList(), epic.getSubtaskIdList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubtaskIdList());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", subtaskIdList=" + subtaskIdList +
                '}';
    }

}