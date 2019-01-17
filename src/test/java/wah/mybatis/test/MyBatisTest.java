package wah.mybatis.test;

import org.junit.Test;

import wah.mybatis.bean.User;
import wah.mybatis.mapper.UserMapper;
import wah.mybatis.sqlsession.MySqlsession;

public class MyBatisTest {

	// @Ignore
	@Test
	public void selectTest() {
		
		MySqlsession sqlsession=new MySqlsession();  
		UserMapper mapper = sqlsession.getMapper(UserMapper.class);  
		User user = mapper.getUserById(1);  
		
		System.out.println(user);
	}
}
