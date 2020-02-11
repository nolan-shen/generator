# generator
根据数据表快速生成增、删、改、查及批量增、删、改、查代码。

# 框架
使用velocity引擎，生成springMVC+spring+MybatisPlus+testng

# 使用方式
- 修改mysql配置：src/main/resources/mybatis-config.xml
- 修改生成配置：src/main/resources/generator.properties
    - 主要修改author（作者）、tableName（需要生成的表名）、backendRootPath（后端代码生成路径）
- 运行src/main/java/com/sn/demo/Main.java main方法生成

# 后端
## 规范
- 表字段使用_分隔（类名相关的会转换为大驼峰，字段名相关的转换为小驼峰）
## 生成结构
```text
├─main
│  ├─java
│  │  └─com
│  │      └─javaRootPackage
│  │                  ├─api
│  │                  │  ├─dto
│  │                  │  │      XxxDTO.java
│  │                  │  └─entity
│  │                  │          Xxx.java
│  │                  ├─controller
│  │                  │      XxxController.java
│  │                  ├─mapper
│  │                  │      XxxMapper.java
│  │                  └─service
│  │                      │  IXxxService.java
│  │                      └─impl
│  │                              XxxServiceImpl.java
│  └─resources
│      └─mapper
│              XxxMapper.xml
└─test
    └─java
        └─javaRootPackage
                        └─controller
                                XxxControllerTest.java
```


