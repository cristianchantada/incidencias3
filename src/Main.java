import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static final String PATH_TO_MODEL = "C:\\Users\\Usuario\\Desktop\\incidencias3\\src\\model";
    private static final String USERS_FILE = PATH_TO_MODEL + "\\users.txt";
    private static final String INCIDENCE_TYPES_FILE = PATH_TO_MODEL + "\\incidenceTypes.txt";
    private static final String INCIDENCE_FILE = PATH_TO_MODEL + "\\incidences.txt";
    private static final String EMPLOYEES_FILE = PATH_TO_MODEL + "\\employees.txt";

    private static final File usersTxtFile = new File(USERS_FILE);
    private static final File incidenceTypesTxtFile = new File(INCIDENCE_TYPES_FILE);
    private static final File incidenceTxtFile = new File(INCIDENCE_FILE);
    private static final File employeesTxtFile = new File(EMPLOYEES_FILE);

    private static List<User> appUsers = new ArrayList<>();
    private static List<Incidence> incidences = new ArrayList<>();
    private static final List<String> incidenceTypes = new ArrayList<>();
    private static final List<Employee> employees = new ArrayList<>();

    public static void main (String[] args) {
        loadData();
        showMainMenu();
    }

    private static void loadData(){
        appUsers.clear();
        incidences.clear();
        incidenceTypes.clear();
        employees.clear();

        try {
            Scanner usc = new Scanner(usersTxtFile);
            Scanner itsc = new Scanner(incidenceTypesTxtFile);
            Scanner isc = new Scanner(incidenceTxtFile);
            Scanner esc = new Scanner(employeesTxtFile);

            User user;
            while(usc.hasNextLine()){
                String line = usc.nextLine();
                if(line.contains("<NIF>")){
                   String userNif = line.replace("<NIF>", "").replaceAll("</NIF>", "").replaceAll("\t", "");
                   String userName = usc.nextLine().replaceAll("<NOMBRE>", "").replaceAll("</NOMBRE>", "").replaceAll("\t", "");
                   String userEmail = usc.nextLine().replaceAll("<CORREO>", "").replaceAll("</CORREO>", "").replaceAll("\t", "");
                   user = new User(userNif, userName, userEmail);
                   appUsers.add(user);
                }
            }

            while(itsc.hasNextLine()){
                String line = itsc.nextLine();
                incidenceTypes.add(line);
            }

            while(isc.hasNextLine()){
                String line = isc.nextLine();
                String[] incidenceDataArray = line.split("\\|");

                String openIncidenceDateStr = incidenceDataArray[0];

                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

                // Parsear la cadena utilizando el formateador
                LocalDateTime openIncidenceDateInLocalDateFormat = LocalDateTime.parse(openIncidenceDateStr, formatter);

                String nifIncidenceEmployeeStr = incidenceDataArray[1];
                String nameIncidenceEmployeeStr = incidenceDataArray[2];
                String emailIncidenceEmployeeStr = incidenceDataArray[3];
                String isAdminIncidenceEmployeeStr = incidenceDataArray[4];

                try {
                    Boolean employeeIsAdmin = Boolean.parseBoolean(isAdminIncidenceEmployeeStr);
                    Employee employee = new Employee(nifIncidenceEmployeeStr, nameIncidenceEmployeeStr, emailIncidenceEmployeeStr, employeeIsAdmin);

                    String nifIncidenceClientStr = incidenceDataArray[5];
                    String nameIncidenceClientStr = incidenceDataArray[6];
                    String emailIncidenceClientStr = incidenceDataArray[7];
                    String incidenceDescriptionStr = incidenceDataArray[8];
                    String incidenceTypeStr = incidenceDataArray[9];
                    String incidenceStateStr = incidenceDataArray[10];
                    IncidenceState incidenceState = IncidenceState.RECIBIDA;

                    switch (incidenceStateStr) {
                        case "EN_PROCESO" -> {
                            incidenceState = IncidenceState.EN_PROCESO;
                        }
                        case "SOLUCIONADA" -> {
                            incidenceState = IncidenceState.SOLUCIONADA;
                        }
                    }

                    User client = new User(nifIncidenceClientStr, nameIncidenceClientStr, emailIncidenceClientStr);
                    Incidence incidence = new Incidence(openIncidenceDateInLocalDateFormat, employee, client,
                            incidenceDescriptionStr, incidenceTypeStr, incidenceState);
                    incidences.add(incidence);

                } catch (IllegalArgumentException e) {
                    System.out.println("employeeAtributesArray[3] no puede ser convertida a booleano: " + e.getMessage());
                }
            }

            while(esc.hasNextLine()){
                String line = esc.nextLine();
                String[] employeeAtributesArray = line.split("\\|");
                String employeeNif = employeeAtributesArray[0];
                String employeeName = employeeAtributesArray[1];
                String employeeemail = employeeAtributesArray[2];
                try {
                    Boolean employeeIsAdmin = Boolean.parseBoolean(employeeAtributesArray[3]);
                    Employee employee = new Employee(employeeNif, employeeName, employeeemail, employeeIsAdmin);
                    employees.add(employee);
                } catch (IllegalArgumentException e) {
                    System.out.println("employeeAtributesArray[3] no puede ser convertida a booleano: " + e.getMessage());
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error en la lectura del fichero users.txt: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void showMainMenu(){
        String[] options = {"Entrada nueva incidencia", "Cambiar el estado de una incidencia", "Solucionar incidencia por código de cliente", "Listar incidencias", "Ingresar como Administrador" ,"Salir del programa"};
        int selection = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Gestión de Incidencias",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (selection) {
            case 0:
                newIncidence();
                break;
            case 1:
                changeIncidenceState();
                break;
            case 2:
                closeIncidence();
                break;
            case 3:
                listIncidences();
                break;
            case 4:
                showSiginPane();
                break;
            case 5:
                System.exit(0);
        }
    }

    public static void newIncidence(){

        int employeeIndexSelection = selectEmployee();
        Employee incidenceEmployee = employees.get(employeeIndexSelection);

        int clientIndexSelection = selectClient("Indique el cliente que abre la incidencia");
        User incidentClient = appUsers.get(clientIndexSelection);

        String incidenceDescription = JOptionPane.showInputDialog(null, "Descripción de la incidencia");

        int incidenceTypeIndexSelection = selectIncidenceType();
        String incidenceType = incidenceTypes.get(incidenceTypeIndexSelection);

        Incidence newIncidence = new Incidence(incidenceEmployee, incidentClient, incidenceDescription, incidenceType);
        incidences.add(newIncidence);
        rewriteIncidencesTxt();
        showSimpleMessage("Incidencia creada con éxito");
        main(null);
    }

    public static void changeIncidenceState(){

        int incidenceIndex = selectIncidence();

        IncidenceState[] incidenceStateOptions = IncidenceState.values();
        String[] optionsNames = new String[incidenceStateOptions.length];
        for (int i = 0; i < incidenceStateOptions.length; i++) {
            optionsNames[i] = incidenceStateOptions[i].getState();
        }

        int selectedIndex = JOptionPane.showOptionDialog(
                null,
                "Selecciona un estado de incidencia",
                "Estado de incidencia",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                optionsNames,
                optionsNames[0]
        );

        switch (optionsNames[selectedIndex]) {
            case "recibida" -> incidences.get(incidenceIndex).setIncidenceState(IncidenceState.RECIBIDA);
            case "en proceso" -> incidences.get(incidenceIndex).setIncidenceState(IncidenceState.EN_PROCESO);
            case "solucionada" -> incidences.get(incidenceIndex).setIncidenceState(IncidenceState.SOLUCIONADA);
        }

        showSimpleMessage("El estado de la incidencia ha sido cambiado a " + incidences.get(incidenceIndex).getIncidenceState());
        rewriteIncidencesTxt();
        main(null);
    }

    public static void closeIncidence(){
        List<String> incidentOptionList = getStrings();
        if(incidentOptionList.size() > 1){
            String[] incidenceOptionsArray = incidentOptionList.toArray(new String[0]);
            int incidenceIndex = JOptionPane.showOptionDialog(
                    null,
                    "Selecciona la incidencia a establecer como solucionada",
                    "Solucionar incidencia",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    incidenceOptionsArray,
                    incidenceOptionsArray[0]
            );

            if(incidenceIndex == incidentOptionList.size() - 1){
                main(null);
            }

            String selectedIncidenceToRemoveStr = incidenceOptionsArray[incidenceIndex];
            int numRef = Integer.parseInt( selectedIncidenceToRemoveStr.substring(0, 1));

            incidences.get(numRef).setIncidenceState(IncidenceState.SOLUCIONADA);
            rewriteIncidencesTxt();
        } else {
            showSimpleMessage("Todas las incidencias de la app están solucionadas");
        }
        main(null);
    }

    private static List<String> getStrings() {
        List<String> incidentOptionList = new ArrayList<>();
        int i = 0;
        for(Incidence inc : incidences){
            if(!inc.getIncidenceState().equals(IncidenceState.SOLUCIONADA)){
                User u = inc.getUser();
                String uNif = u.getNif();
                String iDesc = inc.getDescription();
                IncidenceState iState = inc.getIncidenceState();
                incidentOptionList.add(i + "-. Cód.cliente: " + uNif + " | Estado: " + iState + " | Desc.: " + iDesc);
            }
            i++;
        }

        incidentOptionList.add("SALIR");
        return incidentOptionList;
    }

    public static void listIncidences(){
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for(Incidence incidence: incidences){
            Employee e = incidence.getEmployee();
            User u = incidence.getUser();


            LocalDateTime incidenceOpenDateTime = incidence.getOpenIncidenceDate();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            String formattedDateTime = incidenceOpenDateTime.format(formatter);
            sb.append("--------------------------------------------------------------------------\n");
            sb.append("Incidencia " + i++ + " con fecha " + formattedDateTime + "\n");
            sb.append("\tDescripción: " + incidence.getDescription()+ "\n");
            sb.append("\tTipo: " + incidence.getIncidenceType()+ "\n");
            sb.append("\tEstado: " + incidence.getIncidenceState()+ "\n");
            sb.append("\tEmpleado receptor:\n");
            sb.append("\t\tNombre: " + e.getName() + "\n");
            sb.append("\t\tNif: " + e.getNif() + "\n");
            sb.append("\t\tE-mail: " + e.getEmail() + "\n");
            sb.append("\tCliente emisor:\n");
            sb.append("\t\tNombre: " + u.getName() + "\n");
            sb.append("\t\tNif: " + u.getNif() + "\n");
            sb.append("\t\tE-mail: " + u.getEmail() + "\n");
        }

        String message = sb.toString();
        showSimpleMessage(message, "Listado de incidencias");
        main(null);
    }

    public static void showSiginPane(){
        String adminName = JOptionPane.showInputDialog(null, "Ingrese el nombre de administrador");
        String adminNif = JOptionPane.showInputDialog(null, "Ingrese el nif de administrador");

        boolean credentials = false;

        for(Employee e : employees){
            if(e.isAdmin()){
                if(e.getNif().equals(adminNif)) {
                    if (e.getName().equals(adminName)){
                        credentials = true;
                    }
                }
            }
        }

        if (credentials){
            showUsersAdminPane();
        } else {
            showSimpleMessage("Nombre de admin o nif incorrectos", "Acceso denegado");
            main(null);
        }
    }

    public static void showUsersAdminPane(){
        String[] options = {"Alta usuario", "Baja usuario", "Modificación usuario", "Listar usuarios", "SALIR Admin clientes"};
        int selection = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Gestión de Usuarios",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (selection) {
            case 0:
                userSignUp();
                break;
            case 1:
                userUnregister();
                break;
            case 2:
                userModification();
                break;
            case 3:
                listUsers();
                break;
            case 4:
                main(null);
        }
    }


    public static void listUsers(){
        StringBuilder sb = new StringBuilder();
        for (User user : appUsers) {
            sb.append("---------------------------------------------------------------\n");
            sb.append("Usuario con NIF " + user.getNif() +
                    "\n\tNombre: " + user.getName() +
                    "\n\te-mail: " + user.getEmail()
                    ).append("\n");

        }
        String listText = sb.toString();

        showSimpleMessage(listText, "Datos de la Lista");
        showUsersAdminPane();

    }

    public static void userSignUp() {
        String userName = JOptionPane.showInputDialog(null, "Inserte nombre nuevo usuario");
        String userNif = JOptionPane.showInputDialog(null, "Inserte DNI usuario");
        String userEmail = JOptionPane.showInputDialog(null, "Inserte e-mail");
        User user = new User(userNif, userName, userEmail);
        boolean isContain = userNifExists(userNif);

        if(!isContain){
            appUsers.add(user);
            rewriteUsersTxt();
        } else {
            showSimpleMessage("NIF ya registrado. El usuario ya se encuentra en el sistema");
        }
        showUsersAdminPane();
    }

    public static void userUnregister() {
        int clientIndex = selectClient("Indique el cliente que desea eliminar");
        appUsers.remove(clientIndex);
        showSimpleMessage("Usuario eliminado satisfactoriamente");
        rewriteUsersTxt();
        showUsersAdminPane();
    }

    public static void userModification() {
        int userIndex = selectClient("Indique el usuario que desea modificar");
        User user = appUsers.get(userIndex);

        String[] options = {"Nombre", "Email", "NIF"};
        int selection = JOptionPane.showOptionDialog(
                null,
                "USUARIO:\n" +
                        "\tNombre: " + user.getName() + "\n" +
                        "\tNIF: " + user.getNif() + "\n" +
                        "\tNombre: " + user.getEmail() + "\n" +
                        "¿QUÉ DATO DESEA MODIFICAR?:",
                "Modificación de usuario",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (selection) {
            case 0:
                String userName = JOptionPane.showInputDialog(null, "Introduzca nuevo nombre usuario");
                user.setName(userName);
                showSimpleMessage("El nombre del usuario " + user.getName() + " ha sido modificado con éxito");
                break;
            case 1:
                String userEmail = JOptionPane.showInputDialog(null, "Introduzca el nuevo emaildel usuario");
                user.setEmail(userEmail);
                showSimpleMessage("El email del usuario " + user.getName() + " ha sido modificado con éxito");
                break;
            case 2:
                String userNif = JOptionPane.showInputDialog(null, "Introduzca nuevo NIF de usuario");
                if(!userNifExists(userNif)){
                    user.setNif(userNif);
                    showSimpleMessage("El NIF del usuario " + user.getName() + " ha sido modificado con éxito");
                } else {
                    showSimpleMessage("El NIF ya está registrado en el sistema. Velva a intentarlo");
                }
                break;
        }
        rewriteUsersTxt();
        showUsersAdminPane();
    }



    public static void rewriteUsersTxt()  {
        FileWriter fw;
        try {
            fw = new FileWriter(USERS_FILE, false);
            fw.write("""
                    <?xml version="1.0" encoding="UTF-8" ?>
                    <dataset>
                    """
            );

            for(User user: appUsers){
                fw.write("\t<record>\n" +
                        "\t\t<NIF>"+ user.getNif() + "</NIF>\n" +
                        "\t\t<NOMBRE>" + user.getName() + "</NOMBRE>\n" +
                        "\t\t<CORREO>"+ user.getEmail()+"</CORREO>\n" +
                        "\t<record>\n"
                );
            }
            fw.write("</dataset>\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Error en la escritura de fichero users.txt: " +  e.getMessage());
            e.printStackTrace();
        }
    }

    public static void rewriteIncidencesTxt()  {
        FileWriter fw;
        try {
            fw = new FileWriter(INCIDENCE_FILE, false);

            for(Incidence incidence: incidences){
                Employee e = incidence.getEmployee();
                User u = incidence.getUser();

                LocalDateTime incidenceOpenDateTime = incidence.getOpenIncidenceDate();
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                String formattedDateTime = incidenceOpenDateTime.format(formatter);

                fw.write(formattedDateTime+"|");
                fw.write(e.getNif()+"|"+e.getName()+"|"+e.getEmail()+"|"+e.isAdmin()+"|");
                fw.write(u.getNif()+"|"+u.getName()+"|"+u.getEmail()+"|");
                fw.write(incidence.getDescription()+"|"+incidence.getIncidenceType()+"|"+incidence.getIncidenceState()+"\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error en la escritura de fichero users.txt: " +  e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean userNifExists(String userNif){
        for(User User : appUsers){
            if (User.getNif().equals(userNif)) {
                return true;
            }
        }
        for(Employee e : employees){
            if (e.getNif().equals(userNif)) {
                return true;
            }
        }
        return false;
    }

    public static int selectEmployee(){
        List<String> paneEmployeesOptionsList = new ArrayList<>();
        employees.forEach(e -> paneEmployeesOptionsList.add(e.getName()));

        String[] paneEmployeeOptionsArray = paneEmployeesOptionsList.toArray(new String[0]);

        return JOptionPane.showOptionDialog(
                null,
                "Selecciona el empleado que recibirá la incidencia",
                "Empleado",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                paneEmployeeOptionsArray,
                paneEmployeeOptionsArray[0]
        );
    }

    public static int selectIncidence(){
        List<String> paneIncidenceOptionsList = new ArrayList<>();

        for(Incidence i : incidences){
            User u = i.getUser();
            String message = "Cliente: " + u.getNif() + ". Estado inc.: " + i.getIncidenceState();
            paneIncidenceOptionsList.add(message);
        }

        String[] paneIncidenceOptionsArray = paneIncidenceOptionsList.toArray(new String[0]);

        return JOptionPane.showOptionDialog(
                null,
                "Indique la incidencia de la que desea cambiar su estado",
                "Cliente",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                paneIncidenceOptionsArray,
                paneIncidenceOptionsArray[0]
        );
    }

    public static int selectClient(String message){
        List<String> paneClientOptionsList = new ArrayList<>();
        appUsers.forEach(u -> paneClientOptionsList.add(u.getName()));

        String[] paneClientOptionsArray = paneClientOptionsList.toArray(new String[0]);

        return JOptionPane.showOptionDialog(
                null,
                message,
                "Cliente",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                paneClientOptionsArray,
                paneClientOptionsArray[0]
        );
    }

    public static int selectIncidenceType(){

        List<String> paneIncidenceTypeOptionsList = new ArrayList<>(incidenceTypes);

        String[] paneIncidenceTypeOptionsArray = paneIncidenceTypeOptionsList.toArray(new String[0]);

        return JOptionPane.showOptionDialog(
                null,
                "Indique el cliente que abre la incidencia",
                "Cliente",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                paneIncidenceTypeOptionsArray,
                paneIncidenceTypeOptionsArray[0]
        );
    }

    public static void showSimpleMessage(String message){
        JOptionPane.showMessageDialog(null, message);
    }

    public static void showSimpleMessage(String message, String title){
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

}
