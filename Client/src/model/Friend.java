package model;

import javax.swing.ImageIcon;

public interface Friend {
    //Vì java không hỗ trợ đa kế thừa nên không thể kế thừa cùng lúc nhiều class
    //Để giải quyết vấn đề đó a sử dụng Interface
    //Cụ thể là Người dùng thì có nhiều IS, Name, Thời gian truy cập khác nhau 
    //nên Interface giúp kế thừa từng đặc điểm riêng đó
    public void set(ImageIcon image, int ID, String name, String time);

    public ImageIcon getImage();

    public String getfName();
}
