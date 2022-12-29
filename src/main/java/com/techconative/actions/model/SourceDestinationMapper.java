package com.techconative.actions.model;

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
  @Mapping(
      target = "count",
      ignore = true
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
  @Mapping(
      target = "count",
      ignore = true
  )
  public abstract Source toSource(Destination destination);
}
