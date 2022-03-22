package com.mahanko.threadstask.entity;

import com.mahanko.threadstask.util.PierIdGenerator;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static Port instance;
    private static final int MAX_WAREHOUSE_CAPACITY = 20;
    private static final int MIN_WAREHOUSE_RESERVE = 0;
    private static final int MAX_PIERS_AMOUNT = 5;
    private static final ReentrantLock lock = new ReentrantLock(true);
    private static final Condition pierCondition = lock.newCondition();
    private static final Condition warehouseOverloadedCondition = lock.newCondition();
    private static final Condition warehouseEmptyCondition = lock.newCondition();
    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    private Queue<Pier> freePiers;
    private int currentContainersAmount;

    private Port() {
        freePiers = new LinkedList<>();
        for (int i = 0; i < MAX_PIERS_AMOUNT; i++) {
            freePiers.add(new Pier(PierIdGenerator.generateId()));
        }
    }

    public static Port getInstance() {
        if (!isCreated.get()) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new Port();
                    isCreated.set(true);
                }
            } finally {
                lock.unlock();
            }
        }

        return instance;
    }

    public void appointPierToShip(CargoShip ship) { // FIXME: 22.03.2022 exception
        lock.lock();
        try {
            while (freePiers.isEmpty()) {
                pierCondition.await();
            }

            ship.setPier(freePiers.poll());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void releasePierFromShip(CargoShip ship) {
        lock.lock();
        try {
            freePiers.add(ship.getPier());
            ship.setPier(null);
            pierCondition.signal();
        } finally {
            lock.unlock();
        }
    }


    public void serveShip(CargoShip ship) { // FIXME: 22.03.2022 lock?
        lock.unlock();
        try {
            if (ship.getCurrentContainerAmount() == 0) {
                loadShip(ship);
            } else {
                unloadShip(ship);
            }
        } finally {
            lock.unlock();
        }
    }

    public void loadShip(CargoShip ship) { // FIXME: 22.03.2022 exception
        lock.lock();
        try {
            while (currentContainersAmount - ship.MAX_CONTAINER_AMOUNT < MIN_WAREHOUSE_RESERVE) {
                warehouseEmptyCondition.await();
            }

            currentContainersAmount -= ship.MAX_CONTAINER_AMOUNT;
            ship.setCurrentContainerAmount(ship.MAX_CONTAINER_AMOUNT);
            warehouseOverloadedCondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void unloadShip(CargoShip ship) { // FIXME: 22.03.2022 exception
        lock.lock();
        try {
            while (currentContainersAmount + ship.getCurrentContainerAmount() > MAX_WAREHOUSE_CAPACITY) {
                warehouseOverloadedCondition.await();
            }

            currentContainersAmount += ship.getCurrentContainerAmount();
            ship.setCurrentContainerAmount(0);
            warehouseEmptyCondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
