package wah.mybatis.sqlsession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import wah.mybatis.config.Function;
import wah.mybatis.config.MapperBean;

public class MyMapperProxy implements InvocationHandler {
	private MySqlsession mySqlsession;

	private MyConfiguration myConfiguration;

	public MyMapperProxy(MyConfiguration myConfiguration, MySqlsession mySqlsession) {
		this.myConfiguration = myConfiguration;
		this.mySqlsession = mySqlsession;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MapperBean readMapper = myConfiguration.readMapper("UserMapper.xml");
		
		// �Ƿ���xml�ļ���Ӧ�Ľӿ�
		if (!method.getDeclaringClass().getName().equals(readMapper.getInterfaceName())) {
			return null;
		}
		
		List<Function> list = readMapper.getList();
		if (null != list || 0 != list.size()) {
			for (Function function : list) {
				// id�Ƿ�ͽӿڷ�����һ��
				if (method.getName().equals(function.getFuncName())) {
					return mySqlsession.selectOne(function.getSql(), String.valueOf(args[0]));
				}
			}
		}
		
		return null;
	}
}
