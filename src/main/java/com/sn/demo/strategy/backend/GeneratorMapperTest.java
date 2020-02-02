package com.sn.demo.strategy.backend;


import com.sn.demo.dto.TableEntity;
import com.sn.demo.strategy.GeneratorStrategy;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;

public class GeneratorMapperTest implements GeneratorStrategy {

	@Override
	public void generatorFile(VelocityContext context, TableEntity tableEntity, Configuration config) {
		String template = "templates/velocity/junit/mapper/MapperTest.java.vm";
		String fileName = getFileName(config, tableEntity.getUpperClassName());
		GeneratorCommonUtil.generatorFile(context, tableEntity, config, template, fileName);
	}


	private String getFileName(Configuration config, String className) {
		String testSrcJavaPath = config.getString("testSrcJavaPath");
		String packagePath = config.getString("mapperPackage");

		testSrcJavaPath = StringUtils.replace(testSrcJavaPath, "/", File.separator);
		packagePath = StringUtils.replace(packagePath, ".", File.separator);
		return testSrcJavaPath + File.separator + packagePath + File.separator + className + "MapperTest.java";
	}
}
