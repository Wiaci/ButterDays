import packets.AwesomeToNicePacket;
import sourse.*;
import sourse.enums.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class UserMagicInteract {

    BufferedReader reader;
    boolean isScript;

    public UserMagicInteract(BufferedReader reader, boolean isScript) {
        this.reader = reader;
        this.isScript = isScript;
    }

    public String[] getNewCommand() throws CtrlDException {
        return getNewLine().trim().split(" ");
    }

    public void printResponse(AwesomeToNicePacket packet) {
        String response = packet.getResponse();
        switch (response.split(" ", 3)[0]) {
            case "butterDays":
                System.out.println("Возникла проблема с базой данных");
            case "info": info(response); break;
            case "show": show(response); break;
            case "add":
            case "add_if_max":
                add(response); break;
            case "update": update(response); break;
            case "remove_by_id": removeByID(response); break;
            case "remove_greater": removeGreater(response); break;
            case "clear": clear(); break;
            case "head": head(response); break;
            case "average_of_average_mark": averageOfAverageMark(response); break;
            case "count_less_than_form_of_education" +
                    "": countLessAndSoOn(response); break;
            case "print_field_ascending_semester_enum": printFieldAndSoOn(response); break;
        }
    }

    private void info(String response) {
        String[] strings = response.split(" ");
        for (int i = 2; i < strings.length; i++) {
            System.out.print(strings[i] + " ");
        }
        System.out.println();
        System.out.println("Тип коллекции: LinkedList");
        System.out.println("Количество элементов в коллекции: " + strings[1]);
    }

    private void show(String response) {
        String[] strings = response.split(" ");
        if (strings.length > 1) {
            System.out.println("Элементы коллекции: ");
            for (int i = 1; i < strings.length; i++) {
                System.out.print(strings[i] + " ");
            }
            System.out.println();
        } else System.out.println("Коллекция пуста");
    }

    private void add(String response) {
        String[] strings = response.split(" ");
        if (strings.length < 3) {
            System.out.println("Элемент успешно добавлен");
        } else if (strings[2].equals("passport")) {
            System.out.println("Элемент успешно не добавлен так как passportId неверный");
        } else System.out.println("Элемент успешно не добавлен так как элемент не является максимальным");
    }

    private void update(String response) {
        String[] strings = response.split(" ");
        if (strings.length < 3) {
             System.out.println("3начение элемента успешно обновлено.");
        } else if (strings[2].equals("id")) {
            System.out.println("3начение элемента успешно не обновлено так как id неверный.");
        } else if (strings[1].equals("no")) System.out.println("У вас нет прав на редактирование группы с этим id");
        else System.out.println("3начение элемента успешно не обновлено так как passportId неверный.");
    }

    private void removeByID(String response) {
        String[] strings = response.split(" ");
        if (strings[1].equals("Succeed")) {
            System.out.println("Элемент удалён.");
        } else if (strings[1].equals("no")) System.out.println("У вас нет прав на редактирование группы с этим id");
        else System.out.println("У админа папа депутат. Ни в коем случае нельзя удалять!");
    }

    private void clear() {
        System.out.println("Природа очистилась на столько, что из StudyGroup самоизолировались все ваши группы.");
    }

    private void head(String response) {
        String[] strings = response.split(" ");
        if (strings.length > 3) {
            System.out.println("Первый элемент: ");
            for (int i = 1; i < strings.length; i++) {
                System.out.println(strings[i] + " ");
            }
        }
        System.out.println("Коллекция пустая, как полки с туалетной бумагой в магазине.");
    }

    private void removeGreater(String response) {
        String[] strings = response.split(" ");
        System.out.println("Удалённые элементы: ");
        for (int i = 1; i < strings.length; i++) {
            System.out.println(strings[i] + " ");
        }
    }

    private void averageOfAverageMark(String response) {
        String[] strings = response.split(" ");
        System.out.printf("Среднее начение: %.1f", Float.parseFloat(strings[1]));
        System.out.println();
    }

    private void countLessAndSoOn(String response) {
        String[] strings = response.split(" ");
        System.out.println(strings[1] + " элементов меньше заданного.");
    }

    private void printFieldAndSoOn(String response) {
        String[] strings = response.split(" ");
        if (strings.length > 1) {
            System.out.println("3начения в порядке ворастания:");
            for (int i = 1; i < strings.length; i++) {
                System.out.println(strings[i] + ", ");
            }
        } else System.out.println("Семестры нигде не указаны");
    }

    public void help() {
        System.out.println(
                "    help : вывести справку по доступным командам\n" +
                        "    info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, " + "количество элементов и т.д.)\n" +
                        "    show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                        "    add {element} : добавить новый элемент в коллекцию\n" +
                        "    update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                        "    remove_by_id id : удалить элемент из коллекции по его id\n" +
                        "    clear : очистить коллекцию\n" +
                        "    execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                        "    exit : завершить программу (без сохранения в файл)\n" +
                        "    head : вывести первый элемент коллекции\n" +
                        "    add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
                        "    remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                        "    average_of_average_mark : вывести среднее значение поля averageMark для всех элементов коллекции\n" +
                        "    count_less_than_form_of_education formOfEducation : вывести количество элементов, значение поля formOfEducation которых меньше заданного\n" +
                        "    print_field_ascending_semester_enum semesterEnum : вывести значения поля semesterEnum в порядке возрастания");
    }

    public boolean check(String[] command) {
        String firstWord = command[0];
        switch (firstWord) {
            case "info":
            case "show":
            case "add":
            case "clear":
            case "head":
            case "add_if_max":
            case "remove_greater":
            case "average_of_average_mark":
            case "print_field_ascending_semester_enum":
                return true;
        }
        if (command.length == 1) return false;
        if (firstWord.equals("update") && command[1].matches("\\d{1,4}")) return true;
        if (firstWord.equals("remove_by_id") && command[1].matches("\\d{1,4}")) return true;
        return firstWord.equals("count_less_than_form_of_education") && (command[1].equals("DISTANCE_EDUCATION")
                || command[1].equals("FULL_TIME_EDUCATION") || command[1].equals("EVENING_CLASSES"));
    }

    public <T> T read(String helloMessage, Predicate<String> predicate, Function<String, T> function, String errorMessage ) throws CtrlDException  {
        do {
            System.out.print(helloMessage);
            String line = getNewLine();
            if(predicate.test(line)){
                return function.apply(line);
            } else {
                System.out.println(errorMessage);
            }
        } while(true);
    }

    public <T> T readEnum(String helloMessage, T[] enums, String errorMessage, boolean isNecessary) throws CtrlDException {
        do {
            System.out.print(helloMessage);
            String line = getNewLine();
            if (line.equals("") && isNecessary) {
                System.out.println(errorMessage);
                continue;
            } else if (line.equals("")) return null;
            else
                for (T t: enums) {
                    if (line.equals(t.toString()))
                        return t;
                }
            System.out.println(errorMessage);
        } while (true);
    }

    public StudyGroup getStudyGroup() throws CtrlDException {
        String name = read("Введите имя группы: ", x -> !x.equals(""), s -> s,
                "Строка не может быть пустой");
        int x = read("Введите координату x: ", s -> s.matches("-?\\d{1,10}"), Integer::parseInt,
                "Формат ввода неверный");
        int y = read("Введите координату y: ", s -> s.matches("-?\\d{1,10}") && Integer.parseInt(s) > -791,
                Integer::parseInt, "Формат ввода неверный");
        long studentsCount = read("Введите количество студентов в группе: ", s -> s.matches("\\d{1,10}"),
                Long::parseLong, "Формат ввода неверный");
        float averageMark = read("Введите средний балл студентов: ", s -> s.matches("\\d{0,10}\\.?\\d{1,10}"),
                Float::parseFloat, "Формат ввода неверный");
        FormOfEducation formOfEducation =
                readEnum("Введите форму обучения (DISTANCE_EDUCATION, FULL_TIME_EDUCATION, EVENING_CLASSES): "
                        , FormOfEducation.values(),
                "Такой формы обучения нет", false);
        Semester semester = readEnum("Введите номер семестра (FOURTH, FIFTH, SIXTH, EIGHTH): ", Semester.values(),
                "Такого номера семестра нет", false);
        String adminName = read("Введите имя админа группы: ", s -> !s.equals(""), s -> s,
                "Строка не может быть пустой");
        float weight = read("Введите вес админа: ", s -> s.matches("\\d{0,10}\\.?\\d{1,10}"),
                Float::parseFloat, "Формат ввода неверный");
        String passportId;

        do {
            System.out.print("Введите passportID админа: ");
            String line = getNewLine();
            if (isScript) System.out.println(line);
            if (line.matches(".{5,20}") || line.equals("")) {
                if (!Person.getPassportIDSet().contains(line)) {
                    passportId = line;
                    break;
                } else if (line.equals("")) {
                    passportId = null;
                    break;
                } else {
                    System.out.println("Админ с таким passportId уже существует");
                    continue;
                }
            }
            System.out.println("Слишком длинный/короткий passportId");
        } while (true);

        Color eyeColor = readEnum("Введите цвет глаз админа (RED, YELLOW, ORANGE, BROWN): ", Color.values(),
                "Формат ввода неверный", false);
        Country nationality = readEnum("Введите национальность админа (FRANCE, SPAIN, INDIA, JAPAN): ", Country.values(),
                "Такой страны нет", true);

        return new StudyGroup(name, new Coordinates(x, y), studentsCount, averageMark, formOfEducation, semester,
                new Person(adminName, weight, passportId, eyeColor, nationality));
    }

    private String getNewLine() throws CtrlDException {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            System.out.println("Aga!");
        }
        if (line != null) {
            return line;
        } else {
            throw new CtrlDException();
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
                "поставить свечку стоит 50 рублей", "самое сложное - это самое сложное"};
        int i = (int) (Math.random() * quotes.length);
        System.out.println("Здравствуйте! Знаете ли вы, что " + quotes[i] + "?");
    }

    public String[] getLoginAndPassword() throws CtrlDException {
        System.out.print("Введите логин: ");
        String login = getNewLine();
        System.out.print("Введите пароль: ");
        String password = getNewLine();
        return new String[] {login, password};
    }

    public String getEMail() throws CtrlDException {
        System.out.print("Введите свою почту(gmail): ");
        return getNewLine();
    }

    public boolean wantToRegister() throws CtrlDException {
        System.out.println("Не хотите зарегистрироваться?(y/n)");
        if (!getNewLine().equals("y")) {
            System.out.println("Ну как хотите");
            return false;
        } else {
            System.out.println("Добро пожаловать!");
            return true;
        }
    }
}

class CtrlDException extends Exception { }