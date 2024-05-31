package rzepiszczak.damian.tripmaker.trip.application.model.commands;

import rzepiszczak.damian.tripmaker.trip.application.model.TravelerId;

import java.time.LocalDateTime;

public record CreateNewTripCommand(TravelerId travelerId, String destination, LocalDateTime from, LocalDateTime to) {
}
