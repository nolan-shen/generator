package com.sn.demo.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface SysGeneratorMapper {


	List<Map<String, Object>> queryList(@Param("tableName") String tableName);

	Map<String, String> queryTable(String tableName);

	List<Map<String, Object>> queryColumns(String tableName);
}
