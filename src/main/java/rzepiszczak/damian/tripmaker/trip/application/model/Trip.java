package rzepiszczak.damian.tripmaker.trip.application.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rzepiszczak.damian.tripmaker.common.AggregateRoot;
import rzepiszczak.damian.tripmaker.common.exception.DomainException;
import rzepiszczak.damian.tripmaker.trip.application.model.events.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static rzepiszczak.damian.tripmaker.trip.application.model.Trip.Stage.*;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Trip extends AggregateRoot<TripId> {

    enum Stage {PLANNING, STARTED, FINISHED, CANCELLED}

    @Getter
    private TravelerId travelerId;
    @Getter
    private Destination destination;
    private Period period;
    private Stage stage = PLANNING;
    private Timeline timeline;

    Trip(TripId tripId, TravelerId travelerId, Destination destination, Period period) {
        this.id = tripId;
        this.destination = destination;
        this.period = period;
        this.travelerId = travelerId;
        registerEvent(new TripCreated(id.getId()));
    }

    void start(LocalDateTime startedAt) {
        if (!canStart(startedAt)) {
            throw new DomainException("Cannot start trip, assign plan or check possible start date");
        }
        stage = STARTED;
        registerEvent(new TripStarted(id));
    }

    private boolean canStart(LocalDateTime now) {
        return (now.isBefore(period.getFrom()) || now.isEqual(period.getFrom()))
                && Duration.between(now, period.getFrom()).toDays() <= 1
                && timeline != null;
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

    void share() {
        if (stage != FINISHED) {
            throw new DomainException("Cannot share not finished trip");
        }
        registerEvent(new TripShared(id));
    }

    void assign(Timeline timeline) {
        this.timeline = timeline;
        registerEvent(new TimelineCreated(id));
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
