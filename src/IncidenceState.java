public enum IncidenceState {

    RECIBIDA("recibida"),
    EN_PROCESO("en proceso"),
    SOLUCIONADA("solucionada");

    private final String state;

    IncidenceState(String state) {
        this.state = state;
    }

    public String getState(){
        return state;
    }

}
