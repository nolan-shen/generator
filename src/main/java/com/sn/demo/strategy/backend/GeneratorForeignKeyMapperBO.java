package com.sn.demo.strategy.backend;


import com.sn.demo.dto.ColumnEntity;
import com.sn.demo.dto.TableEntity;
import com.sn.demo.strategy.GeneratorStrategy;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.List;

public class GeneratorForeignKeyMapperBO implements GeneratorStrategy {

	@Override
	public void generatorFile(VelocityContext context, TableEntity tableEntity, Configuration config) {
		String template = "templates/velocity/bo/mapper/ForeignKeyQueryMapperBO.java.vm";

		Object foreignKeyColumns = context.get("foreignKeyColumns");
		if (null != foreignKeyColumns) {
			List<ColumnEntity> columnEntityList = (List<ColumnEntity>) foreignKeyColumns;
			for (ColumnEntity entity : columnEntityList) {
				context.put("foreignKeyColumn", entity.getLowerAttrName());
				context.put("ForeignKeyColumn", tableEntity.getUpperClassName() + entity.getUpperAttrName());
				context.put("attrType", entity.getAttrType());
				String fileName = getFileName(config, tableEntity.getUpperClassName(), entity.getUpperAttrName(), "MapperBO");
				GeneratorCommonUtil.generatorFile(context, tableEntity, config, template, fileName);
			}
		}
	}

	private String getFileName(Configuration config, String className, String fileName, String fileSuffix) {
		String srcJavaPath = config.getString("srcJavaPath");
		String packagePath = config.getString("mapperBOPackage");

		srcJavaPath = StringUtils.replace(srcJavaPath, "/", File.separator);
		packagePath = StringUtils.replace(packagePath, ".", File.separator);
		return srcJavaPath + File.separator + packagePath + File.separator + className + fileName + fileSuffix + ".java";
//		return srcJavaPath + File.separator + packagePath + File.separator + StringUtil.lowerCase(className) + File.separator + className + fileName + fileSuffix + ".java";
	}
}
