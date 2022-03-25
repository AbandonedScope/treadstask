package com.mahanko.threadstask;

import com.mahanko.threadstask.entity.CargoShip;
import com.mahanko.threadstask.entity.CustomProperties;
import com.mahanko.threadstask.entity.Port;
import com.mahanko.threadstask.exception.CustomThreadException;
import com.mahanko.threadstask.parser.CustomPropertiesParser;
import com.mahanko.threadstask.reader.CustomFileReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        try {
            CustomFileReader reader = new CustomFileReader();
            CustomPropertiesParser parser = new CustomPropertiesParser();
            CustomProperties properties = parser.parse(reader.readFile("src/main/resources/properties.txt"));
            Port.setProperties(properties);
            List<CargoShip> list = new ArrayList<>();
            for (int i = 0; i < properties.getShipsAmount(); i++) {
                CargoShip ship = new CargoShip(properties);
                if (i < properties.getShipsWithCargoAmount()) {
                    ship.setCurrentContainerAmount(properties.getCargoPerShipAmount());
                }
                ship.setName("Cargo ship №" + (i + 1));
                list.add(ship);
            }

            for (CargoShip s : list) {
                s.start();
            }
        } catch (CustomThreadException e) {
            logger.log(Level.ERROR, e);
        }
    }
}
