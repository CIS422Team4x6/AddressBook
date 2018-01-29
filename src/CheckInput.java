import javax.swing.*;

public class CheckInput {
    private Book book;
    private String fname;
    private String lname;
    private String email;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private boolean haveFname;

    public CheckInput(Book book, String fname, String lname, String email, String city, String state, String zip, String phone) {
        this.book = book;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;

    }

    public boolean checkAll() {
        if (!checkFname()) {
            return false;
        }
        if (!checkLname()) {
            return false;
        }
        if (!checkCity()) {
            return false;
        }
        if (!checkState()) {
            return false;
        }
        if (!checkZip()) {
            return false;
        }
        if (!checkPhone()) {
            return false;
        }
        if (!checkEmail()) {
            return false;
        }
        return true;
    }

    private boolean checkFname() {
        boolean valid = false;
        if (!fname.equals("") && fname.matches("[a-zA-z]+")) {
            valid = true;
            haveFname = true;
            //JOptionPane.showMessageDialog(book, "Please enter a first name");
        } else if (fname.equals("")) {
            haveFname = false;
            valid = true;
            //JOptionPane.showMessageDialog(book, "Please enter a first name");
        } else {
            JOptionPane.showMessageDialog(book, "Invalid first name. Please try again");
        }

        return valid;
    }

    private boolean checkLname() {
        boolean valid = false;
        if (!lname.equals("") && lname.matches("[a-zA-z]+")) {
            valid = true;
            //JOptionPane.showMessageDialog(book, "Please enter a last name");
        } else if (lname.equals("")) {
            if (!haveFname) {
                JOptionPane.showMessageDialog(book, "Please enter a last name or a first name");
            } else {
                valid = true;
                //JOptionPane.showMessageDialog(book, "Please enter a last name");
            }
        } else {
            JOptionPane.showMessageDialog(book, "Invalid last name. Please try again");
        }

        return valid;
    }

    private boolean checkEmail() {
        boolean valid = false;
        if (!email.equals("") && email.matches("^[a-zA-z0-9_!#$%&'*+-/=?^`{|}~.,]+@{1}[a-zA-Z0-9]+.{1}[a-zA-Z]+$")) {
            valid = true;
        } else if (email.equals("")) {
            valid = true;
        } else {
            JOptionPane.showMessageDialog(book, "Invalid email address. Please try again");
        }

        return valid;
    }

    private boolean checkCity() {
        boolean valid = false;
        if (!city.equals("") && (city.matches("^[a-zA-Z]+$") || city.matches("^[a-zA-Z]+ [a-zA-Z]+$"))) {
            valid = true;
        } else if (city.equals("")) {
            valid = true;
        } else {
            JOptionPane.showMessageDialog(book, "Invalid city name. Please try again");
        }

        return valid;
    }

    private boolean checkZip(){
        boolean valid = false;
        if (!zip.equals("") && (zip.matches("^[0-9]{5}$") || zip.matches("^[0-9]{5}-[0-9]{4}$"))){
            valid = true;
        } else if (zip.equals("")) {
            valid = true;
        } else {
            int selection = JOptionPane.showConfirmDialog(book, "Entered zip is not in U.S. standard format. Are you sure to save it?",
                    "Check Zip", JOptionPane.YES_NO_OPTION);
            if (selection == 0) {
                valid = true;
            }
        }
        return valid;
    }

    private boolean checkPhone() {
        boolean valid = false;
        if (!phone.equals("")) {
            if (phone.matches("^[0-9]{10}$")) {//#########
                    /*|| phone.matches("^([0-9]{3})[0-9]{3}-[0-9]{4}$") //(###)###-####
                    || phone.matches("^([0-9]{3}) [0-9]{3}-[0-9]{4}$") //(###) ###-####
                    || phone.matches("^[0-9]{3}-[0-9]{3}-[0-9]{4}$") //###-###-####
                    || phone.matches("^[0-9]{3}.[0-9]{3}.[0-9]{4}$") //###.###.####
                    || phone.matches("^[0-9]{7}$") //#######
                    || phone.matches("^[0-9]{3}.[0-9]{4}$") //###.####
                    || phone.matches("^[0-9]{3}-[0-9]{4}$") //###-####
                    ){ */
                valid = true;
            } else {
                JOptionPane.showMessageDialog(book, "Invalid phone number. Please try again");
            }
        } else if (phone.equals("")) {
            JOptionPane.showMessageDialog(book, "Please enter a phone number");
        }
        return valid;
    }

    private boolean checkState() {
        boolean valid = false;
        if (!state.equals("") && state.matches("^[A-Z]{2}$")) {
            valid = true;
        } else if (state.equals("")) {
            JOptionPane.showMessageDialog(book, "Please enter a state in format of U.S. State abbreviation");
        }
        return valid;
    }
}
