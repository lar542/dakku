package com.popol.dakku.config.root;

import java.io.IOException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.popol.dakku.modules.commons.auth.vo.UserVO;
import com.popol.dakku.modules.commons.vo.PaginationInfoVO;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.popol.dakku.modules.web", sqlSessionFactoryRef = "sessionFactory")
public class MybatisContext implements TransactionManagementConfigurer {
	
	@Autowired
	private DataSource dataSource;
	
	@Override
	@Bean
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}
	
	@Bean
	public SqlSessionFactoryBean sessionFactory() throws IOException {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/popol/dakku/modules/web/**/*.xml"));
		Class<?>[] typeAliases = {UserVO.class, PaginationInfoVO.class};
		sessionFactoryBean.setTypeAliases(typeAliases);
		return sessionFactoryBean;
	}
}
