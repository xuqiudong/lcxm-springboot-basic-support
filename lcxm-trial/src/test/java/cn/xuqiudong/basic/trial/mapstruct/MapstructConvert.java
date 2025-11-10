package cn.xuqiudong.basic.trial.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-11-08 13:53
 */
@Mapper(componentModel = "spring")
public interface MapstructConvert {

    MapstructConvert INSTANCE = Mappers.getMapper(MapstructConvert.class);


    @Mappings({

            @Mapping(source = "priceDto", target = "price", numberFormat = "#.00")

            }
    )
    MapStructSourceModel toModel(MapStructSourceDto sourceDto);

    @Mappings(
            {

                    @Mapping(source = "price", target = "priceDto", numberFormat = "#.00")
            }
    )
    MapStructSourceDto toDto(MapStructSourceModel sourceModel);
}
