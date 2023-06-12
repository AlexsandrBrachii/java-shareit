package ru.practicum.shareit.booking;

public enum BookingStatus {

    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public String toString() {
        return this.name();
    }
}
