package com.techconative.actions.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class Source3Destination3Mapper {
  public static Source3Destination3Mapper personMapper = Mappers.getMapper(Source3Destination3Mapper.class);

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
  public abstract Destination3 toDestination3(Source3 source3);

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
  public abstract Source3 toSource3(Destination3 destination3);
}
