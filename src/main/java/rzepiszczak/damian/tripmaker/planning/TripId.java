package rzepiszczak.damian.tripmaker.planning;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class TripId {
    private final String id = UUID.randomUUID().toString();
}
