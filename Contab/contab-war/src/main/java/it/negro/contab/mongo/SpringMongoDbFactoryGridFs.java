package it.negro.contab.mongo;

import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public class SpringMongoDbFactoryGridFs extends ContabMongoDBFactory {
	
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(this, mappingMongoConverter());
	}
	

}
