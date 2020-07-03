import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ClientServerCommunicaion.sourse.Coordinates;
import ClientServerCommunicaion.sourse.Person;
import ClientServerCommunicaion.sourse.StudyGroup;
import ClientServerCommunicaion.sourse.enums.Color;
import ClientServerCommunicaion.sourse.enums.Country;
import ClientServerCommunicaion.sourse.enums.FormOfEducation;
import ClientServerCommunicaion.sourse.enums.Semester;

import java.sql.*;
import java.util.LinkedList;

public class CollectionSaver {

    private static Logger logger = LoggerFactory.getLogger(CollectionSaver.class);
    private static Connection connection;

    public static void setConnection(Connection connection) {
        CollectionSaver.connection = connection;
    }

    public static LinkedList<StudyGroup> load() throws SQLException {
        Statement getAll = connection.createStatement();
        ResultSet studyGroupsSet = getAll.executeQuery("SELECT * FROM STUDY_GROUP;");
        LinkedList<StudyGroup> collection = new LinkedList<>();
        if (!studyGroupsSet.first()) return collection;
        do {
            int id = studyGroupsSet.getInt("id");
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
            int adminId = studyGroupsSet.getInt("person_id");
            ResultSet admin = getAll.executeQuery("select * from person where id=" + adminId);
            admin.first();
            String adminName = admin.getString("name");
            int weight = admin.getInt("weight");
            String passportId = admin.getString("passport_id");
            checkForNull = admin.getString("eye");
            Color color = null;
            if (checkForNull != null) color = Color.valueOf(checkForNull);
            Country country = Country.valueOf(admin.getString("nationality"));
            collection.add(new StudyGroup(name, new Coordinates(x, y), studentsCount, averageMark, formOfEducation, semester,
                    new Person(adminName, weight, passportId, color, country)));
        } while (studyGroupsSet.next());
        return collection;
    }
}
