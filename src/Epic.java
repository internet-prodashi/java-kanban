import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subtaskIdList;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
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

    public Epic(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration, LocalDateTime endTime, List<Integer> subtaskIdList) {
        super(id, title, description, status, startTime, duration);
        this.endTime = endTime;
        this.subtaskIdList = subtaskIdList;
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(List<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration() +
                ", endTime=" + endTime +
                ", subtaskIdList=" + subtaskIdList +
                '}';
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}