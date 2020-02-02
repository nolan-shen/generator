package com.sn.demo.strategy.backend;


import com.sn.demo.dto.TableEntity;
import com.sn.demo.strategy.GeneratorStrategy;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;

public class GeneratorMapperXML implements GeneratorStrategy {

	@Override
	public void generatorFile(VelocityContext context, TableEntity tableEntity, Configuration config) {
		String template = "templates/velocity/Mapper.xml.vm";
		String fileName = getFileName(config, tableEntity.getUpperClassName());
		GeneratorCommonUtil.generatorFile(context, tableEntity, config, template, fileName);
	}


	private String getFileName(Configuration config, String className) {
		String packagePath = config.getString("resourceMapperXmlPath");

		packagePath = StringUtils.replace(packagePath, "/", File.separator);
		packagePath = StringUtils.replace(packagePath, ".", File.separator);
		return packagePath + File.separator + className + "Mapper.xml";
	}
}
