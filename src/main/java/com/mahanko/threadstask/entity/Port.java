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

    public Pier getPier() { // FIXME: 22.03.2022 exception
        Pier pier = null;
        piersManipulationLock.lock();
        try {
            while ((pier = freePiers.poll()) == null) {
                logger.log(Level.INFO, "Thread {} waiting of free pier", Thread.currentThread().getName());
                pierCondition.await();
            }

            logger.log(Level.INFO, "To thread {} appointed pier with id:{} .", Thread.currentThread().getName(), pier.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            piersManipulationLock.unlock();
        }

        return pier;
    }

    public void addPier(Pier pier) {
        piersManipulationLock.lock();
        try {
            freePiers.add(pier);
            logger.log(Level.INFO, "Pier with id:{} appointed to thread {} was released.", pier.getId(), Thread.currentThread().getName());
            pierCondition.signalAll();
        } finally {
            piersManipulationLock.unlock();
        }
    }

    public void reserveSpaceForCargo(int spaceToReserve) { // FIXME: 24.03.2022 exception
        logger.log(Level.INFO, "Thread {} loading started.", Thread.currentThread().getName());
        cargoManipulationLock.lock();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            while (currentContainersAmount - spaceToReserve < MIN_WAREHOUSE_RESERVE) {
                logger.log(Level.INFO, "Thread {} waiting for cargo started.", Thread.currentThread().getName());
                cargoCondition.await();
            }

            TimeUnit.MILLISECONDS.sleep(100);
            currentContainersAmount -= spaceToReserve;
            logger.log(Level.INFO, "Thread {} loading ended.", Thread.currentThread().getName());
            cargoCondition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cargoManipulationLock.unlock();
        }
    }

    public void freeSpaceFromCargo(int spaceToFree) { // FIXME: 24.03.2022 exception
        logger.log(Level.INFO, "Thread {} unloading started.", Thread.currentThread().getName());
        cargoManipulationLock.lock();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            while (currentContainersAmount + spaceToFree > MAX_WAREHOUSE_CAPACITY) {
                logger.log(Level.INFO, "Thread {} waiting for free space started.", Thread.currentThread().getName());
                cargoCondition.await();
            }

            TimeUnit.MILLISECONDS.sleep(100);
            currentContainersAmount += spaceToFree;
            logger.log(Level.INFO, "Thread {} unloading ended.", Thread.currentThread().getName());
            cargoCondition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cargoManipulationLock.unlock();
        }
    }
}
