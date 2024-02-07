import java.time.LocalDate;
import java.time.LocalDateTime;

public class Incidence {

    private LocalDateTime openIncidenceDate;
    private IncidenceState incidenceState = IncidenceState.RECIBIDA;
    private Employee employee;
    private User user;
    private String description;
    private String incidenceType;

    public Incidence(Employee employee, User user, String description, String incidenceType){
        this.employee = employee;
        this.user = user;
        this. description = description;
        this. incidenceType = incidenceType;
        this.openIncidenceDate = LocalDateTime.now();
    }

    public Incidence(LocalDateTime openIncidenceDate, Employee employee, User user, String description, String incidenceType, IncidenceState incidenceState){
        this(employee, user, description, incidenceType);
        this.openIncidenceDate = openIncidenceDate;
        this.incidenceState = incidenceState;
    }

    public LocalDateTime getOpenIncidenceDate() {
        return openIncidenceDate;
    }

    public IncidenceState getIncidenceState() {
        return incidenceState;
    }

    public Employee getEmployee() {
        return employee;
    }

    public User getUser() {
        return user;
    }

    public String getDescription() {
        return description;
    }

    public String getIncidenceType() {
        return incidenceType;
    }

    public void setIncidenceState(IncidenceState incidenceState) {
        this.incidenceState = incidenceState;
    }
}
