package com.mahanko.threadstask.entity;

public class CargoShip extends Thread {
    public static final int MAX_CONTAINER_AMOUNT = 5;
    private int currentContainerAmount;
    private Pier pier;
    private CargoShipState state;

    public CargoShip() {
        state = CargoShipState.CREATED;
    }

    public CargoShipState getShipState() {
        return  state;
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
        currentContainerAmount = newContainerAmount;
    }

    public Pier getPier() {
        return  pier;
    }

    @Override
    public void run() {
        Port port= Port.getInstance();
        port.appointPierToShip(this);
        port.serveShip(this);
        port.releasePierFromShip(this);
        state = CargoShipState.SERVED;
    }
}
