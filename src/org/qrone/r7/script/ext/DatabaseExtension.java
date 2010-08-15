package org.qrone.r7.script.ext;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.qrone.memcached.ClientMemcachedService;
import org.qrone.memcached.MemcachedService;
import org.qrone.r7.Extension;
import org.qrone.r7.script.ScriptablePrototype;
import org.qrone.r7.script.browser.Window;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@Extension
public class DatabaseExtension implements ScriptablePrototype<Window>{
	
	public MemcachedService memcached_connect(String host, Number port){
		String[] serverlist = new String[1];
		serverlist[0] = host + ":" + String.valueOf(port);
		return new ClientMemcachedService(serverlist);
	}

	public DB mongo_connect(String host, Number port, String schema)
			throws UnknownHostException, MongoException{
		Mongo m = new Mongo(host, port.intValue());
        return m.getDB(schema);
	}
	
	public DB mongo_connect(String host, Number port, String schema, 
			String user, String password) throws UnknownHostException, MongoException{
		Mongo m = new Mongo(host, port.intValue());
		DB db = m.getDB(schema);
		db.authenticate(user, password.toCharArray());
        return db;
	}

	public Connection jdbc_connect(String cls, String url) throws SQLException{
		try {
			Class.forName(cls).newInstance();
		} catch (Exception e) {
			return null;
		}
		
		return DriverManager.getConnection(url);
	}
	
	public Connection jdbc_connect(String cls, String url, Properties info) throws SQLException{
		try {
			Class.forName(cls).newInstance();
		} catch (Exception e) {
			return null;
		}
		
		return DriverManager.getConnection(url, info);
	}
	
	public Connection derby_connect(String file, 
			String user, String password) throws SQLException{
		Properties props = new Properties();
        props.put("user", user);
        props.put("password", password);
        
		return jdbc_connect("org.apache.derby.jdbc.EmbeddedDriver", 
				"jdbc:derby:" + file + ";create=true", props);
	}
	
	public Connection mysql_connect(String host, String schema, 
			String user, String password) throws SQLException{
		Properties props = new Properties();		
        props.put("user", user);		
        props.put("password", password);	
        props.put("useUnicode", "true");	
        props.put("characterEncoding", "utf8");		

		return jdbc_connect("com.mysql.jdbc.Driver", 
				"jdbc:mysql://" + host + "/" + schema, props);
	}
}
