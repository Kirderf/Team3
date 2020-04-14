package backend.database;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * The type User dao.
 */
@Entity
public class UserDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @OneToMany(targetEntity = ImageDAO.class)
    private long accountID;
    private String username;
    private String hashedPassword;
    private String saltString;


    /**
     * Instantiates a new User dao.
     *
     * @param username the username
     * @param password the password
     */
    UserDAO(String username, String password) {

        //generates random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        //convert password to char arra
        char[] passwordChars = password.toCharArray();
        //password hashed with salt
        byte[] hashedBytes = hashPassword(passwordChars, salt);

        //convert hashed password to string to store in datbase
        String hashedString = Hex.encodeHexString(hashedBytes);
        //convert byte to string
        this.saltString = org.apache.commons.codec.binary.Base64.encodeBase64String(salt);
        this.username = username;
        this.hashedPassword = hashedString;
    }

    /**
     * Instantiates a new User dao.
     */
    public UserDAO() {
    }

    /**
     * Get account id long.
     *
     * @return the long
     */
    long getAccountID() {
        return this.accountID;
    }

    /**
     * Verify password boolean.
     *
     * @param testPassword the test password
     * @return the boolean
     */
    boolean verifyPassword(String testPassword) {
        //the password that is to be tested
        char[] passwordChars = testPassword.toCharArray();
        //salt string from the implicit parameter
        byte[] saltBytes = Base64.decodeBase64(saltString);
        //hashes the password that is to be tested
        byte[] hashedBytes = hashPassword(passwordChars, saltBytes);
        //converts to string to compare to string in databse
        String hashedString = Hex.encodeHexString(hashedBytes);
        //if the outputs are equal, then the passwords are equal
        return (hashedString.equals(hashedPassword));
    }

    private byte[] hashPassword(final char[] password, final byte[] salt) {
        //high iterations slows down algorithm
        int iterations = 10000;
        int keyLength = 512;

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            //encodes password
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            return key.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    String getUsername() {
        return username;
    }

}
