package it.negro.contab.service;

import java.util.List;

import it.negro.contab.entity.ConfigBean;
import it.negro.contab.repository.ConfigRepository;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ContabConfigService {
	
	private ConfigRepository configRepository;
	
	public void setConfigRepository(ConfigRepository configRepository) {
		this.configRepository = configRepository;
	}
	
	@RequestMapping(path = "/rest/readAllConfig", method = RequestMethod.GET)
	public @ResponseBody List<ConfigBean> readAllConfig(){
		return configRepository.read();
	}
	
	@RequestMapping(path = "/rest/readConfig/{id}", method = RequestMethod.GET)
	public @ResponseBody
	ConfigBean readConfig(@PathVariable("id") String id){
		return configRepository.read(id);
	}
	
	@RequestMapping(path = "/rest/saveConfig", method = RequestMethod.POST)
	public @ResponseBody ConfigBean saveConfig(@RequestBody ConfigBean configBean){
		return configRepository.write(configBean);
	}
	
	@RequestMapping(path = "/rest/eliminaConfig/{id}", method = RequestMethod.GET)
	public @ResponseBody List<ConfigBean> removeConfig(@PathVariable("id") String id){
		return configRepository.delete(id);
	}
	
}
