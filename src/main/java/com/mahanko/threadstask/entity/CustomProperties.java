package com.mahanko.threadstask.entity;

public class CustomProperties {
    private int maxPortWarehouseCapacity = 20;
    private int minPortWarehouseReserve = 0;
    private int maxPortPiersAmount = 5;
    private int maxShipContainerAmount = 5;

    public CustomProperties() {
    }

    public static  PropertiesBuilder newBuilder() {
        return new CustomProperties().new PropertiesBuilder();
    }

    public int getMaxPortPiersAmount() {
        return maxPortPiersAmount;
    }

    public int getMaxPortWarehouseCapacity() {
        return maxPortWarehouseCapacity;
    }

    public int getMinPortWarehouseReserve() {
        return minPortWarehouseReserve;
    }

    public int getMaxShipContainerAmount() {
        return maxShipContainerAmount;
    }

    public class PropertiesBuilder {
        private PropertiesBuilder() {
        }

        public PropertiesBuilder setMaxPortWarehouseCapacity(int capacity) {
            CustomProperties.this.maxPortWarehouseCapacity = capacity;
            return this;
        }

        public PropertiesBuilder setMinPortWarehouseReserve(int reserve) {
            CustomProperties.this.minPortWarehouseReserve = reserve;
            return this;
        }

        public PropertiesBuilder setMaxPortPiersAmount(int amount) {
            CustomProperties.this.maxPortPiersAmount = amount;
            return this;
        }

        public PropertiesBuilder setMaxShipContainerAmount(int amount) {
            CustomProperties.this.maxShipContainerAmount = amount;
            return this;
        }

        public CustomProperties build() {
            return CustomProperties.this;
        }
    }
}
