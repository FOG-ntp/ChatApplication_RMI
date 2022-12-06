package function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import message.Message;

public class Client extends Thread {

    private Socket socket;
    private String userName;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ImageIcon profile;
    private int ID;
    private String time;

    public Client(Socket socket) {
        this.socket = socket;
        execute();
    }

    private void execute() {
        this.start();
    }

    @Override
    public void run() {
        try {
            //Sử dụng lớp ObjectInputStream để đọc tin nhắn
            in = new ObjectInputStream(socket.getInputStream());
            //Sử dụng lớp ObjectOutputStream để show tin nhắn
            out = new ObjectOutputStream(socket.getOutputStream());
            //Nêu xác nhận ID thì thực hiện addClient thông qua function Method
            ID = Method.addClient(this);
            //  Vòng lắp bắt đầu nhận tin nhắn từ Client
            while (true) {
                //Đọc tin nhắn từ input stream
                Message ms = (Message) in.readObject();
                //getStatus
                String status = ms.getStatus();
                //Nếu status có giá trị New thì thực hiện :
                if (status.equals("New")) {
                    //get những giá trị như Name, Time, ImageProfile
                    userName = ms.getName().split("!")[0];
                    time = ms.getName().split("!")[1];
                    profile = ms.getImage();
                    //Hiển thị Text với nội dung .... trên khung quản lí Server
                    Method.getTxt().append("Người dùng mới : " + userName + " đã kết nối ...\n");
                    // Danh sách tất cả người dùng gửi cho client mới đăng nhập
                    for (Client client : Method.getClients()) {
                        ms = new Message();
                        ms.setStatus("New");
                        ms.setID(client.getID());
                        ms.setName(client.getUserName() + "!" + client.getTime());
                        ms.setImage(client.getProfile());
                        out.writeObject(ms);
                        out.flush();//Xóa nội dung bộ đêm của output stream
                    }
                    // gửi client mới đến client cũ
                    for (Client client : Method.getClients()) {
                        if (client != this) {
                            ms = new Message();
                            ms.setStatus("New");
                            ms.setName(userName + "!" + time);
                            ms.setID(ID);
                            ms.setImage(profile);
                            client.getOut().writeObject(ms);
                            client.getOut().flush();
                        }
                    }
                    //Khi status mang giá trị File thực hiện :
                } else if (status.equals("File")) {
                    //khởi tạo giá trị FileID và get thông qua function Method 
                    int fileID = Method.getFileID();
                    //truyền vào biến fileN là tên file tên file
                    String fileN = ms.getName();
                    SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyhhmmssaa");
                    String fileName = fileID + "!" + df.format(new Date()) + "!" + ms.getName().split("!")[0];
                    Method.getTxt().append(fileName);
                    FileOutputStream output = new FileOutputStream(new File("data/" + fileName));
                    output.write(ms.getData());
                    output.close();
                    Method.setFileID(fileID + 1);
                    ms = new Message();
                    ms.setStatus("File");
                    ms.setName(fileID + "!" + fileN);
                    ms.setImage((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(new File("data/" + fileName)));
                    ms.setID(ID);
                    for (Client client : Method.getClients()) {
                        client.getOut().writeObject(ms);
                        client.getOut().flush();
                    }
                } else if (status.equals("download")) {
                    sendFile(ms);
                } else {
                    for (Client client : Method.getClients()) {
                        client.getOut().writeObject(ms);
                        client.getOut().flush();
                    }
                }
            }

        } catch (Exception e) {
            try {
                //Remove người dùng khỏi server đang chạy
                Method.getClients().remove(this);
                //Hiển thị message thông báo người dùng đó rời khỏi server
                Method.getTxt().append("Người dùng có tên : " + userName + " đã ra khỏi máy chủ này ...\n");
                for (Client s : Method.getClients()) {
                    Message ms = new Message();
                    ms.setStatus("Error");
                    ms.setID(ID);
                    ms.setName(userName);
                    s.getOut().writeObject(ms);
                    s.getOut().flush();
                }
            } catch (Exception e1) {
                Method.getTxt().append("Error : " + e1);
            }
        }
    }

    private void sendFile(Message ms) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String fID = ms.getMessage();
                File file = new File("data");
                for (File f : file.listFiles()) {
                    if (f.getName().startsWith(fID)) {
                        try {
                            //Đọc file thông qua lớp FleInputStream
                            FileInputStream ins = new FileInputStream(f);
                            byte data[] = new byte[ins.available()];
                            ins.read(data);
                            ins.close();
                            //sau khi đọc file xong sẽ thiếtlaajp data và trạng thái của file đó
                            ms.setData(data);
                            ms.setStatus("GetFile");
                            out.writeObject(ms);
                            out.flush();
                            break;
                        } catch (Exception e) {
                            //  send to client error

                        }
                    }
                }
            }
        }).start();
    }

    
    //Getter Setter lấy giá trị các thuộc tính như Socket, Username, ObjectInputStream,ObjectOutStream, Time, ID,Profile
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ImageIcon getProfile() {
        return profile;
    }

    public void setProfile(ImageIcon profile) {
        this.profile = profile;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

}
