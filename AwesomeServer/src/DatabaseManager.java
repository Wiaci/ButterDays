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

    public LinkedList<StudyGroup> load() throws SQLException {
        if (!checkIntegrity()) {
            logger.info("Database is corrupted");
            return new LinkedList<>();
        };
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
            if (!admin.first()) {
                logger.info("Database is corrupted");
                return new LinkedList<>();
            };
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
        ResultSet groupAdminsIdSet = checker.executeQuery("select admin_id from study_group;");
        HashSet<Integer> groupAdminsId = new HashSet<>();
        while (groupAdminsIdSet.next())
            groupAdminsId.add(groupAdminsIdSet.getInt("admin_id"));

        ResultSet ownersLoginSet = checker.executeQuery("select owner_login from study_group;");
        HashSet<String> ownersLogin = new HashSet<>();
        while (ownersLoginSet.next())
            ownersLogin.add(ownersLoginSet.getString(1));
        for (int adminsId : groupAdminsId) {
            ResultSet personSet = checker.executeQuery("select * from person where id=" + adminsId);
            boolean isEmpty = true;
            while (personSet.next()) {
                isEmpty = false;
            }
            if (isEmpty) return false;
        }

        for (String userLogin : ownersLogin) {
            PreparedStatement checker2 = connection.prepareStatement("select * from users where login=?");
            checker2.setString(1, userLogin);
            ResultSet loginSet = checker2.executeQuery();
            boolean isEmpty = true;
            while (loginSet.next()) {
                isEmpty = false;
            }
            if (isEmpty) return false;
        }
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

    public boolean checkLogin(String login) throws SQLException {
        PreparedStatement check = connection.prepareStatement("select * from users where login=?;");
        check.setString(1, login);
        ResultSet users = check.executeQuery();
        int counter = 0;
        while (users.next()) counter++;
        return counter != 0;
    }

    public void registerUser(String login, String password) throws SQLException {
        PreparedStatement registration = connection.prepareStatement("insert into users values(?, ?)");
        registration.setString(1, login);
        registration.setString(2, password);
        registration.execute();
    }

    public boolean checkAccess(long id, String login) throws SQLException {
        PreparedStatement getLogin = connection.prepareStatement("select owner_login from study_group where id =?;");
        getLogin.setInt(1, (int) id);
        ResultSet ownerLoginSet = getLogin.executeQuery();
        String ownerLogin = "";
        while (ownerLoginSet.next()) {
            ownerLogin = ownerLoginSet.getString(1);
            System.out.println(ownerLogin);
        }
        System.out.println("ownerlogin: " + ownerLogin);
        System.out.println("login: " + login);
        return login.equals(ownerLogin);
    }



    public void addGroup(StudyGroup studyGroup, String login) throws SQLException {
        PreparedStatement addStudyGroup =
                connection.prepareStatement("insert into study_group values(" +
                        "nextval('id_seq'), ?, ?, ?, ?, ?, ?, ?, currval('id_seq'), ?, ?);");
        addStudyGroup.setString(1, studyGroup.getName());
        addStudyGroup.setInt(2, studyGroup.getX());
        addStudyGroup.setInt(3, studyGroup.getY());
        addStudyGroup.setInt(4, (int) studyGroup.getStudentsCount());
        addStudyGroup.setFloat(5, studyGroup.getAverageMark());
        addStudyGroup.setString(6, studyGroup.getFormOfEducation() != null ?
                studyGroup.getFormOfEducation().toString() : null);
        addStudyGroup.setString(7, studyGroup.getSemesterEnum() != null ? studyGroup.getSemesterEnum().toString() : null);
        addStudyGroup.setString(8, login);
        addStudyGroup.setDate(9, new java.sql.Date(Date.from(studyGroup.getDateOfCreation().toInstant()).getTime()));

        addStudyGroup.execute();

        Person person = studyGroup.getGroupAdmin();
        PreparedStatement addPerson = connection.prepareStatement("insert into person values(currval('id_seq'), ?, ?, ?, ?, ?)");
        addPerson.setString(1, person.getName());
        addPerson.setFloat(2, person.getWeight());
        addPerson.setString(3, person.getPassportID());
        addPerson.setString(4, person.getEyeColor() != null ? person.getEyeColor().toString() : null);
        addPerson.setString(5, person.getNationality().toString());
        addPerson.execute();

        Statement getIds = connection.createStatement();
        ResultSet ids = getIds.executeQuery("select id from study_group;");
        int groupId = 0;
        while(ids.next()) {
            groupId = ids.getInt("id");
        }
        studyGroup.setId((long) groupId);
    }

    public void update(int id, StudyGroup group) throws SQLException {
        PreparedStatement updater =
                connection.prepareStatement(
                        "update study_group set name=?, x=?, y=?,students_count=?, average_mark=?, form=?, sem=? where id=?;");
        updater.setString(1, group.getName());
        updater.setInt(2, group.getX());
        updater.setInt(3, group.getY());
        updater.setInt(4, (int) group.getStudentsCount());
        updater.setFloat(5, group.getAverageMark());
        updater.setString(6, group.getFormOfEducation() != null ? group.getFormOfEducation().toString() : null);
        updater.setString(7, group.getSemesterEnum() != null ? group.getSemesterEnum().toString() : null);
        updater.setInt(8, id);
        updater.execute();
        updater = connection.prepareStatement(
                        "update person set name=?, weight=?,passport_id=?, eye_color=?, nationality=? where id=?;");
        Person person = group.getGroupAdmin();
        updater.setString(1, person.getName());
        updater.setFloat(2, person.getWeight());
        updater.setString(3, person.getPassportID());
        updater.setString(4, person.getEyeColor() != null ? person.getEyeColor().toString() : null);
        updater.setString(5, person.getNationality().toString());
        updater.setInt(6, id);
        updater.execute();
    }

    public void removeGroup(long id) throws SQLException {
        connection.createStatement().execute("delete from study_group where id =" + id);
        connection.createStatement().execute("delete from person where id =" + id);
    }

    public void clear(String login) throws SQLException {
        PreparedStatement deleter = connection.prepareStatement("select id from study_group where owner_login = ?;");
        deleter.setString(1, login);
        ResultSet idToDelete = deleter.executeQuery();
        while (idToDelete.next()) {
            int id = idToDelete.getInt(1);
            connection.createStatement().execute("delete from study_group where id =" + id);
            connection.createStatement().execute("delete from person where id =" + id);
        }
    }

    public void removeGreater(StudyGroup group, String login) throws SQLException {
        long studentsCount = group.getStudentsCount();
        PreparedStatement st = connection.prepareStatement("select id from study_group where students_count > ? and owner_login = ?");
        st.setInt(1, (int) studentsCount);
        st.setString(2, login);
        ResultSet idSet = st.executeQuery();
        while (idSet.next()) {
            int id = idSet.getInt(1);
            connection.createStatement().execute("delete from study_group where id =" + id);
            connection.createStatement().execute("delete from person where id =" + id);
        }
    }
}

class ButterDaysCorruptedException extends Exception {}
