package Admin;

public class AdminTable {
    Integer num;
    String username;
    String role;

    public AdminTable (Integer num, String username, String role) {
        this.num = num;
        this.username = username;
        this.role = role;
    }

    //set
    public void setNum(Integer num) {
        this.num = num;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    //getter
    public Integer getNum() {
        return num;
    }
    public String getRole() {
        return role;
    }
    public String getUsername() {
        return username;
    }
}
