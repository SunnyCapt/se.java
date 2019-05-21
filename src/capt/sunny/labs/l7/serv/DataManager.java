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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DataManager implements Serializable, AutoCloseable {
    private ConcurrentHashMap<String, Creature> map = new ConcurrentHashMap<>();
    private int lastHashCode;
    private Date creationDate = new Date();
    private Comparator<Map.Entry<String, Creature>> comparator = Comparator.comparingInt(a -> a.getValue().getAge());
    private DB db;

    public DataManager(DB _db) {
        db = _db;
        lastHashCode = 0;
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

    /**
     * Добавляет в колекцию новое значение и сортирует ее.
     *
     * @param name    String имя нового объекта
     * @param element Creature
     */
    public void insert(String name, Creature element) {
        map.put(name, element);
        //sortKeys();
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
        String pattern = " UPDATE s278068_objects\n" +
                "\tSET %s\n" +
                "\tWHERE name='%s'; ";

        if (isCollectionEdited()) {
            try {
                StringBuilder req = new StringBuilder();
                map.entrySet().stream().filter(c -> c.getValue().getOwnerNick().equals(owner)).forEach(c -> req.append(String.format(pattern, c.getValue().getStringForDB(), c.getValue().getName())));
                db.request(req.toString(), DBActionType.UPDATE);
                lastHashCode = hashCode();
            } catch (DBException e) {
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
     * Удаляет все записи из коллекции, у которых ключ меньше переданного.
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
        ResultSetWrapper result = db.request("SELECT * FROM s278068_objects", DBActionType.GET);
        try {
            while (result.getResultSet().next()) {
                String userName = result.getResultSet().getString("owner");

                ResultSetWrapper gettingUser = db.request("SELECT * FROM s278068_users WHERE user_name='" + userName + "'", DBActionType.GET);
                gettingUser.getResultSet().next();
                User user = new User(userName, gettingUser.getResultSet().getString("upassword"));
                gettingUser.close();


                insert(result.getResultSet().getString("name"), new Creature(
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


class SUserUtils{
    public static User login(String _login, String _password, DataManager dataManager) throws LoginException {
        try {
            Object result = dataManager.getOneDBFild("SELECT user_name FROM s278068_users WHERE user_name='"+_login+"' and upassword='"+_password+"'; ");
            if (result!=null)
                return new User(_login, _password);
            else
                throw new LoginException("Wrong login/password");
        } catch (DBException e) {
            throw new LoginException("Cannt login: " + e.getMessage());
        }
    }
}