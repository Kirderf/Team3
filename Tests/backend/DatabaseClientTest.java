package backend;

import backend.database.DatabaseClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseClientTest {
    DatabaseClient databaseClient;
    DatabaseClientTest() throws IOException {
        databaseClient = DatabaseClient.getInstance();
    }

    @BeforeEach
    void setUp(){

    }

}