import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subtaskId;

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskId = new ArrayList<>();
    }

    public Epic(int id, String title, String description) {
        super(id, title, description);
        this.subtaskId = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status, List<Integer> subtaskId) {
        super(id, title, description, status);
        this.subtaskId = subtaskId;
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(List<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubtaskId(), epic.getSubtaskId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubtaskId());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", subtaskId=" + subtaskId +
                '}';
    }

}