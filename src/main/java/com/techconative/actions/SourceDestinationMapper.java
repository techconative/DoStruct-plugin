package com.techconative.actions;

import com.techconative.actions.model.Destination;
import com.techconative.actions.model.Source;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class SourceDestinationMapper {
  public static SourceDestinationMapper personMapper = Mappers.getMapper(SourceDestinationMapper.class);

  @Mapping(
      source = "name",
      target = "nameD"
  )
  @Mapping(
      source = "description",
      target = "descriptionD"
  )
  public abstract Destination toDestination(Source source);

  @Mapping(
      source = "nameD",
      target = "name"
  )
  @Mapping(
      source = "descriptionD",
      target = "description"
  )
  public abstract Source toSource(Destination destination);
}
