package it.negro.contab.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.ChainedPersistenceExceptionTranslator;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDbFactory;

import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

public class ContabMongoDBFactory extends AbstractMongoConfiguration implements MongoDbFactory {

	private static Mongo mongoInstance;

	private String host;
	private int port;
	private String user;
	private String password;
	private String db;
	
	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDb(String db) {
		this.db = db;
	}

	@Override
	protected String getDatabaseName() {
		return db;
	}

	@Override
	public Mongo mongo() throws Exception {
		if (mongoInstance == null)
			createMongo();
		return mongoInstance;
	}

	private synchronized void createMongo() throws Exception{
		try {
			if (mongoInstance == null) {
				ServerAddress saddr = new ServerAddress(host, port);
				List<MongoCredential> credList = new ArrayList<MongoCredential>();
				MongoCredential mongoCred = MongoCredential.createMongoCRCredential(user, db, password.toCharArray());
				credList.add(mongoCred);
				mongoInstance = new MongoClient(saddr, credList);
			}
		} catch (UnknownHostException e) {
			throw new DataAccessException(e.getMessage(), e) {
				private static final long serialVersionUID = -2801105749893011570L;

			};
		}
	}

	@Override
	public DB getDb() throws DataAccessException {
		return getDb(db);
	}
	
	@Override
	public DB getDb(String dbName) throws DataAccessException {
		try {
			if (mongoInstance == null)
				createMongo();
			return mongoInstance.getDB(dbName);
		} catch (Exception e) {
			throw new DataAccessException(e.getMessage(), e) {
				private static final long serialVersionUID = -2801105749893011570L;
				
			};
		}
	}

	@Override
	public PersistenceExceptionTranslator getExceptionTranslator() {
		return new ChainedPersistenceExceptionTranslator();
	}

}
