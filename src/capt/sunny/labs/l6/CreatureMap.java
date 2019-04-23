package capt.sunny.labs.l6;


import capt.sunny.labs.l6.FileSavingException;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static capt.sunny.labs.l6.IOTools.getCSVQuotes;


public class CreatureMap implements Serializable {
    public Map<String, Creature> map = new ConcurrentHashMap<>();
    int lastHashCode;
    private Date creationDate = new Date();
    private Comparator<Map.Entry<String, Creature>> comparator = new Comparator<Map.Entry<String, Creature>>() {
        @Override
        public int compare(Map.Entry<String, Creature> a, Map.Entry<String, Creature> b) {
            return a.getValue().getAge() - b.getValue().getAge();
        }
    };

    public CreatureMap() {
        lastHashCode = 0;
    }

    public CreatureMap(List<String[]> lines) throws InvalidParameterException {
        lines.remove(lines.get(lines.size()-1));
        lines.forEach(e -> map.put(e[0], new Creature(e)));
        lastHashCode = hashCode();
    }

    /**
     * Добавляет в колекцию новое значение и сортирует ее.
     *
     * @param key     String ключ нового элемента
     * @param element Creature
     */
    public void insert(String key, Creature element) {
        map.put(key, element);
        //sortKeys();
    }

    /**
     * Возвращает строку отображающую сождержимое коллекции
     * В случае, если коллекция пустая, возвращается строка:
     * "Colection is empty"
     */
    public String show() {
        String result = this.toString();
        return "\n\n" + (result.equals("") ? "Colection is empty\n" : result);
    }

    /**
     * Сохраняет текущее состояние в файл.
     *
     * @param fileName    String путь до файла
     * @param charsetName String кодировка
     * @throws FileSavingException выбрасывается в случае, если не удается сохранить файл
     */
    public void save(String fileName, String charsetName) throws FileSavingException {
        if (isEdited()) {
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), charsetName);) {
                writer.write(toCSV());
                writer.flush();
                lastHashCode = hashCode();
            } catch (FileNotFoundException e) {
                throw new FileSavingException(String.format("\"File not saved: can't write to \": file \"%s\"", fileName));
            } catch (IOException e) {
                throw new FileSavingException("File not saved: " + e.getMessage());
            }

        }
    }

    /**
     * Добавляет элемент в коллекцию, если он меньше всех элементов коллекции.
     * Сравнение происходит по полю age. Ключем нового элемента выступает его hashCode.
     * После коллекция сортируется.
     *
     * @param element Creature рассматрвиаемый элемент
     */
    @SuppressWarnings("unchecked")
    public void add_if_min(Creature element) {
        if (map.isEmpty()) {
            map.put(String.valueOf(element.hashCode()), element);
        } else {
            if (element.compareTo(map.entrySet().stream()
                    .min(comparator).get().getValue()) < 0) {

                map.put(String.valueOf(element.hashCode()), element);
            }
        }
    }

    /**
     * Удаляет элемент коллекции по ключу.
     * В случае если ключ не найден выбрасываестя исключение InvalidParameterException.
     *
     * @param key String ключ удаляемого элемента
     */
    public void remove(String key) {
        if (map.keySet().contains(key)) {
            map.remove(key);
        } else {
            throw new InvalidParameterException("Нет объекта с таким ключем");
        }
    }

    /**
     * Возвращает строку с информацией о типе коллекции, дате ее создания и количестве объектов в ней.
     *
     * @return inforamtion String результат
     */
    public String info() {
        String information = String.format("\ntype: %s\nobjects number: %d\nCreation date: %s\n", map.getClass().getName(), map.size(), creationDate.toString());
        return information;
    }

    /**
     * Удаляет все записи из коллекции, у которых ключ меньше переданного.
     *
     * @param key String ключ для сравнений
     */
    public void remove_lower(String key) {
        map.entrySet().stream().filter(e -> key.compareTo(e.getKey()) > 0).forEach(e -> remove(e.getKey()));
    }

    private String toCSV() {
        StringBuilder sb = new StringBuilder();
        map.entrySet().stream().sorted(comparator).forEach(e -> sb.append(String.format("\"%s\",%s", getCSVQuotes(e.getKey()), e.getValue().toCSVLine())));
        return sb.toString();
    }


    public boolean isEdited() {
        return lastHashCode != hashCode();
    }

    @Override
    public String toString() {
        final String[] result = {""};
        map.entrySet().stream().sorted(comparator).forEach(e -> {
            result[0] += String.format("key: %s \nelement: " +
                            "%s===========================================\n",
                    e.getKey(), e.getValue().toString());
        });

        return result[0];
    }


    @Override
    public int hashCode() {
        final int[] hash = {0, 0};
        map.entrySet().stream().forEach(e ->
        {
            hash[0] += e.getValue().hashCode();
            hash[1] += e.getKey().hashCode();
        });
        return hash[0] * hash[1];
    }
}
