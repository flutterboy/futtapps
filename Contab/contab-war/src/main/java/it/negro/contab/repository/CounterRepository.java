package it.negro.contab.repository;

import it.negro.contab.entity.Counter;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class CounterRepository {
	
	private MongoTemplate mongo;
	
	public void setMongo(MongoTemplate mongo) {
		this.mongo = mongo;
	}
	
	public int getNextSequence (){
		Counter counter = mongo.findAndModify(
				  new Query(Criteria.where("_id").is("movimenti")),
				  new Update().inc("val", 1),
				  new FindAndModifyOptions().returnNew(true),
				  Counter.class, "sequences");

		return counter.getVal();
	}
	
}
