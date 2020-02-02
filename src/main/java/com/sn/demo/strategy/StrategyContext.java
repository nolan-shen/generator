package com.sn.demo.strategy;

import com.sn.demo.dto.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.velocity.VelocityContext;

public class StrategyContext {

	private GeneratorStrategy generatorStrategy;

	public StrategyContext() {

	}

	public void setGeneratorStrategy(GeneratorStrategy generatorStrategy) {
		this.generatorStrategy = generatorStrategy;
	}

	public void executeStrategy(VelocityContext context, TableEntity tableEntity, Configuration config) {
		generatorStrategy.generatorFile(context, tableEntity, config);
	}


}
