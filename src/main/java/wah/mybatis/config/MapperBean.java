package wah.mybatis.config;

import java.util.List;

public class MapperBean {

	private String interfaceName; // �ӿ���
	private List<Function> list; // �ӿ������з���

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public List<Function> getList() {
		return list;
	}

	public void setList(List<Function> list) {
		this.list = list;
	}

}
