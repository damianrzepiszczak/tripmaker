package rzepiszczak.damian.tripmaker.trip.management.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Destination {
    private String name;
    static Destination of(String destination) {
        return new Destination(destination);
    }
}
