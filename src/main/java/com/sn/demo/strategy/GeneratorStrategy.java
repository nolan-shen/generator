package com.sn.demo.strategy;

import com.sn.demo.dto.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.velocity.VelocityContext;

public interface GeneratorStrategy {

	void generatorFile(VelocityContext context, TableEntity tableEntity, Configuration config);
}
