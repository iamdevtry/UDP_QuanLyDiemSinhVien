package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;
import server.constants.Constants;

public class Server {

    static final int PORT = 1234; //delare port
    private DatagramSocket socket = null; //declare socket

    public static String database;
    public static String username;
    public static String password;
    private DBAccess acc = null;

    public Server() {
        try {
            socket = new DatagramSocket(PORT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void action() {
        InetAddress host = null;
        int port;

        try {
            System.out.println("Server is listening...");
            while (true) {
                DatagramPacket packet = receive();
                host = packet.getAddress();
                port = packet.getPort();
                int action = getAction(packet);
                System.out.println(action);

                switch (action) {
                    case Constants.CONNECT_TO_SERVER:
                        send(String.valueOf(Constants.SUCCESS), host, port);
                        break;
                    case Constants.CONNECT_TO_DB:
                        connectToDatabase(packet, host, port);
                        break;
                    case Constants.ADD_NEW_STUDENT:
                        addNewStudent(packet, host, port);
                        break;
                    case Constants.GET_ALL_FACULTIES:
                        getAllFaculties(host, port);
                        break;
                    case Constants.GET_CLASSES_BY_FACULTY_ID:
                        getClassesByFacultyId(packet, host, port);
                        break;
                    case Constants.GET_STUDENTS_WITH_MARKS:
                        getStudentsWithMarks(packet, host, port);
                        break;
                    case Constants.ADD_MARK_BY_STUDENT_ID:
                        addMarks(packet, host, port);
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public int getAction(DatagramPacket packet) {
        String dataReceive = new String(packet.getData()).trim();
        return Integer.parseInt(dataReceive.split("@")[0]);
    }

    public static void main(String[] args) {
        new Server().action();
    }

    private DatagramPacket receive() throws IOException {
        byte[] buffer = new byte[655507];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet;
    }

    private void send(String chuoi, InetAddress host, int port) throws IOException {
        byte[] buffer = chuoi.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, port);
        socket.send(packet);
    }

    private void connectToDatabase(DatagramPacket packet, InetAddress host, int port) {
        String dataReceive = new String(packet.getData()).trim();
        String[] dataUser = dataReceive.split("#");
        database = "qlsv";
        username = dataUser[1];
        password = dataUser[2];
        acc = new DBAccess(username, password);
        if (acc.check) {
            try {
                send(String.valueOf(Constants.SUCCESS), host, port);
            } catch (Exception ex) {

            }
        } else {
            try {
                send(String.valueOf(Constants.FAILED), host, port);
            } catch (Exception ex) {

            }

        }
    }

    private void addNewStudent(DatagramPacket packet, InetAddress host, int port) {
        String dataReceive = new String(packet.getData()).trim();
        String newStudent = dataReceive.split("@")[1];
        int result = acc.addStudent(newStudent);

        try {
            if (result != -1) {
                send(String.valueOf(Constants.SUCCESS), host, port);
            } else {
                send(String.valueOf(Constants.FAILED), host, port);
            }
        } catch (Exception ex) {

        }
    }

    private void getAllFaculties(InetAddress host, int port) throws SQLException {
        String query = "{CALL GetAllKhoa()}";
        String result = acc.getAllFaculties(query);
        try {
            if (result.isEmpty()) {
                send(String.valueOf(Constants.FAILED), host, port);
            } else {
                send(result, host, port);
            }
        } catch (Exception ex) {

        }
    }

    private void getClassesByFacultyId(DatagramPacket packet, InetAddress host, int port) {
        String dataReceive = new String(packet.getData()).trim();
        String faculty = dataReceive.split("@")[1];
        String idFaculty = faculty.split(",")[0];
        String result = acc.getClassByFacultyId(idFaculty);

        try {
            if (result.isEmpty()) {
                send(String.valueOf(Constants.FAILED), host, port);
            } else {
                send(result, host, port);
            }
        } catch (Exception ex) {

        }
    }

    private void getStudentsWithMarks(DatagramPacket packet, InetAddress host, int port) throws SQLException {
        String result = acc.getAllStudentWithMark();
        
        try {
            if (result.isEmpty()) {
                send(String.valueOf(Constants.FAILED), host, port);
            } else {
                send(result, host, port);
            }
        } catch (Exception ex) {

        }
    }

    private void addMarks(DatagramPacket packet, InetAddress host, int port) {
        String dataReceive = new String(packet.getData()).trim();
        String data = dataReceive.split("@")[1];
        int result = acc.addMarks(data);

        try {
            if (result != -1) {
                send(String.valueOf(Constants.SUCCESS), host, port);
            } else {
                send(String.valueOf(Constants.FAILED), host, port);
            }
        } catch (Exception ex) {

        }
    }

}
