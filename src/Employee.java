public class Employee extends User {

    private Boolean isAdmin;

    public Employee(String nif, String name, String email, Boolean isAdmin) {
        super(nif, name, email);
        this.isAdmin = isAdmin;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }
}
