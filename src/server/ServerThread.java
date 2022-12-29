package server;


import server.utils.DES;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.Scanner;

public class ServerThread implements Runnable {

    private Scanner input = null;
    private PrintWriter output = null;
    private Socket socket;
    private String name;
    private ResultSet rs;

    public ServerThread(Socket socket, String string) throws IOException {
        this.socket = socket;
        this.name = string;
        this.input = new Scanner(this.socket.getInputStream());
        this.output = new PrintWriter(this.socket.getOutputStream(), true);

        new Thread(this).start();
    }

    public static final int KETNOI = 1;
    public static final int SQL = 2;
    public static final int NHAP = 3;
    public static final int HIENTHI = 4;
    public static final int re_enter = -1;

    public static String database;
    public static String username;
    public static String password;

    private String key = "laptrinh";
    private int count = 2;

    public int lenh(String strcmd) {
        if (strcmd.equals("connect")) {
            return KETNOI;
        }
        if (strcmd.equals("SQL")) {
            return SQL;
        }
        if (strcmd.equals("input")) {
            return NHAP;
        }
        if (strcmd.equals("show")) {
            return HIENTHI;
        }
        return re_enter;
    }

    public void run() {
        Scanner sc = null;
        DBAccess acc = null;
        try {
            while (true) {
                String s = input.nextLine().trim();
                sc = null;
                String cmd = "";
                String data = "";
                try {
                    sc = new Scanner(s);
                    sc.useDelimiter("@");
                    cmd = sc.next();
                    data = sc.next();
                } catch (Exception e) {

                }

                switch (lenh(cmd)) {
                    case KETNOI:
                        String[] ketnoi = data.split("connect");
                        InetAddress host = InetAddress.getByName(ketnoi[0]);
                        int port = Integer.parseInt(ketnoi[1]);
                        System.out.println(host + " " + port);
                        
                        if (host.equals(InetAddress.getByName("localhost")) && port == 3306) {
                            output.println("TC");
                        } else {
                            output.println("TB");
                        }
                        break;

                    case SQL:
                        String[] ketnoi2 = data.split("connect");
                        database = ketnoi2[0];
                        username = ketnoi2[1];
                        password = ketnoi2[2];
//                        acc = new DBAccess();
                        if (acc.check) {
                            output.println("TC");
                        } else {
                            output.println("TB");
                        }
                        break;

                    case NHAP:
                        String[] ketnoi3 = data.split("input");
                        String hoten = ketnoi3[0];
                        String mssv = ketnoi3[1];
                        String toan = ketnoi3[2];
                        String van = ketnoi3[3];
                        String anh = ketnoi3[4];

                        //Thực hiện DES
                        String enhoten = DES.DES_CBC_Encrypt(hoten, key);
                        String enmssv = DES.DES_CBC_Encrypt(mssv, key);
                        String entoan = DES.DES_CBC_Encrypt(toan, key);
                        String envan = DES.DES_CBC_Encrypt(van, key);
                        String enanh = DES.DES_CBC_Encrypt(anh, key);

                        String query = "insert into sinhvien (hoten,mssv,toan,van,anh) values('" + enhoten + "','" + enmssv + "','" + entoan + "','" + envan + "','" + enanh + "')";
                        if (acc.Update(query) >= 0) {
                            output.println("#");
                        } else {
                            output.println("#");
                        }
                        break;
                    case HIENTHI:
                        String sql = "select *  from sinhvien";
                        
                        String kq = acc.getSV(sql);
                        if (kq != null) {
                            output.println(kq);
                        } else {
                            output.println("trungbinh");
                        }

                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(name + " has departed");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {

            }
            if (sc != null) {
                sc.close();
            }
        }
    }
}
