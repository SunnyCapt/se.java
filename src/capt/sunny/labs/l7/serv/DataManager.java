package capt.sunny.labs.l7.serv;


import capt.sunny.labs.l7.Creature;
import capt.sunny.labs.l7.User;
import capt.sunny.labs.l7.serv.db.DB;
import capt.sunny.labs.l7.serv.db.DBActionType;
import capt.sunny.labs.l7.serv.db.DBException;
import capt.sunny.labs.l7.serv.db.ResultSetWrapper;

import javax.security.auth.login.LoginException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class DataManager implements Serializable, AutoCloseable {
    private ConcurrentHashMap<String, Creature> map = new ConcurrentHashMap<>();
    private int lastHashCode;
    private Date creationDate = new Date();
    private Comparator<Map.Entry<String, Creature>> comparator = Comparator.comparingInt(a -> a.getValue().getAge());
    private DB db;

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public DataManager(DB _db) {
        db = _db;
        lastHashCode = 0;
    }


//
//    private User getUserStatusFrom(String threadName){
//        if (!users.keySet().contains(threadName))
//            throw new InvalidParameterException("Wrong thread name");
//        return users.get(threadName);
//    }

    public boolean canLogin(String _userName) {
        String[] thredWithOldSession = {null};
        users.entrySet().stream().forEach(c -> {
            if (c.getValue().getNick().equals(_userName) && c.getValue().isTokenValid())
                thredWithOldSession[0] = c.getKey();
        });
        return thredWithOldSession[0] == null;
    }

    public void putUser(String _threadName, User _user){
        users.put(_threadName, _user);
    }

    public ResultSetWrapper dbRequest(String _pgSQLRequest, DBActionType _actionType) throws DBException {
        return db.request(_pgSQLRequest, _actionType);
    }

    public Object getOneDBFild(String _pgSQLRequest) throws DBException {
        ResultSetWrapper result = db.request(_pgSQLRequest, DBActionType.GET);
        try {
            if (!result.getResultSet().next())
                return null;
            else {
                Object res = result.getResultSet().getObject(_pgSQLRequest.substring(7, _pgSQLRequest.toUpperCase().indexOf(" FROM")).trim());
                result.close();
                return res;
            }
        } catch (SQLException e) {
            throw new DBException("DB access error: " + e.getMessage());
        }
    }

    public boolean dbHas(String condition, String dbName) {
        try {
            return dbRequest(String.format("SELECT * FROM %s WHERE %s", dbName, condition), DBActionType.GET).getResultSet().next();
        } catch (DBException | SQLException e) {
            return false;
        }
    }

    /**
     * Добавляет в колекцию новое значение и сортирует ее.
     *
     * @param name    String имя нового объекта
     * @param element Creature
     */
    public boolean insert(String name, Creature element) {
        boolean result = false;
        if (map.keySet().contains(name)) {
            if (map.get(name).getOwnerNick().equals(element.getOwnerNick())) {
                map.put(name, element);
                result = true;
            }
        } else {
            map.put(name, element);
            result = true;
        }
        return result;
    }

    /**
     * Возвращает строку отображающую сождержимое коллекции
     * В случае, если коллекция пустая, возвращается строка:
     * "Colection is empty"
     */
    public String show() {
        String result = this.toString();
        return "\n" + (result.equals("") ? "Colection is empty\n" : result);
    }

    /**
     * Сохраняет текущее состояние в файл.
     *
     * @param owner String имя юзера отправившего запрос
     * @throws FileSavingException выбрасывается в случае, если не удается сохранить файл
     */
    public void save(String owner) throws DBException {
        String updatePattern = " UPDATE s278068_objects SET %s WHERE name='%s'; ";
        String insertPattern = " INSERT INTO s278068_objects VALUES(%s); ";
        String deletePattern = " DELETE FROM s278068_objects WHERE name='%s'; ";

        if (isCollectionEdited()) {
            try {
                //deleting
                ResultSetWrapper objsName = db.request("SELECT name FROM s278068_objects WHERE owner='" + owner + "'", DBActionType.GET);
                while (objsName.getResultSet().next()) {
                    String name = objsName.getResultSet().getString("name");
                    if (!map.keySet().contains(name))
                        db.request(String.format(deletePattern, name), DBActionType.UPDATE);
                }
                //Inserting and updating
                StringBuilder req = new StringBuilder();
                if (!map.isEmpty()) {
                    map.entrySet().stream().filter(c -> c.getValue().getOwnerNick().equals(owner)).forEach(
                            c -> {
                                if (dbHas("name='" + c.getKey() + "'", "s278068_objects"))
                                    req.append(String.format(updatePattern, c.getValue().getStringForDBWithVarNames(), c.getValue().getName()));
                                else
                                    req.append(String.format(insertPattern, c.getValue().getStringForDB()));
                            }
                    );
                    db.request(req.toString(), DBActionType.UPDATE);
                }
                lastHashCode = hashCode();
            } catch (DBException | SQLException e) {
                throw new DBException("Cannt save collection, sorry: " + e.getMessage());
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

    public boolean add_if_min(Creature element) {
        boolean result = false;
        if (map.isEmpty()) {
            map.put(element.getName(), element);
            result = true;
        } else {
            if (element.compareTo(map.entrySet().stream().min(comparator).get().getValue()) < 0) {
                map.put(String.valueOf(element.hashCode()), element);
                result = true;
            }
        }
        return result;

    }

    /**
     * Удаляет элемент коллекции по ключу.
     * В случае если ключ не найден выбрасываестя исключение InvalidParameterException.
     *
     * @param name String ключ удаляемого элемента
     */
    public void remove(String name, String owner) {
        if (map.keySet().contains(name)) {
            if (map.get(name).getOwnerNick().equals(owner))
                map.remove(name);
            else
                throw new InvalidParameterException("Cannt remove it. This creature does not belong to you !!");
        } else {
            throw new InvalidParameterException("No creature with that name");
        }
    }

    /**
     * Возвращает строку с информацией о типе коллекции, дате ее создания и количестве объектов в ней.
     *
     * @return inforamtion String результат
     */
    public String info() {
        String information = String.format("\nspecies: %s\nobjects number: %d\nCreation date: %s\n", map.getClass().getName(), map.size(), creationDate.toString());
        return information;
    }

    /**
     * Удаляет все записи из коллекции, у имена которых меньше переданного.
     *
     * @param name String имя для сравнений
     */
    public void remove_lower(String name, String owner) {
        map.entrySet().stream().filter(e -> name.compareTo(e.getKey()) > 0).forEach(e -> remove(e.getKey(), owner));
    }

    public void copyCollectionOf(DataManager _dataManager) {
        map.putAll(_dataManager.map);
    }

//    private String toCSV() {
//        StringBuilder sb = new StringBuilder();
//        map.entrySet().stream().sorted(comparator).forEach(e -> sb.append(String.format("\"%s\",%s", getCSVQuotes(e.getKey()), e.getValue().toCSVLine())));
//        return sb.toString();
//    }


    private boolean isCollectionEdited() {
        return lastHashCode != hashCode();
    }

    @Override
    public String toString() {
        final String[] result = {""};
        map.entrySet().stream().sorted(comparator).forEach(e -> {
            result[0] += String.format("name: %s \ndetails: %s" +
                            "===========================================\n",
                    e.getKey(), e.getValue().toString(false));
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

    public void loadCollection() throws DBException {
        ResultSetWrapper result = db.request("SELECT * FROM s278068_objects;", DBActionType.GET);
        try {
            while (result.getResultSet().next()) {
                String userName = result.getResultSet().getString("owner");

                ResultSetWrapper gettingUser = db.request("SELECT * FROM s278068_users WHERE user_name='" + userName + "'", DBActionType.GET);
                gettingUser.getResultSet().next();
                User user = new User(userName, gettingUser.getResultSet().getString("upassword"));
                gettingUser.close();


                map.put(result.getResultSet().getString("name"), new Creature(
                        result.getResultSet().getString("species"),
                        result.getResultSet().getInt("age"),
                        result.getResultSet().getString("name"),
                        new Point((Double[]) result.getResultSet().getArray("location").getArray()),
                        user.getNick())
                );
            }
        } catch (SQLException e) {
            throw new DBException("Cannt load collection: " + e.getMessage());
        }
    }

    public String usersTableToString(ResultSet _result) throws DBException {
        try {
            StringBuilder sb = new StringBuilder();
            while (_result.next()) {
                Array arr = _result.getArray("objects");
                try {
                    String[] objs = (String[]) arr.getArray();
                    sb.append(String.format("\nuser: %s\nobjects: %s\n", _result.getString("user_name"), Arrays.toString(objs)));
                } catch (NullPointerException e) {
                    sb.append(System.out.printf("\nuser: %s\nobjects: {}\n", _result.getString("user")));
                }
            }
            return sb.toString();
        } catch (SQLException e) {
            throw new DBException("Cannt read users_table: " + e.getMessage());
        }

    }

    public String objectsTableToString(ResultSet _result) throws DBException {
        try {

            DataManager dataManager = new DataManager(db);
            while (_result.next()) {
                String userName = _result.getString("owner");

                ResultSetWrapper gettingUser = db.request("SELECT * FROM s278068_users WHERE user_name='" + userName + "'", DBActionType.GET);
                gettingUser.getResultSet().next();
                User user = new User(userName, gettingUser.getResultSet().getString("upassword"));
                gettingUser.close();


                dataManager.insert(_result.getString("name"), new Creature(
                        _result.getString("species"),
                        _result.getInt("age"),
                        _result.getString("name"),
                        new Point((Double[]) _result.getArray("location").getArray()),
                        user.getNick())
                );
            }
            String res = dataManager.toString();
            dataManager = null;
            return res;
        } catch (SQLException e) {
            throw new DBException("Cannt read users_table: " + e.getMessage());
        }

    }

    @Override
    public void close() throws DBException {
        db.close();
    }
}


class SUserUtils {
    static String CHARACTERS = "?!#$%&'()*+,-.0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    public static String login(String _login, String _password, DataManager dataManager) throws LoginException {
        try {
            Object result = dataManager.getOneDBFild("SELECT user_name FROM s278068_users WHERE user_name='" + _login + "' and upassword='" + _password + "'; ");
            if (result != null)
                if (dataManager.canLogin(_login))
                    return getRandomToken();
                else
                    throw new LoginException("This application does not support multiple sessions.");
            else
                throw new LoginException("Wrong login/password");
        } catch (DBException e) {
            throw new LoginException("Cannt login: " + e.getMessage());
        }
    }

    private static String getRandomToken() {
        char[] text = new char[32];
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            text[i] = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
        }
        return new String(text);
    }
}