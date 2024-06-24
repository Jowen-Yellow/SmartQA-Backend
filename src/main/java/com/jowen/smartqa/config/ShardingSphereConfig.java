package com.jowen.smartqa.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.algorithm.core.config.AlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.single.api.config.SingleRuleConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Data
@Configuration
@ConfigurationProperties(prefix = "sharding")
public class ShardingSphereConfig {
    private String host;
    private String username;
    private String password;

    @Bean
    public DataSource dataSource() {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = getStringDataSourceMap();

        // 配置规则
        List<RuleConfiguration> ruleConfigList = getShardingRuleConfig();

        // 配置属性
        Properties props = new Properties();
        props.put("sql-show", true);

        // 创建 ShardingSphereDataSource
        try {
            return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, ruleConfigList, props);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull List<RuleConfiguration> getShardingRuleConfig() {
        // 配置 user_answer 表规则
        ShardingTableRuleConfiguration orderTableRuleConfig = new ShardingTableRuleConfiguration("user_answer", "smartqa.user_answer_$->{0..1}");

        // 配置分表策略
        orderTableRuleConfig.setTableShardingStrategy(new StandardShardingStrategyConfiguration("appId", "answerTableInline"));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTables().add(orderTableRuleConfig);

        // 配置分表算法
        Properties tableShardingAlgorithmProps = new Properties();
        tableShardingAlgorithmProps.setProperty("algorithm-expression", "user_answer_$->{appId % 2}");
        shardingRuleConfig.getShardingAlgorithms().put("answerTableInline", new AlgorithmConfiguration("INLINE", tableShardingAlgorithmProps));

        // 配置单表
        SingleRuleConfiguration singleRuleConfig = new SingleRuleConfiguration();
        singleRuleConfig.getTables().add("smartqa.*");
        return List.of(shardingRuleConfig, singleRuleConfig);
    }

    private @NotNull Map<String, DataSource> getStringDataSourceMap() {
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 配置第 1 个数据源
        HikariDataSource smartQADataSource = new HikariDataSource();
        smartQADataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        smartQADataSource.setJdbcUrl("jdbc:mysql://" + host + ":3306/smartqa?useTimezone=true&serverTimezone=Asia/Shanghai");
        smartQADataSource.setUsername(username);
        smartQADataSource.setPassword(password);
        dataSourceMap.put("smartqa", smartQADataSource);
        return dataSourceMap;
    }
}
