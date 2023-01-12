package com.techconative.actions.mappers;

import com.techconative.actions.model.DAO.EmployeeDAO;
import com.techconative.actions.model.DAO.StudentDAO;
import com.techconative.actions.model.DTO.EmployeeDTO;
import com.techconative.actions.model.DTO.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class EmployeeDAOEmployeeDTO {
  public static EmployeeDAOEmployeeDTO mapper = Mappers.getMapper(EmployeeDAOEmployeeDTO.class);

  @Mappings({
      @Mapping(source = "name", target = "empName", qualifiedByName = "referenceMethod"),
      @Mapping(source = "description", target = "empDescription"),
      @Mapping(target = "empNumber", ignore = true)
  })
  @Named("customMap")
  public abstract EmployeeDTO toEmployeeDTO(EmployeeDAO employeeDAO);

  @Mappings({
      @Mapping(source = "name", target = "studentName"),
      @Mapping(source = "description", target = "studentDescription"),
      @Mapping(target = "studentAge", ignore = true)
  })
  public abstract StudentDTO toStudentDTO(StudentDAO studentDAO);
}
