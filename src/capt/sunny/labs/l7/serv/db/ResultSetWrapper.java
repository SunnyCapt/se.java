package capt.sunny.labs.l7.serv.db;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ResultSetWrapper implements AutoCloseable{
    private ResultSet resultSet;
    private Statement statement;

    public ResultSetWrapper(ResultSet _resultSet, Statement _statement){
        resultSet = _resultSet;
        statement = _statement;
    }

    @Override
    public void close() throws DBException {
        try {
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new DBException("Cannt close: " + e.getMessage());
        }
    }

    public ResultSet getResultSet(){
        return resultSet;
    }
}
