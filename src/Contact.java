
public class Contact {
    private String fname;
    private String lname;
    private String email;
    private String street;
    private String second;
    private String city;
    private String state;
    private String zip;
    private String note;
    private String phone;
    private int id;
    private Boolean isModified;

    public Contact(int id, String fname, String lname, String email, String street, String second,
                   String city, String state, String zip, String note, String phone) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.street = street;
        this.second = second;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.note = note;
        this.phone = phone;
        this.isModified = false;
    }

    public Boolean getIsModified() {
        return isModified;
    }

    public int getId() {
        return id;
    }

    public String getFname(){
        return fname;
    }

    public String getLname(){
        return lname;
    }

    public String getEmail(){
        return email;
    }

    public String getStreet(){
        return street;
    }

    public String getSecond(){
        return second;
    }

    public String getCity(){
        return city;
    }

    public String getState(){
        return state;
    }

    public String getZip(){
        return zip;
    }

    public String getNote(){
        return note;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(int id) {this.id = id;}

    public void setCity(String city) {
        this.city = city;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setModified(Boolean modified) {
        isModified = modified;
    }
}
