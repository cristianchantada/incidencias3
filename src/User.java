public class User {

    private String nif;
    private String name;
    private String email;

    public User(String nif, String name, String email){
        this.nif = nif;
        this.name = name;
        this.email = email;
    }

    public String getNif(){
        return nif;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
