package backend;

import backend.database.DatabaseClient;
import backend.util.ImageImport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseClientTest {
    DatabaseClient databaseClient;
    DatabaseClientTest() throws IOException {
        databaseClient = DatabaseClient.getInstance();
    }
    private String testPath1 = new String("resources/worldmap.png");
    private String testPath2 = new String("resources/samplephoto.jpg");
    private String testPath3 = new String("resources/flower.jpeg");
    private File gpsImage = new File("resources/images with gps data for testing/12382975864_09e6e069e7_o.jpg");

    private String absPath1 = new File(testPath1).getAbsolutePath();
    private String absPath2 = new File(testPath2).getAbsolutePath();
    private String absPath3 = new File(testPath3).getAbsolutePath();
    private String absGpsPath = gpsImage.getAbsolutePath();
    private ArrayList<String> paths = new ArrayList<>();



    @BeforeEach
    void setUp(){
        if(!databaseClient.newUser("testUser","testPassword")){
            databaseClient.login("testUser","testPassword");
        }
        databaseClient.addImage((absPath3));
        paths.add(absPath3);
        databaseClient.addAlbum("testalbum",paths);
    }
    @AfterEach
    void tearDown(){
        databaseClient.removeImage(absPath3);
        databaseClient.removeAlbum("testalbum");
        databaseClient.removeImage((absPath1));
        databaseClient.removeImage(absPath2);
    }

    @Test
    void addImage() {
        assertEquals(1,databaseClient.getColumn("path").size());
        databaseClient.removeImage(absPath3);
        assertTrue(databaseClient.getColumn("path").isEmpty());
        databaseClient.addImage((absPath3));

    }

    @Test
    void getInstance() {
        assertTrue(databaseClient.getInstance() instanceof DatabaseClient);
        assertEquals(databaseClient.getInstance(),databaseClient);
    }

    @Test
    void getColumn() {
        //if the path gets the correct data
        assertEquals(databaseClient.getColumn("path").get(0), absPath3);
        assertEquals(0, databaseClient.getColumn("tags").size());
        assertEquals(ImageImport.getMetaData((absPath3))[0],String.valueOf(databaseClient.getColumn("file_size").get(0)));
        assertEquals(ImageImport.getMetaData((absPath3))[1],String.valueOf(databaseClient.getColumn("date").get(0)));
        assertEquals(ImageImport.getMetaData((absPath3))[2],String.valueOf(databaseClient.getColumn("height").get(0)));
        assertEquals(ImageImport.getMetaData((absPath3))[3],String.valueOf(databaseClient.getColumn("width").get(0)));
        assertEquals(Double.parseDouble(ImageImport.getMetaData((absPath3))[4]),Double.parseDouble(String.valueOf(databaseClient.getColumn("gps_latitude").get(0))));
        assertEquals(Double.parseDouble(ImageImport.getMetaData((absPath3))[5]),Double.parseDouble(String.valueOf(databaseClient.getColumn("gps_longitude").get(0))));

    }


    @Test
    void getTags() {
        assertEquals("", databaseClient.getTags(absPath3));
        databaseClient.addTag(absPath3, new String[]{"test Tag"});
        //automatically capitalized
        assertEquals("Test tag", databaseClient.getTags(absPath3));
        databaseClient.addTag(absPath3, new String[]{"tag2","tag3"});
        assertEquals("Test tag,Tag2,Tag3",databaseClient.getTags(absPath3));
    }

    @Test
    void getMetaDataFromDatabase() {
        databaseClient.addImage((absGpsPath));
        for(int i = 0; i<ImageImport.getMetaData((absGpsPath)).length;i++){
            assertTrue(Arrays.asList(databaseClient.getMetaDataFromDatabase(absGpsPath)).contains(ImageImport.getMetaData((absGpsPath))[i]));
        }
        //testpath 1 has not been added
        databaseClient.removeImage(absGpsPath);
    }

    @Test
    void addTag() {
        String path = new File(absPath3).getAbsolutePath();
        databaseClient.addTag(absPath3,new String[]{"invalid,tag"});
        assertEquals("", databaseClient.getTags(absPath3));
        databaseClient.addTag(absPath3, new String[]{"test Tag"});
        assertEquals("Test tag", databaseClient.getTags(absPath3));
        databaseClient.addTag(absPath3, new String[]{"tag2","tag3"});
        assertEquals("Test tag,Tag2,Tag3",databaseClient.getTags(absPath3));
        //add already existing tags, none of these should be added
        databaseClient.addTag(absPath3, new String[]{"tag2","tag3"});
        //tags are automatically capitalized
        assertEquals("Test tag,Tag2,Tag3",databaseClient.getTags(absPath3));
    }

    @Test
    void removeTag() {
        databaseClient.addTag(absPath3, new String[]{"test tag"});
        assertEquals("Test tag",databaseClient.getTags(absPath3));
        databaseClient.removeTag(absPath3,new String[]{"Test tag"});
        assertEquals("",databaseClient.getTags(absPath3));
    }

    @Test
    void removeImage() {
        assertEquals(databaseClient.getColumn("path").get(0), absPath3);
        databaseClient.removeImage(absPath3);
        //the only image should have been added
        assertEquals(0,databaseClient.getColumn("path").size());
        //add it back for later tests
        databaseClient.addImage((absPath3));
    }

    @Test
    void search() {
        //should not find it
        assertEquals(0,databaseClient.search(testPath1,"path").size());
        assertEquals(new File(absPath3).getPath(),databaseClient.search("flower","path").get(0));
        databaseClient.addTag(absPath3,new String[]{"searchtag"});

        assertEquals(new File(absPath3).getPath(),databaseClient.search("searchtag","tags").get(0));
        databaseClient.removeTag(absPath3,new String[]{"searchtag"});
        assertEquals(new File(absPath3).getPath(),databaseClient.search("36287","metadata").get(0));

        assertEquals(new File(absPath3).getPath(),databaseClient.search("20200323","metadata").get(0));
        assertEquals(new File(absPath3).getPath(),databaseClient.search("477","metadata").get(0));
        assertEquals(new File(absPath3).getPath(),databaseClient.search("500","metadata").get(0));
        assertEquals(new File(absPath3).getPath(),databaseClient.search("0.0","metadata").get(0));
        assertEquals(new File(absPath3).getPath(),databaseClient.search("0.0","metadata").get(0));

        assertEquals(0,databaseClient.search("nonexistent/path","path").size());
        assertEquals(0,databaseClient.search("999999","metadata").size());
        assertEquals(0,databaseClient.search("no such tag","tags").size());
    }

    @Test
    void sort() {
        List<String> addedPaths = new ArrayList<>();
        addedPaths.add(absPath1);
        addedPaths.add(absPath2);
        addedPaths.add(absPath3);

        databaseClient.addImage((absPath1));
        databaseClient.addImage((absPath2));
        for (String s : addedPaths){
            assertTrue(databaseClient.getColumn("path").contains(s));
        }
        //file size
        Collections.sort(addedPaths, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(ImageImport.getMetaData((o1))[0])- Integer.parseInt(ImageImport.getMetaData((o2))[0]);
            }
        });
        assertEquals(addedPaths,databaseClient.sort("file_size"));
        //date
        Collections.sort(addedPaths, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return ImageImport.getMetaData((o1))[1].compareTo(ImageImport.getMetaData((o2))[1]);
            }
        });
        assertEquals(addedPaths,databaseClient.sort("date"));
        //filename
        addedPaths.sort(Comparator.comparing(o -> o.substring(o.lastIndexOf(File.separator))));
        assertEquals(addedPaths,databaseClient.sort("filename"));

        //path
        Collections.sort(addedPaths, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        assertEquals(addedPaths,databaseClient.sort("path"));

    }

    @Test
    void addAndRemoveAlbum() {
        assertEquals(1,databaseClient.getAllAlbums().size());
        databaseClient.addAlbum("newTestAlbum",paths);
        assertEquals(2,databaseClient.getAllAlbums().size());
        assertThrows(IllegalArgumentException.class,()->databaseClient.addAlbum("emptyalbumtest",new ArrayList<>()));
        assertThrows(IllegalArgumentException.class,()->databaseClient.addAlbum("testalbum",paths));
        databaseClient.removeAlbum("newTestAlbum");
        assertEquals(1,databaseClient.getAllAlbums().size());
    }

    @Test
    void addPathToAlbum() {
        databaseClient.addImage((absPath1));
        ArrayList<String> pathList = new ArrayList<>();
        pathList.add(absPath1);
        databaseClient.addPathsToAlbum("testalbum",pathList);
        assertEquals(2,databaseClient.getAllAlbums().get("testalbum").size());

    }

    @Test
    void getAllAlbums() {
        assertEquals(1,databaseClient.getAllAlbums().size());
        assertEquals(paths,databaseClient.getAllAlbums().get("testalbum"));
        assertNull(databaseClient.getAllAlbums().get("noSuchAlbum"));
    }

}