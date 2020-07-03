package GUI;

import ClientServerCommunicaion.packets.AwesomeToNicePacket;
import ClientServerCommunicaion.sourse.Coordinates;
import ClientServerCommunicaion.sourse.Person;
import ClientServerCommunicaion.sourse.StudyGroup;
import ClientServerCommunicaion.sourse.enums.Color;
import ClientServerCommunicaion.sourse.enums.Country;
import ClientServerCommunicaion.sourse.enums.FormOfEducation;
import ClientServerCommunicaion.sourse.enums.Semester;

import javax.swing.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class UserMagicInteract {

    public StudyGroup getStudyGroup(String name, String x, String y, String studentsCount, String averageMark,
                                    String formOfEducation, String semester, String adminName, String weight,
                                    String passportId, String eyeColor, String nationality) {
        if (passportId.isEmpty()) passportId = null;
        FormOfEducation form = formOfEducation != null ? FormOfEducation.valueOf(formOfEducation) : null;
        Semester sem = semester != null ? Semester.valueOf(semester) : null;
        Color color = eyeColor != null ? Color.valueOf(eyeColor) : null;
        return new StudyGroup(name, new Coordinates(Integer.parseInt(x), Integer.parseInt(y)),
                    Long.parseLong(studentsCount), Float.parseFloat(averageMark), form, sem,
                    new Person(adminName, Float.parseFloat(weight), passportId,
                    color, Country.valueOf(nationality)));
        }

    public boolean checkField(JTextField field, Predicate<String> predicate) {
        if (predicate.test(field.getText())) {
            field.setBackground(java.awt.Color.WHITE);
            return true;
        } else {
            field.setBackground(new java.awt.Color(0xFFBCD1));
            return false;
        }
    }

    public void printHello() {
        String[] quotes = new String[]{"у втшника нет цели, только путь, наполненный страданиями",
                "раньше было лучше...",
                "я вот посидел поныл и ничего не изменилось ну клево ну круто ну я рад",
                "этот мир прогнил, и не осталось ничего, кроме страданий",
                "волк слабее льва и тигра, но в цирке волк не выступает",
                "чем старше человек, тем больше ему лет",
                "кто обзывается, тот так сам и называется",
                "курение убивает", "это ловушка Джокера",
                "что лучше уточнить этот вопрос у Студенческого офиса Университета ИТМО",
                "я прошу отчислить меня из университета по собственному желанию!",
                "на ответах майл ру фигни не скажут",
                "это не цитата, а стиль жизни", "из-за ИТМО у меня беды с башкой",
                "поставить свечку стоит 50 рублей", "самое сложное - это самое сложное",
                "труд сделал из обезьяны человека, а гравитация - месиво"};
        int i = (int) (Math.random() * quotes.length);
        System.out.println("Здравствуйте! Знаете ли вы, что " + quotes[i] + "?");
    }


}

//TODO: help : вывести справку по доступным командам
//TODO: info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
//TODO: show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении
//TODO: add {element} : добавить новый элемент в коллекцию
//TODO: update id {element} : обновить значение элемента коллекции, id которого равен заданному
//TODO: remove_by_id id : удалить элемент из коллекции по его id
//TODO: clear : очистить коллекцию
//TODO: execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
//TODO: add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
//TODO: remove_greater {element} : удалить из коллекции все элементы, превышающие заданный
//TODO: average_of_average_mark : вывести среднее значение поля averageMark для всех элементов коллекции
//TODO: count_less_than_form_of_education formOfEducation : вывести количество элементов, значение поля formOfEducation которых меньше заданного
//TODO: print_field_ascending_semester_enum : вывести значения поля semesterEnum всех элементов в порядке возрастания
