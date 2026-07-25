package com.quickboot.web.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

/**
 * MyBatis-Plus 代码生成器（测试期工具，不参与打包，不影响运行时）。
 *
 * <p>使用说明：</p>
 * <ol>
 *   <li>修改下方 {@link #JDBC_URL}、{@link #JDBC_USERNAME}、{@link #JDBC_PASSWORD} 为真实数据库连接信息。</li>
 *   <li>在 {@link #TABLES} 中填入需要生成代码的表名（可填多个）。</li>
 *   <li>按需调整 {@link #TABLE_PREFIX}（表名前缀，生成时会自动剔除）。</li>
 *   <li>右键运行 {@link #main(String[])} 即可生成 Entity / Mapper / Service / Controller 代码。</li>
 * </ol>
 *
 * <p>生成策略：</p>
 * <ul>
 *   <li>父包：{@code com.quickboot}，按层分包（entity / mapper / service / controller）。</li>
 *   <li>命名：数据库下划线转驼峰；启用 Lombok；逻辑删除字段 {@code deleted}；乐观锁字段 {@code version}。</li>
 *   <li>Controller：Rest 风格（{@code @RestController}）。</li>
 *   <li>模板引擎：Velocity。</li>
 * </ul>
 *
 * <p>生成的代码结构（以表 {@code t_user} 为例）：</p>
 * <pre>
 * com.quickboot
 * ├── entity
 * │   └── UserEntity.java          // 实体类（含 Lombok 注解、逻辑删除、乐观锁）
 * ├── mapper
 * │   └── UserMapper.java           // Mapper 接口（继承 BaseMapper）
 * ├── service
 * │   ├── UserService.java          // Service 接口（继承 IService）
 * │   └── impl
 * │       └── UserServiceImpl.java  // Service 实现（继承 ServiceImpl）
 * └── controller
 *     └── UserController.java       // RestController（继承 BaseController）
 * resources/mapper
 *     └── UserMapper.xml            // Mapper XML（自定义 SQL）
 * </pre>
 *
 * <p>注意：本类位于 test 目录，不参与运行时打包。生成的代码默认输出到 web 模块的
 * {@code src/main/java} 下；多模块场景下，请按需将 Entity / Mapper 等输出目录调整到对应模块。</p>
 */
public class CodeGenerator {

    // ==================== 数据源配置（请修改为真实值） ====================
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/quickboot?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "root";
    // 驱动类由 FastAutoGenerator 根据 JDBC_URL 自动识别（mysql -> com.mysql.cj.jdbc.Driver）

    // ==================== 代码生成配置 ====================
    /** 父包名 */
    private static final String PARENT_PACKAGE = "com.quickboot";
    /** 模块名（留空则不生成子包层级，按需修改） */
    private static final String MODULE_NAME = "";
    /** 代码输出目录（默认输出到 web 模块的 src/main/java） */
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/java";
    /** Mapper XML 输出目录 */
    private static final String XML_OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/resources/mapper";
    /** 表名前缀（生成时剔除） */
    private static final String TABLE_PREFIX = "t_";
    /** 需要生成代码的表名（请按需修改） */
    private static final String[] TABLES = {"t_user"};

    /**
     * 代码生成入口。直接运行即可生成对应表的分层代码。
     *
     * @param args 启动参数（未使用）
     */
    public static void main(String[] args) {
        FastAutoGenerator.create(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD) // 创建代码生成器，传入数据源配置
                .globalConfig(builder -> builder          // —— 全局配置 ——
                        .author("quickboot")              // 作者名（写入类注释）
                        .outputDir(OUTPUT_DIR)            // 代码输出目录
                        .disableOpenDir()                 // 禁止生成后自动打开文件夹
                )
                .packageConfig(builder -> builder        // —— 包配置 ——
                        .parent(PARENT_PACKAGE)           // 父包名
                        .moduleName(MODULE_NAME)           // 模块名（留空则不分模块子包）
                        .pathInfo(Collections.singletonMap(OutputFile.xml, XML_OUTPUT_DIR)) // Mapper XML 输出路径
                )
                .strategyConfig(builder -> builder       // —— 策略配置 ——
                        .addInclude(TABLES)               // 包含的表名
                        .addTablePrefix(TABLE_PREFIX)     // 表名前缀（生成时剔除）
                        .entityBuilder()                  // —— 实体类策略 ——
                            .naming(NamingStrategy.underline_to_camel)        // 表名下划线转驼峰
                            .columnNaming(NamingStrategy.underline_to_camel)  // 字段名下划线转驼峰
                            .enableLombok()               // 启用 Lombok（@Data 等）
                            .logicDeleteColumnName("deleted") // 逻辑删除字段
                            .versionColumnName("version")    // 乐观锁字段
                            .formatFileName("%sEntity")      // 文件名格式：XxxEntity
                        .mapperBuilder()                  // —— Mapper 策略 ——
                            .formatMapperFileName("%sMapper")  // Mapper 文件名：XxxMapper
                            .formatXmlFileName("%sMapper")     // XML 文件名：XxxMapper
                        .serviceBuilder()                 // —— Service 策略 ——
                            .formatServiceFileName("%sService")    // 接口名：XxxService
                            .formatServiceImplFileName("%sServiceImpl") // 实现名：XxxServiceImpl
                        .controllerBuilder()              // —— Controller 策略 ——
                            .enableRestStyle()            // 使用 @RestController
                            .formatFileName("%sController") // 文件名：XxxController
                )
                .templateEngine(new VelocityTemplateEngine()) // 使用 Velocity 模板引擎
                .execute(); // 执行生成
    }
}
