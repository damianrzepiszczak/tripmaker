package rzepiszczak.damian.tripmaker.trip.application.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rzepiszczak.damian.tripmaker.common.AggregateRoot;
import rzepiszczak.damian.tripmaker.common.exception.DomainException;
import rzepiszczak.damian.tripmaker.trip.application.model.commands.DayInformation;
import rzepiszczak.damian.tripmaker.trip.application.model.events.*;

import java.time.LocalDate;
import java.util.*;

import static rzepiszczak.damian.tripmaker.trip.application.model.Trip.Stage.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Trip extends AggregateRoot<TripId> {

    enum Stage {INCOMING, STARTED, FINISHED, CANCELLED}

    @Getter
    private TravelerId travelerId;
    @Getter
    private Destination destination;
    private Period period;
    private Stage stage;
    private List<TripDay> timeline;

    Trip(TripId tripId, TravelerId travelerId, Destination destination, Period period) {
        this.id = tripId;
        this.destination = destination;
        this.period = period;
        this.travelerId = travelerId;
        stage = INCOMING;
        registerEvent(new TripCreated(id.getId()));
    }

    void start(LocalDate startedAt) {
        if (!canStart(startedAt)) {
            throw new DomainException("Cannot start trip, assign plan or check possible start date");
        }
        stage = STARTED;
        registerEvent(new TripStarted(id));
    }

    private boolean canStart(LocalDate now) {
        return (now.isBefore(period.getFrom()) || now.isEqual(period.getFrom()))
                && java.time.Period.between(now, period.getFrom()).getDays() <= 1
                && timeline != null;
    }

    void reschedule(Period newPeriod) {
        if (stage != INCOMING || newPeriod.howManyDays() != period.howManyDays()) {
            throw new DomainException("New Period has different amount of days");
        }
        this.period = newPeriod;
        if (timeline != null) {
            scheduleForPeriod(newPeriod);
        }
        registerEvent(new TripTimelineRescheduled(id));
    }

    private void scheduleForPeriod(Period period) {
        long amountOfDaysToSchedule = period.howManyDays();
        for (int dayNumber = 0; dayNumber < amountOfDaysToSchedule; dayNumber++) {
            timeline.get(dayNumber).changeDayDate(period.getFrom().plusDays(dayNumber));
        }
    }

    void finish() {
        if (stage != STARTED) {
            throw new DomainException("Cannot finish not started trip");
        }
        stage = FINISHED;
        registerEvent(new TripFinished(id));
    }

    void cancel() {
        if (stage == STARTED) {
            throw new DomainException("Cannot cancel started trip");
        }
        stage = CANCELLED;
        registerEvent(new TripCanceled(id));
    }

    void generateTimeline(Map<LocalDate, DayInformation> details) {
        timeline = new ArrayList<>();
        details.forEach((day, information) -> timeline.add(new TripDay(day, information.getNote(), information.getAttractions())));
        registerEvent(new TimelineCreated(id));
    }

    void modifyDayNote(LocalDate day, String note) {
        TripDay tripDay = getTripDay(day);
        tripDay.modifyNote(note);
    }

    void addNewAttraction(LocalDate day, String attraction) {
        if (stage != INCOMING) {
            throw new DomainException("Cannot add new attraction for started trip");
        }
        TripDay tripDay = getTripDay(day);
        tripDay.newDatAttraction(attraction);
    }

    private TripDay getTripDay(LocalDate day) {
        Optional<TripDay> found = timeline.stream().
                filter(tripDay -> tripDay.getDay().equals(day))
                .findFirst();
        return found.orElseThrow(() -> new DomainException("Cannot find " + day + " activity"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip trip)) return false;
        return id != null && Objects.equals(id, trip.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
