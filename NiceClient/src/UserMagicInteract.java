
import sourse.Coordinates;
import sourse.Person;
import sourse.StudyGroup;
import sourse.enums.Color;
import sourse.enums.Country;
import sourse.enums.FormOfEducation;
import sourse.enums.Semester;

import java.io.BufferedReader;
import java.io.IOException;

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
        //System.out.println(response);
       /* Map<String, Consumer<String>> consumers = Map.of(
                "info", this::info,
                "show", this::show,
                "add", this::add,
                "add_if_max", this::add,
                "update", this::update,
                "remove_by_id", this::removeByID,
                "head", this::head,
                "average_of_average_mark", this::averageOfAverageMark,
                "average_of_average_mark", this::countLessAndSoOn,
                "print_field_ascending_semester_enum", this::printFieldAndSoOn
                );*/

        switch (response.split(" ", 3)[0]) {
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
        System.out.print("Дата иницализации коллекции: ");
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
        } else System.out.println("3начение элемента успешно не обновлено так как passportId неверный.");
    }

    private void removeByID(String response) {
        String[] strings = response.split(" ");
        if (strings[1].equals("Succeed")) {
            System.out.println("Элемент удалён.");
        } else System.out.println("У админа папа депутат. Ни в коем случае нельзя удалять!");
    }

    private void clear() {
        System.out.println("Природа очистилась на столько, что из StudyGroup все самоизолировались.");
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

    public StudyGroup getStudyGroup() throws CtrlDException {
        String name;
        int x;
        int y;
        long studentsCount;
        float averageMark;
        FormOfEducation formOfEducation = null;
        Semester semester = null;
        String adminName;
        float weight;
        String passportId;
        Color eyeColor = null;
        Country nationality = null;

        do {
            System.out.print("Введите имя группы: ");
            name = getNewLine();
            if (isScript) System.out.println(name);
            if (name.equals("")) {
                System.out.println("Строка не может быть пустой");
            }
        } while (name.equals(""));

        do {
            System.out.print("Введите координату x: ");
            String line = getNewLine();
            if (isScript) System.out.println(line);
            if (line.matches("-?\\d{1,10}")) {
                x = Integer.parseInt(line);
                break;
            }
            System.out.println("Формат ввода неверный");
        } while (true);

        do {
            System.out.print("Введите координату y: ");
            String line = getNewLine();
            if (isScript) System.out.println(line);
            if (line.matches("-?\\d{1,10}") && Integer.parseInt(line) > -791) {
                y = Integer.parseInt(line);
                break;
            }
            System.out.println("Формат ввода неверный");
        } while (true);

        do {
            System.out.print("Введите количество студентов в группе: ");
            String line = getNewLine();
            if (isScript) System.out.println(line);
            if (line.matches("\\d{1,10}")) {
                studentsCount = Long.parseLong(line);
                break;
            }
            System.out.println("Формат ввода неверный");
        } while (true);
        do {
            System.out.print("Введите средний балл студентов: ");
            String line = getNewLine();
            if (line.matches("\\d{0,10}\\.?\\d{1,10}")) {
                averageMark = Float.parseFloat(line);
                break;
            }
            System.out.println("Формат ввода неверный");
        } while (true);

        do {
            System.out.print("Введите форму обучения: ");
            String line = getNewLine();
            if (line.equals("")) {
                break;
            } else {
                try {
                    formOfEducation = Enum.valueOf(FormOfEducation.class, line);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Такой формы обучения нет");
                }
            }
        } while (true);

        do {
            System.out.print("Введите номер семестра: ");
            String line = getNewLine();
            if (line.equals("")) {
                break;
            } else {
                try {
                    semester = Enum.valueOf(Semester.class, line);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Такого номера семестра нет");
                }
            }
        } while (true);


        do {
            System.out.print("Введите имя админа группы: ");
            adminName = getNewLine();
            if (isScript) System.out.println(adminName);
            if (adminName.equals("")) {
                System.out.println("Строка не может быть пустой");
            }
        } while (adminName.equals(""));

        do {
            System.out.print("Введите вес админа: ");
            String line = getNewLine();
            if (isScript) System.out.println(line);
            if (line.matches("\\d{0,10}\\.?\\d{1,10}")) {
                weight = Float.parseFloat(line);
                break;
            }
            System.out.println("Формат ввода неверный");
        } while (true);

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

        do {
            System.out.print("Введите цвет глаз админа: ");
            String line = getNewLine();
            if (line.equals("")) {
                break;
            } else {
                try {
                    eyeColor = Enum.valueOf(Color.class, line);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Формат ввода неверный");
                }
            }
        } while (true);

        do {
            System.out.print("Введите национальность админа: ");
            String line = getNewLine();
            if (line.equals("")) {
                break;
            } else {
                try {
                    nationality = Enum.valueOf(Country.class, line);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Такой страны нет");
                }
            }
        } while (true);

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
                "поставить свечку стоит 50 рублей"};
        int i = (int) (Math.random() * quotes.length);
        System.out.println("Здравствуйте! Знаете ли вы, что " + quotes[i] + "?");
    }


}

class CtrlDException extends Exception {
}

