package com.laosuye.mychat.common;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;

public class MPGenerator {
/**
 * MyBatis-Plus代码生成器的主函数。
 * 该函数配置了代码生成器的所有设置，包括数据源、全局配置、包配置、策略配置等。
 * 通过代码生成器，可以自动化生成基于数据库表的实体类、Mapper接口和XML文件等，减少手动编码的工作量。
 */
public static void main(String[] args) {
    // 创建代码生成器实例
    // 代码生成器
    AutoGenerator autoGenerator = new AutoGenerator();

    // 配置数据源
    // 数据源配置
    DataSourceConfig dataSourceConfig = new DataSourceConfig();
    dataSourceConfig.setDbType(DbType.MYSQL);//指定数据库类型
    //---------------------------数据源-----------------------------------
    // 配置开发环境的数据源信息
    assembleDev(dataSourceConfig);//配置数据源
    autoGenerator.setDataSource(dataSourceConfig);

    // 配置全局设置
    // 全局配置
    GlobalConfig globalConfig = new GlobalConfig();
    globalConfig.setOpen(false);
    // 设置输出目录
    //todo 要改输出路径
    globalConfig.setOutputDir(System.getProperty("user.dir") + "/mychat-chat-server/src/main/java");
    // 设置作者信息
    // 设置作者名字
    globalConfig.setAuthor("<a href=\"https://gitee.com/laosuye\">laosuye</a>");
    // 设置Service实现类的命名模式
    //去掉service的I前缀,一般只需要设置service就行
    globalConfig.setServiceImplName("%sDao");
    autoGenerator.setGlobalConfig(globalConfig);

    // 配置包信息
    // 包配置
    PackageConfig packageConfig = new PackageConfig();
    packageConfig.setParent("com.laosuye.mychat.common.user");//自定义包的路径
    packageConfig.setEntity("domain.entity");
    packageConfig.setMapper("mapper");
    packageConfig.setController("controller");
    packageConfig.setServiceImpl("dao");
    autoGenerator.setPackageInfo(packageConfig);

    // 配置生成策略
    // 策略配置
    StrategyConfig strategyConfig = new StrategyConfig();
    // 是否使用Lombok简化实体类代码
    // 是否使用Lombok
    strategyConfig.setEntityLombokModel(true);
    // 设置表和字段的命名转换策略
    // 包，列的命名规则，使用驼峰规则
    strategyConfig.setNaming(NamingStrategy.underline_to_camel);
//        strategyConfig.setTablePrefix("t_");
    strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
    // 启用字段和表注解
    // 字段和表注解
    strategyConfig.setEntityTableFieldAnnotationEnable(true);
    // 指定需要生成代码的表名
    //todo 这里修改需要自动生成的表结构
    strategyConfig.setInclude(
            "user_friend"
    );
    //配置忽略表前缀
    strategyConfig.setTablePrefix("user");
    // 配置自动填充字段
    // 自动填充字段,在项目开发过程中,例如创建时间，修改时间,每次，都需要我们来指定，太麻烦了,设置为自动填充规则，就不需要我们赋值咯
    List<TableFill> list = new ArrayList<TableFill>();
    TableFill tableFill1 = new TableFill("create_time", FieldFill.INSERT);
    TableFill tableFill2 = new TableFill("update_time", FieldFill.INSERT_UPDATE);
    list.add(tableFill1);
    list.add(tableFill2);

//        strategyConfig.setTableFillList(list);
    autoGenerator.setStrategy(strategyConfig);

    // 执行代码生成
    // 执行
    autoGenerator.execute();

}

    /**
     * 配置开发环境的数据源。
     * 该方法用于设置数据源的相关属性，以便连接到开发环境的数据库。
     *
     * @param dataSourceConfig 数据源配置对象，用于存储和管理数据库连接配置信息。
     *                         通过设置该对象的属性，可以配置数据库连接的驱动、用户名、密码和URL等信息。
     */
    //todo 这里修改你的数据源
    public static void assembleDev(DataSourceConfig dataSourceConfig) {
        // 设置数据库驱动名称
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        // 设置数据库用户名
        dataSourceConfig.setUsername("root");
        // 设置数据库密码
        dataSourceConfig.setPassword("YEye1123");
        // 设置数据库连接URL，包括数据库地址、端口、数据库名称以及相关连接参数
        dataSourceConfig.setUrl("jdbc:mysql://39.106.35.36:3306/mychat?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC");
    }

}