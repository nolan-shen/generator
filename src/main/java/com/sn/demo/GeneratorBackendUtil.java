package com.sn.demo;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.sn.demo.dto.ColumnEntity;
import com.sn.demo.dto.EnumEntity;
import com.sn.demo.dto.EnumItemEntity;
import com.sn.demo.dto.TableEntity;
import com.sn.demo.strategy.StrategyContext;
import com.sn.demo.strategy.backend.*;
import com.sn.demo.util.CollectionUtil;
import com.sn.demo.util.FileUtil;
import com.sn.demo.util.StringUtil;
import com.sn.demo.util.id.GenerateIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.util.*;

@Slf4j
public final class GeneratorBackendUtil {
  /**
   * 生成代码
   */
  public static void generatorCode(Configuration config, Map<String, String> table, List<Map<String, Object>> columns) {
    Map<String, Object> contextParam = GeneratorCommonUtil.buildContextParam(config, table, columns);
    TableEntity tableEntity = (TableEntity) contextParam.get("tableEntity");
    Boolean isRelationTable = (Boolean) contextParam.get("isRelationTable");

    Long menuId = GenerateIdUtil.getId();
    contextParam.put("menuId", menuId);
    contextParam.put("createButtonId", menuId + 1L);
    contextParam.put("updateButtonId", menuId + 2L);
    contextParam.put("deleteButtonId", menuId + 3L);

    //设置velocity资源加载器
    Properties prop = new Properties();
    prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    Velocity.init(prop);

    VelocityContext context = new VelocityContext(contextParam);
    StrategyContext strategyContext = new StrategyContext();

    boolean boolOverwriteOldFile = config.getBoolean("boolOverwriteOldFile");
    if (!boolOverwriteOldFile) {
      String fileName = GeneratorEntity.getFileName(config, tableEntity.getUpperClassName());
      Boolean flag = FileUtil.checkFile(fileName, false);
      if (flag) {
        throw new RuntimeException(tableEntity.getUpperClassName() + " 生成文件已存在，已忽略");
      }
    }

    // 生成 Entity
    strategyContext.setGeneratorStrategy(new GeneratorEntity());
    strategyContext.executeStrategy(context, tableEntity, config);

    // 生成 DTO
    strategyContext.setGeneratorStrategy(new GeneratorDTO());
    strategyContext.executeStrategy(context, tableEntity, config);

    // 生成 MapStruct
    strategyContext.setGeneratorStrategy(new GeneratorMapStruct());
    strategyContext.executeStrategy(context, tableEntity, config);

    //mvc----------------------------------------------------------------------
    // 生成 Mapper
    strategyContext.setGeneratorStrategy(new GeneratorMapper());
    strategyContext.executeStrategy(context, tableEntity, config);

    // 生成 MapperXML
    strategyContext.setGeneratorStrategy(new GeneratorMapperXML());
    strategyContext.executeStrategy(context, tableEntity, config);

    // 生成 IService
    strategyContext.setGeneratorStrategy(new GeneratorIService());
    strategyContext.executeStrategy(context, tableEntity, config);

    // 生成 Service
    strategyContext.setGeneratorStrategy(new GeneratorService());
    strategyContext.executeStrategy(context, tableEntity, config);

    // 生成 Controller
    strategyContext.setGeneratorStrategy(new GeneratorController());
    strategyContext.executeStrategy(context, tableEntity, config);

    //权限及压力测试----------------------------------------------------------------------
    // 不是中间表的情况下
    if (!isRelationTable) {
      // 生成 PermissionSQL
      strategyContext.setGeneratorStrategy(new GeneratorPermissionSQL());
      strategyContext.executeStrategy(context, tableEntity, config);

      // 生成 Gatling
      strategyContext.setGeneratorStrategy(new GeneratorGatling());
      strategyContext.executeStrategy(context, tableEntity, config);
    }

    //测试----------------------------------------------------------------------
    // 生成 MapperTest
    strategyContext.setGeneratorStrategy(new GeneratorMapperTest());
    strategyContext.executeStrategy(context, tableEntity, config);

    // 生成 ServiceTest
    strategyContext.setGeneratorStrategy(new GeneratorServiceTest());
    strategyContext.executeStrategy(context, tableEntity, config);

    // 生成 ControllerTest
    strategyContext.setGeneratorStrategy(new GeneratorControllerTest());
    strategyContext.executeStrategy(context, tableEntity, config);
  }

  private static List<EnumEntity> enumClassEntityList(List<ColumnEntity> allEnumColumns) {
    List<EnumEntity> enumClassEntityList = new ArrayList<>();
    for (ColumnEntity columnEntity : allEnumColumns) {
      // 过滤掉 bool 开头的，这类枚举统一用 BooleanEnum
      if (StringUtil.startsWith(columnEntity.getColumnName(), "bool_")) {
        continue;
      }
      EnumEntity enumEntity = new EnumEntity();
      enumEntity.setUpperAttrName(columnEntity.getUpperAttrName());
      String comment = columnEntity.getComment();
      String enumComment = StringUtil.substringBetween(comment, "[", "]");
      List<String> enumItemList = StringUtil.splitAndTrim(enumComment, ",");
      List<EnumItemEntity> enumItemEntityList = new ArrayList<>();
      for (String temp : enumItemList) {
        List<String> enumInfoList = StringUtil.splitAndTrim(temp, "=");
        EnumItemEntity enumItemEntity = new EnumItemEntity();
        enumItemEntity.setCode(Integer.valueOf(enumInfoList.get(0)));
        enumItemEntity.setDescription(enumInfoList.get(1));
        enumItemEntity.setCodeName(enumInfoList.get(2));
        enumItemEntityList.add(enumItemEntity);
      }
      enumEntity.setEnumItemEntityList(enumItemEntityList);
      enumClassEntityList.add(enumEntity);
    }
    return enumClassEntityList;
  }

}
