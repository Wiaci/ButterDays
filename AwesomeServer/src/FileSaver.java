import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Scanner;

public class FileSaver {
    /**
     * Метод загружает коллекцию из файла
     * @param filename имя файла
     */
    public static CommandProcessor load(String filename) {
        CommandProcessor collection = new CommandProcessor();

        try (Reader r = new FileReader(filename);
             BufferedReader bf = new BufferedReader(r)) {
            JAXBContext context = JAXBContext.newInstance(CommandProcessor.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            collection = (CommandProcessor) unmarshaller.unmarshal(bf);

        } catch (FileNotFoundException e) {
            System.out.println("Файл, содержащий коллекцию, отсутствует (возможно, у вас недостаточно прав).\n" +
                    "Будет создана новая коллекция");
            collection = new CommandProcessor();
        } catch (JAXBException e) {
            System.out.println("Файл, содержащий коллекцию, пуст. Будет создана новая коллекция");
            collection = new CommandProcessor();
        } catch (IOException e) {
            System.out.println("Юзер, вы чудовище!");
            Scanner iKnow = new Scanner(System.in);
            String answer;
            do {
                answer = iKnow.nextLine().trim();
                if (!answer.equals("Я знаю")) {
                    e.printStackTrace();
                    continue;
                }
                break;
            } while (true);
        }
        return collection;
    }
    /**
     * Метод сохраняет коллекцию
     * @param collection коллекция, которую нужно сохранить
     * @param collectionFilename имя файла куда нужно сохранить
     */
    public static void save(CommandProcessor collection, String collectionFilename) {
        try (Writer w = new FileWriter(collectionFilename);
             Writer bw = new BufferedWriter(w)) {
            JAXBContext context = JAXBContext.newInstance(CommandProcessor.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(collection, bw);
        } catch (JAXBException e) {
            System.out.println("Сохранение невозможно");
        } catch (IOException e) {
            System.out.println("Нет прав на сохранение коллекции в файл");
        }
    }
}
