package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {

	public List<Department> findAll() {
		List<Department> list = new ArrayList<>();
		list.add(new Department(1, "Tecnologia da Informação"));
		list.add(new Department(2, "CyberSegurança"));
		list.add(new Department(3, "Inovação e Governança"));
		list.add(new Department(4, "Infraestrutura"));
		list.add(new Department(5, "SAC"));
		return list;
	}

}
