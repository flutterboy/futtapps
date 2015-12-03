package it.negro.contab.mongo;

import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public class ContabGridFsFactory {

	private GridFsTemplate gridFsTemplate;
	
	public ContabGridFsFactory(SpringMongoDbFactoryGridFs factory) {
		try {
			this.gridFsTemplate = factory.gridFsTemplate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public GridFsTemplate template() {
		return gridFsTemplate;
	}
	
}
