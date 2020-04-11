package backend.database;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

@Entity
public  class UserDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int accountID;
    private String username;
    private String hashedPassword;
    private String saltString;
    @Transient
    private static int iterations = 10000;
    @Transient
    private static int keyLength = 512;


    public UserDAO(String username, String password){
        //high iterations slows down algorithm
        //hashes password with salt

        //generates random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);


        char[] passwordChars = password.toCharArray();
        //password hashed with salt
        byte[] hashedBytes = hashPassword(passwordChars, salt);

        String hashedString = Hex.encodeHexString(hashedBytes);
        //convert byte to string
        this.saltString = org.apache.commons.codec.binary.Base64.encodeBase64String(salt);
        this.username = username;
        this.hashedPassword = hashedString;
        System.out.println(this.saltString);
    }
    public UserDAO(){
    }
    public int getAccountID(){
        return this.accountID;
    }

    public boolean verifyPassword(String testPassword){
        char[] passwordChars = testPassword.toCharArray();
        byte[] saltBytes = Base64.decodeBase64(saltString);
        byte[] hashedBytes = hashPassword(passwordChars, saltBytes);
        //System.out.println(Arrays.toString(saltBytes));
        String hashedString = Hex.encodeHexString(hashedBytes);
        return (hashedString.equals(hashedPassword));
    }

    private byte[] hashPassword(final char[] password, final byte[] salt) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSaltString() {
        return saltString;
    }

    public void setSaltString(String saltString) {
        this.saltString = saltString;
    }

}
