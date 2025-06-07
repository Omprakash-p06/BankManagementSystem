import org.mindrot.jbcrypt.BCrypt;

public class TestHashGenerator {
    public static void main(String[] args) {
        String plaintextPassword = "idk@123"; // <--- CHANGE THIS!
        String hashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
        System.out.println("Plaintext: " + plaintextPassword);
        System.out.println("Hashed: " + hashedPassword);
        // Copy the 'Hashed' value for your SQL INSERT
    }
}