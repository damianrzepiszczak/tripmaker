package rzepiszczak.damian.tripmaker.trip.management.application.commands;

import rzepiszczak.damian.tripmaker.trip.management.application.domain.TravelerId;

import java.time.LocalDate;

public record CreateNewTripCommand(TravelerId travelerId, String destination, LocalDate from, LocalDate to) {
}
