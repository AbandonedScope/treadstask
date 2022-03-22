package com.mahanko.threadstask.entity;

import com.mahanko.threadstask.util.PierIdGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static final Logger logger = LogManager.getLogger();
    private static Port instance;
    private static final int MAX_WAREHOUSE_CAPACITY = 20;
    private static final int MIN_WAREHOUSE_RESERVE = 0;
    private static final int MAX_PIERS_AMOUNT = 5;
    private static final ReentrantLock piersManipulationLock = new ReentrantLock();
    private static final ReentrantLock cargoManipulationLock = new ReentrantLock();
    private static final Condition pierCondition = piersManipulationLock.newCondition();
    private static final Condition cargoCondition = cargoManipulationLock.newCondition();
    private static final AtomicBoolean isCreated = new AtomicBoolean(false);
    private final Queue<Pier> freePiers;
    private int currentContainersAmount;

    private Port() {
        freePiers = new LinkedList<>();
        for (int i = 0; i < MAX_PIERS_AMOUNT; i++) {
            freePiers.add(new Pier(PierIdGenerator.generateId()));
        }
    }

    public static Port getInstance() {
        if (!isCreated.get()) {
            piersManipulationLock.lock();
            try {
                if (instance == null) {
                    instance = new Port();
                    isCreated.set(true);
                }
            } finally {
                piersManipulationLock.unlock();
            }
        }

        return instance;
    }

    public void appointPierToShip(CargoShip ship) { // FIXME: 22.03.2022 exception
        logger.log(Level.INFO, "Thread {} pier appointing started", Thread.currentThread().getName());
        ship.setShipState(CargoShipState.PROCESSING);
        piersManipulationLock.lock();
        try {
            Pier pier;
            while ((pier = freePiers.poll()) == null) {
                logger.log(Level.INFO, "Thread {} waiting of free pier", Thread.currentThread().getName());
                ship.setShipState(CargoShipState.WAITING);
                pierCondition.await();
            }

            ship.setShipState(CargoShipState.PROCESSING);
            ship.setPier(pier);
            logger.log(Level.INFO, "To thread {} appointed pier with id:{} .", Thread.currentThread().getName(), pier.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            piersManipulationLock.unlock();
            try {
                TimeUnit.MILLISECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void releasePierFromShip(CargoShip ship) {
        piersManipulationLock.lock();
        try {
            Pier pier = ship.getPier();
            freePiers.add(pier);
            logger.log(Level.INFO, "Pier with id:{} appointed to thread {} was released.", pier.getId(), Thread.currentThread().getName());
            ship.setPier(null);
            pierCondition.signalAll();
        } finally {
            piersManipulationLock.unlock();
        }
    }


    public void serveShip(CargoShip ship) {
        logger.log(Level.INFO, "Thread {} processing started.", Thread.currentThread().getName());
        ship.setShipState(CargoShipState.WAITING);
        if (ship.getCurrentContainerAmount() == 0) {
            loadShip(ship);
        } else {
            unloadShip(ship);
        }
    }

    public void loadShip(CargoShip ship) { // FIXME: 22.03.2022 exception
        logger.log(Level.INFO, "Thread {} loading started.", Thread.currentThread().getName());
        ship.setShipState(CargoShipState.PROCESSING);
        cargoManipulationLock.lock();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            while (currentContainersAmount - CargoShip.MAX_CONTAINER_AMOUNT < MIN_WAREHOUSE_RESERVE) {
                logger.log(Level.INFO, "Thread {} waiting for cargo started.", Thread.currentThread().getName());
                ship.setShipState(CargoShipState.WAITING);
                cargoCondition.await();
            }

            ship.setShipState(CargoShipState.PROCESSING);
            TimeUnit.MILLISECONDS.sleep(100);
            currentContainersAmount -= CargoShip.MAX_CONTAINER_AMOUNT;
            ship.setCurrentContainerAmount(CargoShip.MAX_CONTAINER_AMOUNT);
            logger.log(Level.INFO, "Thread {} loading ended.", Thread.currentThread().getName());
            cargoCondition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cargoManipulationLock.unlock();
        }
    }

    public void unloadShip(CargoShip ship) { // FIXME: 22.03.2022 exception
        logger.log(Level.INFO, "Thread {} unloading started.", Thread.currentThread().getName());
        ship.setShipState(CargoShipState.PROCESSING);
        cargoManipulationLock.lock();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            while (currentContainersAmount + ship.getCurrentContainerAmount() > MAX_WAREHOUSE_CAPACITY) {
                logger.log(Level.INFO, "Thread {} waiting for free space started.", Thread.currentThread().getName());
                ship.setShipState(CargoShipState.WAITING);
                cargoCondition.await();
            }

            ship.setShipState(CargoShipState.PROCESSING);
            TimeUnit.MILLISECONDS.sleep(100);
            currentContainersAmount += ship.getCurrentContainerAmount();
            ship.setCurrentContainerAmount(0);
            logger.log(Level.INFO, "Thread {} unloading ended.", Thread.currentThread().getName());
            cargoCondition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cargoManipulationLock.unlock();
        }
    }

}
