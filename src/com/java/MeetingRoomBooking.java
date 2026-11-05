package com.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MeetingRoomBooking {
    public static void main(String[] args) {
        MeetingRoomBookingSystem system = new MeetingRoomBookingSystem();

        String r1 = system.createRoom(4);
        String r2 = system.createRoom(10);
        system.createRoom(2);
        System.out.println("Created rooms: " + r1 + ", " + r2);

        LocalDateTime t10 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime t11 = LocalDateTime.of(2026, 1, 1, 11, 0);
        LocalDateTime t12 = LocalDateTime.of(2026, 1, 1, 12, 0);

        String b1 = system.bookRoom(t10, t11, 3);   // should pick r1 (best-fit over r2)
        System.out.println("Booked: " + b1);

        String b2 = system.bookRoom(t11, t12, 3);   // back-to-back on r1 -- must succeed
        System.out.println("Booked (back-to-back): " + b2);

        try {
            system.bookRoom(t10.plusMinutes(30), t11.plusMinutes(30), 3); // overlaps b1's tail
            System.out.println("Unexpected success");
        } catch (NoRoomAvailableException e) {
            System.out.println("Overlap correctly rejected on r1, absorbed by r2 or failed: " + e.getMessage());
        }
    }
}

final class Rooms {
    private final String roomId;
    private final long capacity;

    Rooms(String roomId, long capacity) {
        this.roomId = roomId;
        this.capacity = capacity;
    }

    String getRoomId() { return roomId; }

    long getCapacity() { return capacity; }
}

final class Booking {
    private final String bookingId;
    private final String roomId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    Booking(String bookingId, String roomId, LocalDateTime startTime, LocalDateTime endTime){
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    String getBookingId() { return bookingId; }
    String getRoomId() { return roomId; }
    LocalDateTime getStartTime() { return startTime; }
    LocalDateTime getEndTime() { return endTime; }

    @Override
    public String toString() {
        return String.format("Booking[%s, room=%s, %s, %s)", bookingId, roomId, startTime, endTime);
    }
}

class InvalidBookingRequestException extends RuntimeException {
    InvalidBookingRequestException(String msg) { super(msg); }
}

class NoRoomAvailableException extends RuntimeException {
    NoRoomAvailableException(String msg) { super(msg); }
}

class RoomsNotFoundException extends RuntimeException {
    RoomsNotFoundException(String msg) { super(msg); }
}

class MeetingRoomBookingSystem {
    private final Map<String, Rooms> rooms = new ConcurrentHashMap<>();
    private final Map<String, TreeMap<LocalDateTime, Booking>> roomBookings = new ConcurrentHashMap<>();
    private final Map<String, Object> roomLocks = new ConcurrentHashMap<>();

    private final AtomicLong roomCounter = new AtomicLong(0);
    private final AtomicLong bookingCounter = new AtomicLong(0);

    String createRoom(long capacity) {
        if (capacity <= 0) {
            throw new InvalidBookingRequestException("Capacity must be a positive value");
        }
        String roomId = "ROOM-" + roomCounter.incrementAndGet();
        rooms.put(roomId, new Rooms(roomId, capacity));
        roomBookings.put(roomId, new TreeMap<>());
        roomLocks.put(roomId, new Object());
        return roomId;
    }

    String bookRoom(LocalDateTime startTime, LocalDateTime endTime, long requiredCapacity) {
        validateBookingRequest(startTime, endTime, requiredCapacity);

        List<Rooms> candidates = new ArrayList<>();
        for (Rooms r : rooms.values()) {
            if (r.getCapacity() >= requiredCapacity) candidates.add(r);
        }
        candidates.sort(Comparator.comparingLong(Rooms::getCapacity));

        if (candidates.isEmpty())
            throw new InvalidBookingRequestException("No room available with the mentioned capacity");

        for (Rooms room : candidates) {
            Object lock = roomLocks.get(room.getRoomId());
            synchronized (lock) {
                if (!hasOverlap(room.getRoomId(), startTime, endTime)){
                    String bookingId = "Booking-" + bookingCounter.incrementAndGet();
                    Booking booking = new Booking(bookingId, room.getRoomId(), startTime, endTime);
                    roomBookings.get(room.getRoomId()).put(startTime, booking);
                    return bookingId;
                }
            }
        }

        // all capacity-sufficient rooms are busy for this interval
        throw new NoRoomAvailableException(
                "All rooms with sufficient capacity are booked for [" + startTime + ", " + endTime + ")");
    }

    private void validateBookingRequest(LocalDateTime startTime, LocalDateTime endTime, long requiredCapacity) {
        if (startTime == null || endTime == null)
            throw new InvalidBookingRequestException("start/end time cannot be null");
        if (!startTime.isBefore(endTime))
            throw new InvalidBookingRequestException("start time must be before end time");
        if (requiredCapacity <= 0)
            throw new InvalidBookingRequestException("capacity must be positive value");
    }

    private boolean hasOverlap(String roomId, LocalDateTime start, LocalDateTime end) {
        TreeMap<LocalDateTime, Booking> bookings = roomBookings.get(roomId);
        if (bookings == null) throw new RoomsNotFoundException("No such room: " + roomId);

        // booking starting at or before `start`
        Map.Entry<LocalDateTime, Booking> prev = bookings.floorEntry(start);
        if (prev != null && prev.getValue().getEndTime().isAfter(start)) return true;

        // booking starting at or after `start`
        Map.Entry<LocalDateTime, Booking> next = bookings.ceilingEntry(start);
        if (next != null && next.getValue().getStartTime().isBefore(end)) return true;

        return false;
    }
}
