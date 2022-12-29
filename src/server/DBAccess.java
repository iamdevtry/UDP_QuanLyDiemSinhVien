package server;

import server.utils.DES;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import server.constants.Constants;

public class DBAccess {

    private Connection con;
    private Statement stmt;
    private Statement stmt2;
    static boolean check = false;
    private String key = "12345678";//Lưu key trong file text, rồi tạo 1 hàm đọc dữ liệu lấy ra

    public DBAccess(String username, String password) {
        try {
            MyConnection mycon = new MyConnection();
            con = mycon.getConnection(username, password);
            stmt = con.createStatement();
            stmt2 = con.createStatement();
            check = true;
        } catch (Exception e) {
            check = false;
        }
    }

    public int Update(String str) {
        try {
            int i = stmt.executeUpdate(str);
            return i;
        } catch (Exception e) {
            return -1;
        }
    }

    public ResultSet Query(String srt) {
        try {
            ResultSet rs = stmt.executeQuery(srt);
            int size = rs.getRow();
            return rs;
        } catch (Exception e) {
            return null;
        }
    }

    public String getSV(String srt) {
        StringBuilder students = new StringBuilder();
        try {
            ResultSet rs = stmt.executeQuery(srt);

            while (rs.next()) {
                String hoten3 = rs.getString("hoten");
                String mssv3 = rs.getString("mssv");
                String toan3 = rs.getString("toan");
                String van3 = rs.getString("van");
                String anh3 = rs.getString("anh");

                //Giải DES
                String dehoten = DES.DES_CBC_Decrypt(hoten3, key);
                String demssv = DES.DES_CBC_Decrypt(mssv3, key);
                String detoan = DES.DES_CBC_Decrypt(toan3, key);
                String devan = DES.DES_CBC_Decrypt(van3, key);
                String deanh = DES.DES_CBC_Decrypt(anh3, key);

                float toan4 = Float.valueOf(detoan).floatValue();
                float van4 = Float.valueOf(devan).floatValue();
                float anh4 = Float.valueOf(deanh).floatValue();

                float avg = ((toan4 + van4 + anh4) / 3);

                if (hoten3 == null) {
                    hoten3 = "";
                }
                if (mssv3 == null) {
                    mssv3 = "";
                }
                if (toan3 == null) {
                    toan3 = "";
                }
                if (van3 == null) {
                    van3 = "";
                }
                if (anh3 == null) {
                    anh3 = "";
                }
                String student = dehoten.trim() + "," + demssv.trim() + "," + String.format("%.2f", avg) + "#";
                students.append(student);
                String kq = students.toString();
            }

        } catch (Exception e) {
            String err = e.toString();
            return null;
        }
        return students.toString();
    }

    public String getAllFaculties(String srt) {
        StringBuilder faculties = new StringBuilder();
        try {
            ResultSet rs = stmt.executeQuery(srt);

            while (rs.next()) {
                String maKhoa = rs.getString("MaKhoa");
                String tenKhoa = rs.getString("TenKhoa");

                //Giải DES
                String decrMaKhoa = DES.DES_CBC_Decrypt(maKhoa, key);
                String decrTenKhoa = DES.DES_CBC_Decrypt(tenKhoa, key);

                if (maKhoa == null) {
                    maKhoa = "";
                }
                if (tenKhoa == null) {
                    tenKhoa = "";
                }

                String falcuty = decrMaKhoa.trim() + "," + decrTenKhoa.trim() + "#";
                faculties.append(falcuty);
                String kq = faculties.toString();
            }

        } catch (Exception e) {
            String err = e.toString();
            return null;
        }
        return faculties.toString();
    }

    public String getClassByFacultyId(String idFaculty) {
        String encrIdFaculty = DES.DES_CBC_Encrypt(idFaculty, key);

        StringBuilder classes = new StringBuilder();
        String query = "{CALL getClassByFacultyId('" + encrIdFaculty + "')}";
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String maLop = rs.getString("MaLop");

                //Giải DES
                String decrMaLop = DES.DES_CBC_Decrypt(maLop, key);

                if (maLop == null) {
                    maLop = "";
                }

                String c = decrMaLop.trim() + "#";
                classes.append(c);
                String kq = classes.toString();
            }

        } catch (Exception e) {
            String err = e.toString();
            return null;
        }
        return classes.toString();
    }

    public int addStudent(String data) {
        String maSV = data.split("#")[0];
        String hoSV = data.split("#")[1];
        String tenSV = data.split("#")[2];
        String phai = data.split("#")[3];
        String khoa = data.split("#")[4];
        String maKhoa = khoa.split(",")[0];
        String maLop = data.split("#")[5];

        System.out.println("masv: " + maSV + " hoSV: " + hoSV + " tenSV: " + tenSV + " phai: " + phai + " maKhoa: " + maKhoa + " maLop: " + maLop);

        String encrMaSV = DES.DES_CBC_Encrypt(maSV, key);
        String encrHoSV = DES.DES_CBC_Encrypt(hoSV, key);
        String encrTenSV = DES.DES_CBC_Encrypt(tenSV, key);
        String encrPhai = DES.DES_CBC_Encrypt(phai, key);
        String encrMaKhoa = DES.DES_CBC_Encrypt(maKhoa, key);
        String encrMaLop = DES.DES_CBC_Encrypt(maLop, key);

        String query = "{CALL AddNewStudent('" + encrMaSV + "','" + encrHoSV + "','" + encrTenSV + "','" + encrPhai + "','" + encrMaKhoa + "','" + encrMaLop + "')}";

        return Update(query);
//return -1;
    }

    public String getAllStudentWithMark() throws SQLException {
        String getStudentsQuery = "{CALL GetAllStudents}";
        StringBuilder students = new StringBuilder();
        try {
            ResultSet rs = stmt.executeQuery(getStudentsQuery);
            while (rs.next()) {
                String maSV = rs.getString("MaSV");
                String hoSV = rs.getString("HoSV");
                String tenSV = rs.getString("TenSV");

                //Get diem
//                String diems = getMarksByStudentId(maSV);
                //
//                String getMarksQuery = "{CALL GetMarkByStudentId('" + maSV + "')}";
                String getMarksQuery = "Select * from KetQua where MaSV = '" + maSV + "'";
                StringBuilder diems = new StringBuilder();
                ResultSet rs2 = stmt2.executeQuery(getMarksQuery);

                while (rs2.next()) {
                    String diem = rs2.getString("Diem");

                    //Giải DES
                    String decrDiem = DES.DES_CBC_Decrypt(diem, key);

                    if (diem == null) {
                        diem = "";
                    }

                    String sc = decrDiem.trim() + "&";
                    diems.append(sc);
                    String kq = diems.toString();
                }
                rs2.close();
                //

                //Giải DES
                String decrMaSV = DES.DES_CBC_Decrypt(maSV, key);
                String decrHoSV = DES.DES_CBC_Decrypt(hoSV, key);
                String decrTenSV = DES.DES_CBC_Decrypt(tenSV, key);

                if (maSV == null) {
                    maSV = "";
                }
                if (hoSV == null) {
                    hoSV = "";
                }
                if (tenSV == null) {
                    tenSV = "";
                }

                String std = decrMaSV.trim() + "," + decrHoSV.trim() + "," + decrTenSV.trim() + "," + diems + "#";
                System.out.println(std);
                students.append(std);
                String kq = students.toString();
            }

        } catch (Exception e) {
            String err = e.toString();
            return null;
        }
        return students.toString();
    }

    private String getMarksByStudentId(String id) {
        String getMarksQuery = "{CALL GetMarkByStudentId('" + id + "')}";
        StringBuilder marks = new StringBuilder();
        try {
            ResultSet rs = stmt2.executeQuery(getMarksQuery);

            while (rs.next()) {
//                String maMH = rs.getString("MaMH");
                String diem = rs.getString("Diem");

                //Giải DES
//                String decrMaKhoa = DES.DES_CBC_Decrypt(maMH, key);
                String decrTenKhoa = DES.DES_CBC_Decrypt(diem, key);

//                if (maMH == null) {
//                    maMH = "";
//                }
                if (diem == null) {
                    diem = "";
                }

//                String sc = decrMaKhoa.trim() + "," + decrTenKhoa.trim() + "&";
                String sc = decrTenKhoa.trim() + "&";
                marks.append(sc);
                String kq = marks.toString();
            }

        } catch (Exception e) {
            String err = e.toString();
            return null;
        }
        return marks.toString();
    }

    public int addMarks(String data) {
        int rs = -1;
//        System.out.println(data);//1911062076#7.5#5#9
        String maSV = data.split("#")[0];
        String toan = data.split("#")[1];
        String van = data.split("#")[2];
        String anh = data.split("#")[3];

        String encrMaSV = DES.DES_CBC_Encrypt(maSV, key);
        String encrToan = DES.DES_CBC_Encrypt(toan, key);
        String encrVan = DES.DES_CBC_Encrypt(van, key);
        String encrAnh = DES.DES_CBC_Encrypt(anh, key);

        String query1 = "Insert into KetQua values('" + encrMaSV + "','" + Constants.TOAN_ID + "','" + encrToan + "')";
        String query2 = "Insert into KetQua values('" + encrMaSV + "','" + Constants.VAN_ID + "','" + encrVan + "')";
        String query3 = "Insert into KetQua values('" + encrMaSV + "','" + Constants.ANH_ID + "','" + encrAnh + "')";
        String[] queries = new String[]{query1, query2, query3};

        for (int i = 0; i < queries.length; i++) {
            rs = Update(queries[i]);
        }
        return rs;
    }
}
