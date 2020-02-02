package com.sn.demo;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.sn.demo.dto.ColumnEntity;
import com.sn.demo.dto.TableEntity;
import com.sn.demo.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.app.Velocity;

import java.util.*;

@Slf4j
public final class GeneratorCommonUtil {

	// =====================================业务 start=====================================

	/**
	 * 生成代码
	 */
	public static Map<String, Object> buildContextParam(Configuration config, Map<String, String> table, List<Map<String, Object>> columns) {
		Map<String, Object> contextParam = new HashMap<>(16);
		contextParam.put("isRelationTable", false);
		contextParam.put("isIncludeParentId", false);
		contextParam.put("isIncludeRanking", false);
		contextParam.put("isIncludeDescription", false);
		contextParam.put("isIncludeCreateDate", false);
		contextParam.put("isIncludeUpdateDate", false);
		contextParam.put("isIncludeStateEnum", false);
		contextParam.put("isIncludeDeleteEnum", false);

//		contextParam.put("isRelationTable", true);
//		contextParam.put("isIncludeParentId", true);
//		contextParam.put("isIncludeRanking", true);
//		contextParam.put("isIncludeDescription", true);
//		contextParam.put("isIncludeCreateDate", true);
//		contextParam.put("isIncludeUpdateDate", true);
//		contextParam.put("isIncludeStateEnum", true);
//		contextParam.put("isIncludeDeleteEnum", true);

		//表信息
		TableEntity tableEntity = new TableEntity();
		tableEntity.setTableName(table.get("tableName"));
		tableEntity.setComments(table.get("tableComment"));

		//表名转换成Java类名
		String ClassName = buildClassName(tableEntity.getTableName());
		tableEntity.setUpperClassName(ClassName);
		tableEntity.setLowerClassName(StringUtil.uncapitalize(ClassName));

		//列信息
		List<ColumnEntity> columnList = buildColumnEntityList(config, tableEntity, columns, contextParam);
		tableEntity.setColumns(columnList);

		//没主键，则第一个字段为主键
		if (tableEntity.getPk() == null) {
			tableEntity.setPk(tableEntity.getColumns().get(0));
		}

		//设置velocity资源加载器
		Properties prop = new Properties();
		prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(prop);
		//封装模板数据
		contextParam.put("tableEntity", tableEntity);
		contextParam.put("tableName", tableEntity.getTableName());
		contextParam.put("tableComment", tableEntity.getComments());
		contextParam.put("pk", tableEntity.getPk());
		contextParam.put("ClassName", tableEntity.getUpperClassName());
		contextParam.put("className", tableEntity.getLowerClassName());
		contextParam.put("classname", StringUtil.lowerCase(tableEntity.getLowerClassName()));
		contextParam.put("class_name", StringUtil.lowerCamelToLowerUnderscore(tableEntity.getLowerClassName()));
		contextParam.put("pathName", tableEntity.getLowerClassName().toLowerCase());
		contextParam.put("columns", tableEntity.getColumns());
		contextParam.put("datetime", DateUtil.now());
		contextParam.put("moduleName", config.getString("moduleName"));
		contextParam.put("javaRootPackage", config.getString("javaRootPackage"));
		return contextParam;
	}

	// =====================================业务 end=====================================
	// =====================================私有方法 start=====================================


	private static List<ColumnEntity> buildColumnEntityList(Configuration config, TableEntity tableEntity, List<Map<String, Object>> columns, Map<String, Object> contextParam) {
		List<ColumnEntity> columnList = new ArrayList<>();
		for (Map<String, Object> column : columns) {
			ColumnEntity columnEntity = new ColumnEntity();
			String columnName = (String) column.get("columnName");
			columnEntity.setColumnName(columnName);

			String columnComment = (String) column.get("columnComment");
			if (StrUtil.isEmpty(columnComment)) {
				columnComment = "";
			}
			columnEntity.setComment(columnComment);
			columnEntity.setShortComment(buildColumnShortComment(columnComment));

			String maxValue = StringUtil.substringAfter(columnComment, "max=");
			if (StringUtil.isNotBlank(maxValue)) {
				columnEntity.setMaxValue(Integer.valueOf(maxValue));
			}

			if (StringUtil.endsWith(columnName, "_enum") || StringUtil.startsWith(columnName, "bool_")) {
				columnEntity.setBoolIsEnum(true);
			}

			Object columnDefault = column.get("columnDefault");
			if (null != columnDefault) {
				columnEntity.setColumnDefault((String) columnDefault);
			}

			Object isNullable = column.get("isNullable");
			if (null != isNullable && StringUtil.equalsIgnoreCase(isNullable.toString(), "yes")) {
				columnEntity.setBoolIsNullable(true);
			}
			Object characterMaximumLength = column.get("characterMaximumLength");
			if (null != characterMaximumLength) {
				columnEntity.setCharacterMaximumLength(new Long((long) characterMaximumLength).intValue());
			}

			if (StringUtil.startsWith(tableEntity.getTableName(), "rel_")) {
				contextParam.put("isRelationTable", true);
			}

			if (StringUtil.equalsIgnoreCase(columnEntity.getColumnName(), "parent_id")) {
				contextParam.put("isIncludeParentId", true);
			}

			if (StringUtil.equalsIgnoreCase(columnEntity.getColumnName(), "ranking")) {
				contextParam.put("isIncludeRanking", true);
			}

			if (StringUtil.equalsIgnoreCase(columnEntity.getColumnName(), "description")) {
				contextParam.put("isIncludeDescription", true);
			}

			if (StringUtil.equalsIgnoreCase(columnEntity.getColumnName(), "create_date")) {
				contextParam.put("isIncludeCreateDate", true);
			}

			if (StringUtil.equalsIgnoreCase(columnEntity.getColumnName(), "update_date")) {
				contextParam.put("isIncludeUpdateDate", true);
			}

			if (StringUtil.equalsIgnoreCase(columnEntity.getColumnName(), "state_enum")) {
				contextParam.put("isIncludeStateEnum", true);
			}

			if (StringUtil.equalsIgnoreCase(columnEntity.getColumnName(), "delete_enum")) {
				contextParam.put("isIncludeDeleteEnum", true);
			}

			columnEntity.setDataType((String) column.get("dataType"));
			columnEntity.setExtra((String) column.get("extra"));

			//列名转换成Java属性名
			String upperAttrName = buildUpperAttrName(columnEntity.getColumnName());
			columnEntity.setUpperAttrName(upperAttrName);
			columnEntity.setLowerAttrName(StringUtil.uncapitalize(upperAttrName));

			//列的数据类型，转换成Java类型
			String attrType = config.getString(columnEntity.getDataType(), "unknowType");
			columnEntity.setAttrType(attrType);
			//是否主键
			if ("PRI".equalsIgnoreCase((String) column.get("columnKey")) && tableEntity.getPk() == null) {
				tableEntity.setPk(columnEntity);
			}

			columnList.add(columnEntity);
		}
		return columnList;
	}


	/**
	 * 短的备注（去掉冒号后面部分）
	 */
	private static String buildColumnShortComment(String columnComment) {
		return StringUtil.substringBefore(columnComment, ":");
	}

	private static String buildUpperAttrName(String columnName) {
		return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
	}

	private static String buildClassName(String tableName) {
		return buildUpperAttrName(tableName);
	}

	// =====================================私有方法 end=====================================

}
