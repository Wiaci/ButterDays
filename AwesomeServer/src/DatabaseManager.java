import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sourse.*;
import sourse.enums.*;

import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/studs";
    private static final String USER = "postgres";
    private static final String PASS = "123";
    private static Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
        logger.info("Database connected");

    }

    public ResultSet getStudyGroups() throws SQLException {
        Statement getAll = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return getAll.executeQuery("SELECT * FROM STUDY_GROUP;");
    }

    public ResultSet getPersonById(int id) throws SQLException {
        Statement getPerson = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return getPerson.executeQuery("select * from person where id=" + id);
    }

    public LinkedList<StudyGroup> load() throws SQLException, ButterDaysCorruptedException {
        if (!checkIntegrity()) throw new ButterDaysCorruptedException();
        ResultSet studyGroupsSet = getStudyGroups();
        LinkedList<StudyGroup> collection = new LinkedList<>();
        if (!studyGroupsSet.first()) {
            logger.info("I'm there!");
            return collection;
        }
        do {
            String name = studyGroupsSet.getString("name");
            int x = studyGroupsSet.getInt("x");
            int y = studyGroupsSet.getInt("y");
            long studentsCount = studyGroupsSet.getInt("students_count");
            float averageMark = studyGroupsSet.getFloat("average_mark");
            String checkForNull = studyGroupsSet.getString("form");
            FormOfEducation formOfEducation = null;
            if (checkForNull != null) formOfEducation = FormOfEducation.valueOf(checkForNull);
            checkForNull = studyGroupsSet.getString("sem");
            Semester semester = null;
            if (checkForNull != null) semester = Semester.valueOf(checkForNull);

            ResultSet admin = getPersonById(studyGroupsSet.getInt("admin_id"));
            if (!admin.first()) throw new ButterDaysCorruptedException();
            String adminName = admin.getString("name");
            int weight = admin.getInt("weight");
            String passportId = admin.getString("passport_id");
            checkForNull = admin.getString("eye_color");
            Color color = null;
            if (checkForNull != null) color = Color.valueOf(checkForNull);
            Country country = Country.valueOf(admin.getString("nationality"));
            StudyGroup studyGroup = new StudyGroup(name, new Coordinates(x, y), studentsCount, averageMark, formOfEducation, semester,
                    new Person(adminName, weight, passportId, color, country));
            studyGroup.setId(studyGroupsSet.getLong("id"));
            collection.add(studyGroup);
        } while (studyGroupsSet.next());

        return collection;
    }

    private void init() throws SQLException {
        Statement toInit = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        toInit.execute("CREATE TABLE IF NOT EXISTS STUDY_GROUP (\n" +
                "    ID INT PRIMARY KEY,\n" +
                "    NAME VARCHAR(20) NOT NULL,\n" +
                "    X INT NOT NULL,\n" +
                "    Y INT NOT NULL CHECK(Y>-791),\n" +
                "    STUDENTS_COUNT INT NOT NULL,\n" +
                "    AVERAGE_MARK REAL NOT NULL,\n" +
                "    FORM FORM_OF_EDUCATION,\n" +
                "    SEM SEMESTER,\n" +
                "    ADMIN_ID INT UNIQUE NOT NULL,\n" +
                "    OWNER_ID INT UNIQUE NOT NULL,\n" +
                "    CREATION_DATE DATE NOT NULL\n" +
                ");");
        toInit.execute("CREATE TABLE IF NOT EXISTS PERSON (\n" +
                "    ID INT PRIMARY KEY,\n" +
                "    NAME VARCHAR(30) NOT NULL,\n" +
                "    WEIGHT REAL NOT NULL,\n" +
                "    PASSPORT_ID VARCHAR(20) UNIQUE CHECK(PASSPORT_ID SIMILAR TO '_{5,20}'),\n" +
                "    EYE_COLOR COLOR,\n" +
                "    NATIONALITY COUNTRY NOT NULL\n" +
                ");");
        toInit.execute("CREATE TABLE IF NOT EXISTS USERS (\n" +
                "    ID INT PRIMARY KEY,\n" +
                "    LOGIN VARCHAR(50) NOT NULL UNIQUE,\n" +
                "    PASS VARCHAR(50) NOT NULL UNIQUE\n" +
                ");");
    }

    private boolean checkIntegrity() throws SQLException {
        init();
        Statement checker = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        if (!checker.execute("select * from study_group;")) return false;
        if (!checker.execute("select * from person;")) return false;
        if (!checker.execute("select * from users;")) return false;
        ResultSet groupAdminsIdSet = checker.executeQuery("select admin_id from study_group;");
        HashSet<Integer> groupAdminsId = new HashSet<>();
        while (groupAdminsIdSet.next())
            groupAdminsId.add(groupAdminsIdSet.getInt("admin_id"));

        ResultSet ownersIdSet = checker.executeQuery("select owner_id from study_group;");
        HashSet<Integer> ownersId = new HashSet<>();
        while (ownersIdSet.next())
            ownersId.add(ownersIdSet.getInt("owner_id"));
        for (int adminsId : groupAdminsId)
            if (!checker.execute("select * from person where id=" + adminsId))
                return false;
        for (int userId : ownersId)
            if (!checker.execute("select * from users where id=" + userId))
                return false;
        return true;
    }

    public boolean checkUser(String login, String password) throws SQLException {
        PreparedStatement check = connection.prepareStatement("select * from users where login=? and pass=?;");
        check.setString(1, login);
        check.setString(2, password);
        ResultSet users = check.executeQuery();
        int counter = 0;
        while (users.next()) counter++;
        return counter != 0;
    }

    public void registerUser(String login, String password) throws SQLException {
        PreparedStatement registration = connection.prepareStatement("insert into users values(nextval('id_user'), ?, ?)");
        registration.setString(1, login);
        registration.setString(2, password);
        registration.execute();
    }


}

class ButterDaysCorruptedException extends Exception {}