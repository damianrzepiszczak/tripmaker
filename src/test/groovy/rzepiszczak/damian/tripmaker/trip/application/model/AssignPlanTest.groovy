package rzepiszczak.damian.tripmaker.trip.application.model

import rzepiszczak.damian.tripmaker.common.MockClock
import rzepiszczak.damian.tripmaker.trip.application.model.commands.AssignPlanCommand
import rzepiszczak.damian.tripmaker.trip.application.model.events.TimelineCreated
import rzepiszczak.damian.tripmaker.trip.application.model.events.TripCreated
import rzepiszczak.damian.tripmaker.trip.application.model.events.TripStarted
import rzepiszczak.damian.tripmaker.trip.infrastructure.TripPersistenceConfiguration
import spock.lang.Specification

import java.time.LocalDateTime

class AssignPlanTest extends Specification {

    private LocalDateTime someDay = LocalDateTime.of(2024, 5, 15, 0, 0)
    private TripPersistenceConfiguration configuration = new TripPersistenceConfiguration()
    private Trips trips = configuration.tripRepository()
    private TripService tripService = new TripFacade(trips, new MockClock(someDay), new TripFactory(trips))

    def 'create new timeline based on plan details'() {
        given: 'traveler want to organize Dubai trip based on sample plan details'
            TravelerId travelerId = TravelerId.from(UUID.randomUUID())
        and:
            tripService.create(travelerId, "Dubai", someDay, someDay.plusDays(1))
            Trip trip = trips.findTravelerTrips(travelerId)[0]
        when: 'assign plan'
            AssignPlanCommand assignPlanCommand = new AssignPlanCommand(trip.tripId)
            assignPlanCommand.addDetail(someDay, new AssignPlanCommand.DayInformation("First day activities", List.of("Deira")))
            tripService.assignPlan(assignPlanCommand)
        then: 'timeline was created and trip can be started'
            trip.start(someDay).isSuccessful()
            trip.events()*.class == [TripCreated, TimelineCreated, TripStarted]
    }
}