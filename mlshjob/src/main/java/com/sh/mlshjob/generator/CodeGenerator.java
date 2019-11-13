package com.sh.mlshjob.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        GlobalConfig globalConfig = new GlobalConfig();
//        globalConfig.setOutputDir("D:\\codecreate");
//        globalConfig.setAuthor("yujinghui");
        globalConfig.setOutputDir(projectPath + "/mlshsettlement" + "/src/main/java");
        globalConfig.setAuthor("jinghui.yu");
        globalConfig.setOpen(false);
        globalConfig.setFileOverride(false);

        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.sh.mlshsettlement");
        packageConfig.setController("controller");
        packageConfig.setEntity("model");
        packageConfig.setMapper("mapper");
        packageConfig.setService("service");
        packageConfig.setServiceImpl("service.impl");
//
//        packageConfig.setXml("resources/mapper");

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl("jdbc:mysql://118.89.231.92:3306/sh_mlsh_life?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai");
        dataSourceConfig.setDriverName("com.mysql.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("1q2w3e");

        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        strategy.setInclude(new String[]{"trade_info"});
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityTableFieldAnnotationEnable(true);

        AutoGenerator mpg = new AutoGenerator();
        mpg.setDataSource(dataSourceConfig);
        mpg.setGlobalConfig(globalConfig);
        mpg.setStrategy(strategy);
        mpg.setPackageInfo(packageConfig);
        mpg.execute();
    }
}
