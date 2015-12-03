package it.negro.contab.repository;

import java.io.InputStream;

import it.negro.contab.mongo.ContabGridFsFactory;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

public class FileRepository {
	
	private GridFsOperations gridFsOperations;
	
	public void setGridFsFactory(ContabGridFsFactory gridFsFactory) {
		this.gridFsOperations = gridFsFactory.template();
	}
	
	public Object store(InputStream stream){
		
		DBObject metaData = new BasicDBObject();
		metaData.put("extra1", "anything 1");
		metaData.put("extra2", "anything 2");

		GridFSFile result = gridFsOperations.store(stream, "testing.png", "image/png", metaData);
		Object id = result.getId();
		return id;
	}
	
	public GridFSDBFile read (String fileId){
		GridFSDBFile result = gridFsOperations.findOne(new Query().addCriteria(Criteria.where("_id").is(new ObjectId(fileId))));
		return result;
	}
	
}
