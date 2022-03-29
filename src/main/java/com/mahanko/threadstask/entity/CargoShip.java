package com.mahanko.threadstask.entity;

import com.mahanko.threadstask.exception.CustomThreadException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CargoShip implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    public final int maxContainerAmount;
    private int currentContainerAmount;
    private Pier pier;
    private CargoShipState state;

    public CargoShip(CustomProperties properties) {
        maxContainerAmount = properties.getMaxShipContainerAmount();
        state = CargoShipState.CREATED;
    }

    public CargoShipState getShipState() {
        return state;
    }

    public void setShipState(CargoShipState state) {
        this.state = state;
    }

    public void setPier(Pier pier) {
        this.pier = pier;
    }

    public int getCurrentContainerAmount() {
        return currentContainerAmount;
    }

    public void setCurrentContainerAmount(int newContainerAmount) {
        if (newContainerAmount > maxContainerAmount) {
            newContainerAmount = maxContainerAmount;
        }

        currentContainerAmount = newContainerAmount;
    }

    public Pier getPier() {
        return pier;
    }

    public void loadCargo() throws CustomThreadException {
        Port.getInstance().reserveSpaceForCargo(maxContainerAmount);
        currentContainerAmount = maxContainerAmount;
    }

    public void unloadCargo() throws CustomThreadException {
        Port.getInstance().freeSpaceFromCargo(currentContainerAmount);
        currentContainerAmount = 0;
    }

    @Override
    public void run() {
        try {
            Port port = Port.getInstance();
            logger.log(Level.INFO, "Thread {} pier appointing started", Thread.currentThread().getName());
            setShipState(CargoShipState.WAITING);
            setPier(port.getPier());
            setShipState(CargoShipState.PROCESSING);
            if (currentContainerAmount == 0) {
                loadCargo();
            } else {
                unloadCargo();
            }
            Pier oldPier = getPier();
            setPier(null);
            port.addPier(oldPier);
            state = CargoShipState.SERVED;
        } catch (CustomThreadException e) {
            logger.log(Level.ERROR, e);
        }
    }
}
