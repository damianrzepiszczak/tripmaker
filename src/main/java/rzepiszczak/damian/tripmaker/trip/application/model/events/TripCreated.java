package rzepiszczak.damian.tripmaker.trip.application.model.events;

import lombok.RequiredArgsConstructor;
import rzepiszczak.damian.tripmaker.common.event.DomainEvent;

@RequiredArgsConstructor
public class TripCreated implements DomainEvent {
    private final String referencePlanId;
}