package backend.database;

import java.io.UnsupportedEncodingException;

public class userTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        UserDAO userDAO = new UserDAO("testuser","feil");
        System.out.println(userDAO.verifyPassword("feil"));
        System.out.println(userDAO.verifyPassword("pass1"));
    }
}
