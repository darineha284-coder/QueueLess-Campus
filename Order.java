public class Order {
    private int token;
    private String student;
    private String pickup;
    private double total;
    private String status;

    public Order(int token, String student, String pickup, double total) {
        this.token = token;
        this.student = student;
        this.pickup = pickup;
        this.total = total;
        status = "PLACED";
    }

    public int getToken() { return token; }
    public String getStudent() { return student; }
    public String getPickup() { return pickup; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
}