package com.techconative.actions.mappers;

import com.techconative.actions.model.Destination;
import com.techconative.actions.model.Destination2;
import com.techconative.actions.model.Destination3;
import com.techconative.actions.model.Source;
import com.techconative.actions.model.Source2;
import com.techconative.actions.model.Source3;
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
}
