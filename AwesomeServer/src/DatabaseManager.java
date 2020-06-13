import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sourse.*;
import sourse.enums.*;

import java.sql.*;
import java.util.LinkedList;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/studs1";
    private static final String USER = "postgres";
    private static final String PASS = "123";
    private static Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private Connection connection;

    public DatabaseManager(String dbHost, String dbPort, String username, String password) throws SQLException {
        String dbURL = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/studs1";
        System.out.println(dbURL);
        connection = DriverManager.getConnection(dbURL, username, password);
        //connection = DriverManager.getConnection(DB_URL, USER, PASS);
        logger.info("Database connected");

    }

    public ResultSet getStudyGroups() throws SQLException {
        Statement getAll = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return getAll.executeQuery("SELECT * FROM STUDY_GROUP;");
    }

    public ResultSet getPersonById(int id) throws SQLException {
        PreparedStatement getPerson = connection.prepareStatement("select * from person where id=?",
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        getPerson.setInt(1, id);
        return getPerson.executeQuery();
    }

    public LinkedList<StudyGroup> load() throws SQLException {
        init();
        ResultSet studyGroupsSet = getStudyGroups();
        LinkedList<StudyGroup> collection = new LinkedList<>();
        if (!studyGroupsSet.first()) {
            logger.info("Коллекция пуста!");
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
            }
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
        Statement toInit = connection.createStatement();
        toInit.execute("CREATE TABLE IF NOT EXISTS USERS (\n" +
                "    LOGIN VARCHAR(50) NOT NULL PRIMARY KEY,\n" +
                "    PASS VARCHAR(50) NOT NULL\n" +
                ");");
        toInit.execute("CREATE TABLE IF NOT EXISTS STUDY_GROUP (\n" +
                "    ID INT PRIMARY KEY,\n" +
                "    NAME VARCHAR(20) NOT NULL,\n" +
                "    X INT NOT NULL,\n" +
                "    Y INT NOT NULL CHECK(Y>-791),\n" +
                "    STUDENTS_COUNT INT NOT NULL,\n" +
                "    AVERAGE_MARK REAL NOT NULL,\n" +
                "    FORM VARCHAR(20) CHECK(FORM='DISTANCE_EDUCATION' OR FORM='FULL_TIME_EDUCATION' OR FORM='EVENING_CLASSES'),\n" +
                "    SEM VARCHAR(10) CHECK(SEM='FOURTH' OR SEM='FIFTH' OR SEM='SIXTH' OR SEM='EIGHTH'),\n" +
                "    ADMIN_ID INT UNIQUE NOT NULL,\n" +
                "    OWNER_LOGIN VARCHAR(50) NOT NULL REFERENCES USERS ON DELETE CASCADE,\n" +
                "    CREATION_DATE DATE NOT NULL\n" +
                ");");
        toInit.execute("CREATE TABLE IF NOT EXISTS PERSON (\n" +
                "    ID INT PRIMARY KEY REFERENCES STUDY_GROUP ON DELETE RESTRICT,\n" +
                "    NAME VARCHAR(30) NOT NULL,\n" +
                "    WEIGHT REAL NOT NULL,\n" +
                "    PASSPORT_ID VARCHAR(20) UNIQUE CHECK(PASSPORT_ID SIMILAR TO '_{5,20}' OR PASSPORT_ID=NULL),\n" +
                "    EYE_COLOR VARCHAR(10) CHECK(EYE_COLOR='RED' OR EYE_COLOR='YELLOW' OR EYE_COLOR='ORANGE' OR EYE_COLOR='BROWN'),\n" +
                "    NATIONALITY VARCHAR(10) NOT NULL CHECK(NATIONALITY='FRANCE' OR NATIONALITY='SPAIN' OR NATIONALITY='INDIA' OR NATIONALITY='JAPAN')\n" +
                ");");
        toInit.execute("CREATE SEQUENCE IF NOT EXISTS ID_SEQ;");
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
        }
        return login.equals(ownerLogin);
    }

    public boolean isInBase(long id) throws SQLException {
        PreparedStatement getId = connection.prepareStatement("select id from study_group where id=?;");
        getId.setInt(1, (int) id);
        ResultSet idInBase = getId.executeQuery();
        return idInBase.next();
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
        PreparedStatement statement = connection.prepareStatement("delete from person where id =?");
        statement.setInt(1, (int) id);
        statement.execute();
        statement = connection.prepareStatement("delete from study_group where id =?");
        statement.setInt(1, (int) id);
        statement.execute();
    }

    public void clear(String login) throws SQLException {
        PreparedStatement deleter = connection.prepareStatement("select id from study_group where owner_login = ?;");
        deleter.setString(1, login);
        ResultSet idToDelete = deleter.executeQuery();
        while (idToDelete.next()) {
            int id = idToDelete.getInt(1);
            PreparedStatement statement = connection.prepareStatement("delete from person where id =?");
            statement.setInt(1, (int) id);
            statement.execute();
            statement = connection.prepareStatement("delete from study_group where id =?");
            statement.setInt(1, (int) id);
            statement.execute();
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
            PreparedStatement statement = connection.prepareStatement("delete from person where id =?");
            statement.setInt(1, (int) id);
            statement.execute();
            statement = connection.prepareStatement("delete from study_group where id =?");
            statement.setInt(1, (int) id);
            statement.execute();
        }
    }
}
