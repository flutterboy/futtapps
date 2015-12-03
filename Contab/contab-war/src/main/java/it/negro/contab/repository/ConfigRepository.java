package it.negro.contab.repository;

import java.util.List;

import it.negro.contab.entity.ConfigBean;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ConfigRepository {
	
	private MongoTemplate mongo;
	
	public void setMongo(MongoTemplate mongo) {
		this.mongo = mongo;
	}
	
	public List<ConfigBean> read(){
		List<ConfigBean> result = mongo.find(new Query(), ConfigBean.class, "configs");
		return result;
	}
	
	public ConfigBean read(String id){
		ConfigBean result = mongo.findOne(new Query(Criteria.where("_id").is(id)), ConfigBean.class, "configs");
		return result;
	}
	
	public List<ConfigBean> delete(String id){
		mongo.remove(new Query(Criteria.where("_id").is(id)), "configs");
		return read();
	}
	
	public ConfigBean write(ConfigBean configBean){
		if (mongo.exists(new Query(Criteria.where("_id").is(configBean.getId())), "configs"))
			mongo.save(configBean, "configs");
		else
			mongo.insert(configBean, "configs");
		return read(configBean.getId());
	}
	
}
