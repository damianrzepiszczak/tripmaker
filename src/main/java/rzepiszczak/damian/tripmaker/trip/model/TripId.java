package rzepiszczak.damian.tripmaker.trip.model;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class TripId {
    private final String id = UUID.randomUUID().toString();
}