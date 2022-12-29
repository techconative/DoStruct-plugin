package com.techconative.actions.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class Source2Destination2Mapper {
  public static Source2Destination2Mapper personMapper = Mappers.getMapper(Source2Destination2Mapper.class);

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
  public abstract Destination2 toDestination2(Source2 source2);

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
  public abstract Source2 toSource2(Destination2 destination2);
}
