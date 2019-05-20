package capt.sunny.labs;

import capt.sunny.labs.l7.serv.DataManager;
import capt.sunny.labs.l7.serv.db.DB;
import capt.sunny.labs.l7.serv.db.DBActionType;
import capt.sunny.labs.l7.serv.db.DBException;
import capt.sunny.labs.l7.serv.db.ResultSetWrapper;
import org.json.JSONObject;

import java.sql.SQLException;


public class Main {
//_users WHERE user_name='Ivan_Alexander' and upassword='1234';
    public static void main(String[] args) throws DBException, SQLException {
//        DB db = new DB("localhost", 3128,"studs");
//        db.connect("s278068", "taq704");
//        DataManager dm = new DataManager(db);
//        String str = (String) dm.getOneDBFild("SELECT upassword FROM s278068_users _users WHERE user_name='Ivan_Alexander'");
//        System.out.println(str);
        //dm.close();
        String obj = "{ \"nick\": x, \"token\": \"STupID STudeNt mUSt Die\", \"message\": \"Hello Ivan_Alexander. You haven't been in street racing for a long time!\"}";
        JSONObject jsonObject = new JSONObject(obj);
        System.out.println(jsonObject.getString("nick"));

    }

}

