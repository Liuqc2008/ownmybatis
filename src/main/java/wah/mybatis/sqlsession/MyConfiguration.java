package wah.mybatis.sqlsession;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wah.mybatis.config.Function;
import wah.mybatis.config.MapperBean;

public class MyConfiguration {

	private static ClassLoader loader = ClassLoader.getSystemClassLoader();

	/**
	 * ��ȡxml��Ϣ������
	 */
	public Connection build(String resource) {
		try {
			InputStream stream = loader.getResourceAsStream(resource);
			SAXReader reader = new SAXReader();
			Document document = reader.read(stream);
			Element root = document.getRootElement();
			return evalDataSource(root);
		} catch (Exception e) {
			throw new RuntimeException("error occured while evaling xml " + resource);
		}
	}

	private Connection evalDataSource(Element node) throws ClassNotFoundException {
		if (!node.getName().equals("database")) {
			throw new RuntimeException("root should be <database>");
		}
		String driverClassName = null;
		String url = null;
		String username = null;
		String password = null;
		// ��ȡ���Խڵ�
		for (Object item : node.elements("property")) {
			Element i = (Element) item;
			String value = getValue(i);
			String name = i.attributeValue("name");
			if (name == null || value == null) {
				throw new RuntimeException("[database]: <property> should contain name and value");
			}
			
			// ��ֵ
			switch (name) {
			case "url":
				url = value;
				break;
			case "username":
				username = value;
				break;
			case "password":
				password = value;
				break;
			case "driverClassName":
				driverClassName = value;
				break;
			default:
				throw new RuntimeException("[database]: <property> unknown name");
			}
		}

		Class.forName(driverClassName);
		Connection connection = null;
		try {
			// �������ݿ�����
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	// ��ȡproperty���Ե�ֵ,�����valueֵ,���ȡ û������value,���ȡ����
	private String getValue(Element node) {
		return node.hasContent() ? node.getText() : node.attributeValue("value");
	}

	@SuppressWarnings("rawtypes")
	public MapperBean readMapper(String path) {
		MapperBean mapper = new MapperBean();
		try {
			InputStream stream = loader.getResourceAsStream(path);
			SAXReader reader = new SAXReader();
			Document document = reader.read(stream);
			Element root = document.getRootElement();
			mapper.setInterfaceName(root.attributeValue("nameSpace").trim()); // ��mapper�ڵ��nameSpaceֵ��Ϊ�ӿ���
			List<Function> list = new ArrayList<Function>(); // �����洢������List
			for (Iterator rootIter = root.elementIterator(); rootIter.hasNext();) {// �������ڵ��������ӽڵ�
				Function fun = new Function(); // �����洢һ����������Ϣ
				Element e = (Element) rootIter.next();
				String sqltype = e.getName().trim();
				String funcName = e.attributeValue("id").trim();
				String sql = e.getText().trim();
				String resultType = e.attributeValue("resultType").trim();
				fun.setSqltype(sqltype);
				fun.setFuncName(funcName);
				Object newInstance = null;
				try {
					newInstance = Class.forName(resultType).newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				fun.setResultType(newInstance);
				fun.setSql(sql);
				list.add(fun);
			}
			mapper.setList(list);

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return mapper;
	}
}
