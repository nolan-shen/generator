package com.sn.demo;


import com.sn.demo.dto.ColumnEntity;
import com.sn.demo.dto.TableEntity;
import com.sn.demo.strategy.StrategyContext;
import com.sn.demo.strategy.frontend.*;
import com.sn.demo.util.FileUtil;
import com.sn.demo.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public final class GeneratorFrontendUtil {

	// =====================================业务 start=====================================

	/**
	 * 生成代码
	 */
	public static void generatorCode(Configuration config, Map<String, String> table, List<Map<String, Object>> columns) {

		Map<String, Object> contextParam = GeneratorCommonUtil.buildContextParam(config, table, columns);
		TableEntity tableEntity = (TableEntity) contextParam.get("tableEntity");

		contextParam.put("frontendPageColumns", frontendPageColumns(tableEntity.getColumns()));
		contextParam.put("frontendPageNotBoolEnumColumns", frontendPageNotBoolEnumColumns(tableEntity.getColumns()));
		contextParam.put("frontendPageBoolEnumColumns", frontendPageBoolEnumColumns(tableEntity.getColumns()));
		contextParam.put("frontendNewModalColumns", frontendNewModalColumns(tableEntity.getColumns()));
		contextParam.put("frontendSearchFormInputColumns", frontendSearchFormInputColumns(tableEntity.getColumns()));

		//设置velocity资源加载器
		Properties prop = new Properties();
		prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(prop);

		VelocityContext context = new VelocityContext(contextParam);
		StrategyContext strategyContext = new StrategyContext();

		boolean boolOverwriteOldFile = config.getBoolean("boolOverwriteOldFile");
		if (!boolOverwriteOldFile) {
			String fileName = GeneratorPage.getFileName(config, tableEntity.getUpperClassName());
			Boolean flag = FileUtil.checkFile(fileName, false);
			if (flag) {
				throw new RuntimeException(tableEntity.getUpperClassName() + " 生成文件已存在，已忽略");
			}
		}

		// 补充 routerConfig
		strategyContext.setGeneratorStrategy(new GeneratorRouterConfig());
		strategyContext.executeStrategy(context, tableEntity, config);

		// 补充 apiConfig
		strategyContext.setGeneratorStrategy(new GeneratorApiConfig());
		strategyContext.executeStrategy(context, tableEntity, config);

		// 生成 page
		strategyContext.setGeneratorStrategy(new GeneratorPage());
		strategyContext.executeStrategy(context, tableEntity, config);

		// 生成 model
		strategyContext.setGeneratorStrategy(new GeneratorModel());
		strategyContext.executeStrategy(context, tableEntity, config);

		// 生成 service
		strategyContext.setGeneratorStrategy(new GeneratorFrontendService());
		strategyContext.executeStrategy(context, tableEntity, config);


	}

	// =====================================业务 end=====================================
	// =====================================私有方法 start=====================================

	private static List<ColumnEntity> frontendNewModalColumns(List<ColumnEntity> columnEntityList) {
		List<String> foreachIgnoreColumns = new ArrayList<>();
		foreachIgnoreColumns.add("id");
		foreachIgnoreColumns.add("description");
		foreachIgnoreColumns.add("ranking");
		foreachIgnoreColumns.add("delete_date");
		foreachIgnoreColumns.add("delete_user_id");
		foreachIgnoreColumns.add("create_date");
		foreachIgnoreColumns.add("create_user_id");
		foreachIgnoreColumns.add("update_date");
		foreachIgnoreColumns.add("update_user_id");

		// 排除所有枚举
		for (ColumnEntity columnEntity : columnEntityList) {
			String columnName = columnEntity.getColumnName();
			if (StringUtil.endsWith(columnName, "_enum") || StringUtil.startsWith(columnName, "bool_")) {
				foreachIgnoreColumns.add(columnName);
			}
		}

		List<ColumnEntity> columns = new ArrayList<>();
		for (ColumnEntity columnEntity : columnEntityList) {
			if (!foreachIgnoreColumns.contains(columnEntity.getColumnName())) {
				columns.add(columnEntity);
			}
		}
		return columns;
	}

	private static List<ColumnEntity> frontendSearchFormInputColumns(List<ColumnEntity> columnEntityList) {
		List<String> foreachIgnoreColumns = new ArrayList<>();
		foreachIgnoreColumns.add("id");
		foreachIgnoreColumns.add("ranking");
		foreachIgnoreColumns.add("delete_date");
		foreachIgnoreColumns.add("delete_user_id");
		foreachIgnoreColumns.add("create_date");
		foreachIgnoreColumns.add("create_user_id");
		foreachIgnoreColumns.add("update_date");
		foreachIgnoreColumns.add("update_user_id");

		// 排除所有枚举
		for (ColumnEntity columnEntity : columnEntityList) {
			String columnName = columnEntity.getColumnName();
			if (StringUtil.endsWith(columnName, "_enum") || StringUtil.startsWith(columnName, "bool_")) {
				foreachIgnoreColumns.add(columnName);
			}
		}

		List<ColumnEntity> columns = new ArrayList<>();
		for (ColumnEntity columnEntity : columnEntityList) {
			if (!foreachIgnoreColumns.contains(columnEntity.getColumnName())) {
				columns.add(columnEntity);
			}
		}
		return columns;
	}

	private static List<ColumnEntity> frontendPageColumns(List<ColumnEntity> columnEntityList) {
		List<String> foreachIgnoreColumns = new ArrayList<>();
		foreachIgnoreColumns.add("id");
		foreachIgnoreColumns.add("ranking");
		foreachIgnoreColumns.add("state_enum");
		foreachIgnoreColumns.add("delete_enum");
		foreachIgnoreColumns.add("delete_date");
		foreachIgnoreColumns.add("delete_user_id");
		foreachIgnoreColumns.add("create_date");
		foreachIgnoreColumns.add("create_user_id");
		foreachIgnoreColumns.add("update_date");
		foreachIgnoreColumns.add("update_user_id");

		List<ColumnEntity> columns = new ArrayList<>();
		for (ColumnEntity columnEntity : columnEntityList) {
			if (!foreachIgnoreColumns.contains(columnEntity.getColumnName())) {
				columns.add(columnEntity);
			}
		}
		return columns;
	}

	private static List<ColumnEntity> frontendPageNotBoolEnumColumns(List<ColumnEntity> columnEntityList) {
		List<String> foreachIgnoreColumns = new ArrayList<>();
		foreachIgnoreColumns.add("state_enum");
		foreachIgnoreColumns.add("delete_enum");

		List<ColumnEntity> columns = new ArrayList<>();
		for (ColumnEntity columnEntity : columnEntityList) {
			String columnName = columnEntity.getColumnName();
			if (!foreachIgnoreColumns.contains(columnEntity.getColumnName()) && StringUtil.endsWith(columnName, "_enum") && !StringUtil.startsWith(columnName, "bool_")) {
				columns.add(columnEntity);
			}
		}

		return columns;
	}

	private static List<ColumnEntity> frontendPageBoolEnumColumns(List<ColumnEntity> columnEntityList) {
		List<ColumnEntity> columns = new ArrayList<>();
		for (ColumnEntity columnEntity : columnEntityList) {
			String columnName = columnEntity.getColumnName();
			if (StringUtil.endsWith(columnName, "_enum") && StringUtil.startsWith(columnName, "bool_")) {
				columns.add(columnEntity);
			}
		}
		return columns;
	}

	// =====================================私有方法 end=====================================

}
