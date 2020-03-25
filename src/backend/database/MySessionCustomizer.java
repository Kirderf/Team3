package backend.database;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.Session;

public class MySessionCustomizer implements SessionCustomizer {

    private String schema = "MY_CUSTOM_NAME";
    @Override
    public void customize(Session session) throws Exception {
        session.getLogin().setTableQualifier(schema);
    }
}
