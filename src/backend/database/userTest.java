package backend.database;

import java.io.UnsupportedEncodingException;

public class userTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        UserDAO userDAO = new UserDAO("testuser","pass1");
        System.out.println(userDAO.getHashedPassword());
        System.out.println(userDAO.verifyPassword("feil"));
        System.out.println(userDAO.verifyPassword("pass1"));
    }
}
