package capt.sunny.labs.l7.serv.db;

import java.sql.*;


//
//update s278068_users set objects=array['pers#1'] where user_name='alexander';
//insert into s278068_objects values('pers#1', 'Ivan_Alexander', '777', '2019-05-18T23:58:40', array[1.11,2.22,3.33], 9, TRUE, 'human(s)')
public class DB {
    public Connection connection = null;
    private String url = "jdbc:postgresql://%s:%d/%s";

    public DB(String _host, int _port, String _dbName) {
        url = String.format(url, _host, _port, _dbName);
    }



    public void connect(String _login, String _password) throws DBException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException | NullPointerException e) {
            throw new DBException("Could not find org.postgresql.Driver.");
        }

        if (connection == null)
            try {
                connection = DriverManager.getConnection(url, _login, _password);
            } catch (SQLException e) {
                throw new DBException("Wrong login/password: " + e.getMessage());
            }
    }

    public void close() throws DBException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DBException("Cannt close: " + e.getMessage());
        }
    }

    public ResultSetWrapper request(String _pgSQLRequest, DBActionType _actionType) throws DBException {
        try {
            if (connection != null) {
                Statement tempStatement = connection.createStatement();
                switch (_actionType) {
                    case UPDATE:
                        tempStatement.executeUpdate(_pgSQLRequest);
                        tempStatement.close();
                    case GET:
                        return new ResultSetWrapper(tempStatement.executeQuery(_pgSQLRequest), tempStatement);
                }
            } else {
                throw new DBException("Cannt complete a reqeust: statement not initialized");
            }
        } catch (SQLException e) {
            throw new DBException("Cannt complete a reqeust: " + e.getMessage());
        }

        return null;
    }
}



/*
-- Table: public.s278068_objects

-- DROP TABLE public.s278068_objects;

CREATE TABLE public.s278068_objects
(
    name character varying COLLATE pg_catalog."default" NOT NULL,
    owner character varying COLLATE pg_catalog."default" NOT NULL,
    size double precision NOT NULL,
    creation_date character varying COLLATE pg_catalog."default" NOT NULL,
    location double precision[] NOT NULL,
    age bigint NOT NULL,
    is_live boolean NOT NULL,
    species character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "s278068-objects_pkey" PRIMARY KEY (name)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.s278068_objects
    OWNER to postgres;

GRANT ALL ON TABLE public.s278068_objects TO postgres;

GRANT ALL ON TABLE public.s278068_objects TO s278068;
 */

/*
-- Table: public.s278068_users

-- DROP TABLE public.s278068_users;

CREATE TABLE public.s278068_users
(
    user_name character varying(30) COLLATE pg_catalog."default" NOT NULL,
    upassword character varying(30) COLLATE pg_catalog."default" NOT NULL,
    objects text[] COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.s278068_users
    OWNER to postgres;

GRANT ALL ON TABLE public.s278068_users TO postgres;

GRANT ALL ON TABLE public.s278068_users TO s278068;
 */