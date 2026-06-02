import java.io.*;
import java.util.*;

public class SistemaCitasClinicas {

    public static class Usuario {
        private String id;
        private String contrasena;

        public Usuario(String id, String contrasena) {
            this.id = id;
            this.contrasena = contrasena;
        }
        public String getId() { return id; }
        public String getContrasena() { return contrasena; }
    }

    public static class Doctor {
        private String id;
        private String nombreCompleto;
        private String especialidad;

        public Doctor(String id, String nombreCompleto, String especialidad) {
            this.id = id;
            this.nombreCompleto = nombreCompleto;
            this.especialidad = especialidad;
        }
        public String getId() { return id; }
        public String getNombreCompleto() { return nombreCompleto; }
        public String getEspecialidad() { return especialidad; }

        public String toCSV() {
            return id + "," + nombreCompleto + "," + especialidad;
        }
        @Override
        public String toString() {
            return "[ID: " + id + "] Dr(a). " + nombreCompleto + " - Esp: " + especialidad;
        }
    }

    public static class Paciente {
        private String id;
        private String nombreCompleto;

        public Paciente(String id, String nombreCompleto) {
            this.id = id;
            this.nombreCompleto = nombreCompleto;
        }
        public String getId() { return id; }
        public String getNombreCompleto() { return nombreCompleto; }

        public String toCSV() {
            return id + "," + nombreCompleto;
        }
        @Override
        public String toString() {
            return "[ID: " + id + "] Paciente: " + nombreCompleto;
        }
    }

    public static class Cita {
        private String id;
        private String fechaHora;
        private String motivo;
        private Doctor doctor;
        private Paciente paciente;

        public Cita(String id, String fechaHora, String motivo, Doctor doctor, Paciente paciente) {
            this.id = id;
            this.fechaHora = fechaHora;
            this.motivo = motivo;
            this.doctor = doctor;
            this.paciente = paciente;
        }
        public String getId() { return id; }

        public String toCSV() {
            return id + "," + fechaHora + "," + motivo + "," + doctor.getId() + "," + paciente.getId();
        }
        @Override
        public String toString() {
            return "Cita #" + id + " | Fecha: " + fechaHora + " | Motivo: " + motivo +
                    "\n  -> Doctor: " + doctor.getNombreCompleto() +
                    "\n  -> Paciente: " + paciente.getNombreCompleto();
        }
    }

    private Map<String, Usuario> mapaUsuarios = new HashMap<>();
    private Map<String, Doctor> mapaDoctores = new HashMap<>();
    private Map<String, Paciente> mapaPacientes = new HashMap<>();
    private Map<String, Cita> mapaCitas = new HashMap<>();

    // Archivos CSV
    private final String ARCHIVO_USUARIOS = "usuarios.csv";
    private final String ARCHIVO_DOCTORES = "doctores.csv";
    private final String ARCHIVO_PACIENTES = "pacientes.csv";
    private final String ARCHIVO_CITAS = "citas.csv";

    public SistemaCitasClinicas() {
        mapaUsuarios.put("admin", new Usuario("admin", "1234"));
    }

    public boolean login(String id, String contrasena) {
        Usuario u = mapaUsuarios.get(id);
        return u != null && u.getContrasena().equals(contrasena);
    }

    public void darAltaDoctor(String id, String nombre, String especialidad) throws Exception {
        if (mapaDoctores.containsKey(id)) {
            throw new Exception("Error: Ya existe un doctor con el ID " + id);
        }
        Doctor doc = new Doctor(id, nombre, especialidad);
        mapaDoctores.put(id, doc);
    }

    public void darAltaPaciente(String id, String nombre) throws Exception {
        if (mapaPacientes.containsKey(id)) {
            throw new Exception("Error: Ya existe un paciente con el ID " + id);
        }
        Paciente pac = new Paciente(id, nombre);
        mapaPacientes.put(id, pac);
    }

    public void crearCita(String id, String fechaHora, String motivo, String idDoctor, String idPaciente) throws Exception {
        if (mapaCitas.containsKey(id)) {
            throw new Exception("Error: Ya existe una cita con el ID " + id);
        }
        Doctor doc = mapaDoctores.get(idDoctor);
        if (doc == null) {
            throw new Exception("Error: El Doctor con ID '" + idDoctor + "' no existe en el sistema.");
        }
        Paciente pac = mapaPacientes.get(idPaciente);
        if (pac == null) {
            throw new Exception("Error: El Paciente con ID '" + idPaciente + "' no existe en el sistema.");
        }

        Cita cita = new Cita(id, fechaHora, motivo, doc, pac);
        mapaCitas.put(id, cita);
    }

    public void cargarDatos() {
        cargarUsuarios();
        cargarDoctores();
        cargarPacientes();
        cargarCitas();
    }

    public void guardarDatos() throws IOException {
        guardarArchivo(ARCHIVO_USUARIOS, mapaUsuarios.values(), u -> ((Usuario)u).getId() + "," + ((Usuario)u).getContrasena());
        guardarArchivo(ARCHIVO_DOCTORES, mapaDoctores.values(), d -> ((Doctor)d).toCSV());
        guardarArchivo(ARCHIVO_PACIENTES, mapaPacientes.values(), p -> ((Paciente)p).toCSV());
        guardarArchivo(ARCHIVO_CITAS, mapaCitas.values(), c -> ((Cita)c).toCSV());
    }

    private void cargarUsuarios() {
        File f = new File(ARCHIVO_USUARIOS);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 2) {
                    mapaUsuarios.put(datos[0], new Usuario(datos[0], datos[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Aviso: No se pudieron cargar los usuarios de " + ARCHIVO_USUARIOS);
        }
    }

    private void cargarDoctores() {
        File f = new File(ARCHIVO_DOCTORES);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 3) {
                    mapaDoctores.put(datos[0], new Doctor(datos[0], datos[1], datos[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Aviso: No se pudieron cargar los doctores.");
        }
    }

    private void cargarPacientes() {
        File f = new File(ARCHIVO_PACIENTES);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 2) {
                    mapaPacientes.put(datos[0], new Paciente(datos[0], datos[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Aviso: No se pudieron cargar los pacientes.");
        }
    }

    private void cargarCitas() {
        File f = new File(ARCHIVO_CITAS);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 5) {
                    String idCita = datos[0];
                    String fecha = datos[1];
                    String motivo = datos[2];
                    Doctor doc = mapaDoctores.get(datos[3]);
                    Paciente pac = mapaPacientes.get(datos[4]);

                    if (doc != null && pac != null) {
                        mapaCitas.put(idCita, new Cita(idCita, fecha, motivo, doc, pac));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Aviso: No se pudieron cargar las citas.");
        }
    }

    private interface TransformadorLinea { String transformar(Object obj); }

    private void guardarArchivo(String nombreArchivo, Collection<?> coleccion, TransformadorLinea t) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Object obj : coleccion) {
                bw.write(t.transformar(obj));
                bw.newLine();
            }
        }
    }

    public void mostrarDoctores() { mapaDoctores.values().forEach(System.out::println); }
    public void mostrarPacientes() { mapaPacientes.values().forEach(System.out::println); }
    public void mostrarCitas() { mapaCitas.values().forEach(System.out::println); }


    public static void main(String[] args) {
        SistemaCitasClinicas sistema = new SistemaCitasClinicas();
        sistema.cargarDatos();

        Scanner scanner = new Scanner(System.in);
        boolean autenticado = false;

        System.out.println("=========================================");
        System.out.println(" SISTEMA DE ADMINISTRACION CLINICA ");
        System.out.println("=========================================");

        while (!autenticado) {
            System.out.print("ID de Administrador: ");
            String user = scanner.nextLine();
            System.out.print("Contraseña: ");
            String pass = scanner.nextLine();

            if (sistema.login(user, pass)) {
                autenticado = true;
                System.out.println("\n Acceso concedido. ¡Bienvenido!");
            } else {
                System.out.println(" Credenciales incorrectas. Intente nuevamente.\n");
            }
        }

        int opcion = 0;
        do {
            System.out.println("\n-----------------------------------------");
            System.out.println("              MENÚ PRINCIPAL             ");
            System.out.println("-----------------------------------------");
            System.out.println("1. Registrar Doctor");
            System.out.println("2. Registrar Paciente");
            System.out.println("3. Crear Cita");
            System.out.println("4. Ver Doctores Registrados");
            System.out.println("5. Ver Pacientes Registrados");
            System.out.println("6. Ver Citas Agendadas");
            System.out.println("7. Guardar y Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                continue;
            }

            try {
                switch (opcion) {
                    case 1:
                        System.out.println("\n--- ALTA DE DOCTOR ---");
                        System.out.print("ID Único: "); String idDoc = scanner.nextLine();
                        System.out.print("Nombre Completo: "); String nomDoc = scanner.nextLine();
                        System.out.print("Especialidad: "); String espDoc = scanner.nextLine();
                        sistema.darAltaDoctor(idDoc, nomDoc, espDoc);
                        System.out.println(" Doctor registrado con éxito.");
                        break;

                    case 2:
                        System.out.println("\n--- ALTA DE PACIENTE ---");
                        System.out.print("ID Único: "); String idPac = scanner.nextLine();
                        System.out.print("Nombre Completo: "); String nomPac = scanner.nextLine();
                        sistema.darAltaPaciente(idPac, nomPac);
                        System.out.println(" Paciente registrado con éxito.");
                        break;

                    case 3:
                        System.out.println("\n--- CREAR CITA ---");
                        System.out.print("ID de la Cita: "); String idCita = scanner.nextLine();
                        System.out.print("Fecha y Hora (ej. 15/10/2026 16:30): "); String fecha = scanner.nextLine();
                        System.out.print("Motivo de la consulta: "); String motivo = scanner.nextLine();
                        System.out.print("ID del Doctor Asignado: "); String docAsig = scanner.nextLine();
                        System.out.print("ID del Paciente: "); String pacAsig = scanner.nextLine();

                        sistema.crearCita(idCita, fecha, motivo, docAsig, pacAsig);
                        System.out.println(" Cita generada y relacionada exitosamente.");
                        break;

                    case 4:
                        System.out.println("\n--- LISTA DE DOCTORES ---");
                        sistema.mostrarDoctores();
                        break;

                    case 5:
                        System.out.println("\n--- LISTA DE PACIENTES ---");
                        sistema.mostrarPacientes();
                        break;

                    case 6:
                        System.out.println("\n--- LISTA DE CITAS ---");
                        sistema.mostrarCitas();
                        break;

                    case 7:
                        System.out.println("\nGuardando bases de datos en archivos CSV...");
                        sistema.guardarDatos();
                        System.out.println("¡Datos salvados! Saliendo con seguridad.");
                        break;

                    default:
                        System.out.println("Opción inválida. Intente del 1 al 7.");
                }
            } catch (Exception e) {
                // Captura controlada del error solicitado en los requerimientos. El flujo continúa.
                System.out.println("\n[ERROR CONTROLADO]: " + e.getMessage());
            }

        } while (opcion != 7);

        scanner.close();
    }
}