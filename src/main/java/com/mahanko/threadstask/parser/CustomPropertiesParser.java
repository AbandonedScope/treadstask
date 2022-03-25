package com.mahanko.threadstask.parser;

import com.mahanko.threadstask.entity.CustomProperties;
import com.mahanko.threadstask.exception.CustomThreadException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.mahanko.threadstask.parser.CustomPropertiesTags.*;

public class CustomPropertiesParser {
    public static final Logger logger = LogManager.getLogger();
    private CustomPropertiesParser() {
    }

    public static CustomProperties parse(List<String> properties) throws CustomThreadException {
        CustomProperties.PropertiesBuilder builder = CustomProperties.newBuilder();
        for (String propertyLine: properties) {
            String[] list = propertyLine.split(" ");
            int value = Integer.parseInt(list[1]);
            CustomPropertiesTags property = valueOf(list[0].toUpperCase(Locale.ROOT).replace('-','_'));
            switch (property) {
                case SHIP_MAX_CONTAINER_AMOUNT:
                    builder.setMaxShipContainerAmount(value);
                    break;
                case PORT_MAX_PIERS_AMOUNT:
                    builder.setMaxPortPiersAmount(value);
                    break;
                case PORT_MIN_WAREHOUSE_RESERVE:
                    builder.setMinPortWarehouseReserve(value);
                    break;
                case PORT_MAX_WAREHOUSE_CAPACITY:
                    builder.setMaxPortWarehouseCapacity(value);
                    break;
                default:
                    logger.log(Level.ERROR, "Unexpected value: {}" , list[0]);
                    throw new CustomThreadException("Unexpected value: " + list[0]);
            }
        }

        return  builder.build();
    }
}
