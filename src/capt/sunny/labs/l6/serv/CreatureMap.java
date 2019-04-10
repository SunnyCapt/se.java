package capt.sunny.labs.l6.serv;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static capt.sunny.labs.l6.IOTools.getCSVQuotes;

public class CreatureMap {
    private Map<String, Creature> map = new ConcurrentHashMap<>();
    int lastHashCode;
    private Date creationDate = new Date();
    private List<String> sortedKeys = new ArrayList<>();

    public CreatureMap() {
        lastHashCode = 0;
    }

    public CreatureMap(List<String[]> lines) throws InvalidParameterException {

        for (String[] line : lines) {
            map.put(line[0], new Creature(line));
        }
        sortKeys();
        lastHashCode = hashCode();
    }

    private void sortKeys() {
        List<Map.Entry<String, Creature>> list = new ArrayList(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Creature>>() {
            @Override
            public int compare(Map.Entry<String, Creature> a, Map.Entry<String, Creature> b) {
                return a.getValue().getAge() - b.getValue().getAge();
            }
        });
        sortedKeys.clear();
        list.forEach((entry) -> {
            sortedKeys.add(entry.getKey());
        });
    }

    /** Добавляет в колекцию новое значение и сортирует ее.
      @param key String ключ нового элемента
      @param element Creature
      */
    public void insert(String key, Creature element) {
        map.put(key, element);
        sortKeys();
    }

    /** Возвращает строку отображающую сождержимое коллекции
     В случае, если коллекция пустая, возвращается строка:
        "Colection is empty"
     */
    public String show() {
        String result = this.toString();
        return "\n\n" + (result.equals("") ? "Colection is empty\n" : result);
    }

    /** Сохраняет текущее состояние в файл.
      @param fileName String путь до файла
      @param charsetName String кодировка
      @throws FileSavingException выбрасывается в случае, если не удается сохранить файл
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

    /**Добавляет элемент в коллекцию, если он меньше всех элементов коллекции.
      Сравнение происходит по полю age. Ключем нового элемента выступает его hashCode.
      После коллекция сортируется.
      @param element Creature рассматрвиаемый элемент
     */
    public void add_if_min(Creature element) {
        if (map.isEmpty()) {
            map.put(String.valueOf(element.hashCode()), element);
        } else {
            if (element.compareTo(map.get(findMin())) < 0) {
                map.put(String.valueOf(element.hashCode()), element);
            }
        }
        sortKeys();
    }

    /**Удаляет элемент коллекции по ключу.
     В случае если ключ не найден выбрасываестя исключение InvalidParameterException.
     @param key String ключ удаляемого элемента
     */
    public void remove(String key) {
        if (sortedKeys.contains(key)) {
            map.remove(key);
            sortedKeys.remove(key);
        } else {
            throw new InvalidParameterException("Нет объекта с таким ключем");
        }
    }

    /**Возвращает строку с информацией о типе коллекции, дате ее создания и количестве объектов в ней.
     @return inforamtion String результат
     */
    public String info() {
        String information = String.format("\ntype: %s\nobjects number: %d\nCreation date: %s\n", map.getClass().getName(), map.size(), creationDate.toString());
        return information;
    }

    /**Удаляет все записи из коллекции, у которых ключ меньше переданного.
     @param key String ключ для сравнений
     */
    public void remove_lower(String key) {
        List<String> mins = new ArrayList<>();
        map.forEach(
                (_key, _creature) -> {
                    if (key.compareTo(_key) > 0) {
                        mins.add(_key);
                    }
                }
        );
        mins.forEach((_key) -> {
            map.remove(_key);
            sortedKeys.remove(_key);
        });
    }

    private String toCSV() {
        StringBuilder sb = new StringBuilder();
        String line;
        for (String key : sortedKeys) {
            Creature element = map.get(key);
            line = String.format("\"%s\",%s", getCSVQuotes(key), element.toCSVLine());
            sb.append(line);
        }
        return sb.toString();
    }


    public String findMin() {
        return sortedKeys.get(0);
    }



    public boolean isEdited() {
        return lastHashCode != hashCode();
    }

    @Override
    public String toString() {
        String result = "";
        for (String _key : sortedKeys) {
            Creature _item = map.get(_key);
            result += String.format("key: %s \nelement: " +
                    "%s===========================================\n",
                    _key, _item.toString());
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int[] hash = {0, 0};
        map.forEach((key, creature) ->
        {
            hash[0] += creature.hashCode();
            hash[1] += key.hashCode();
        });
        return hash[0] * hash[1];
    }
}
