package com.mahanko.threadstask;

import com.mahanko.threadstask.entity.CargoShip;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<CargoShip> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CargoShip ship = new CargoShip();
            if (i % 2 == 1) {
                ship.setCurrentContainerAmount(5);
            }
            ship.setName("Cargo ship №" + (i + 1));
            list.add(ship);
        }

        for (CargoShip s : list) {
            s.start();
        }

    }
}
