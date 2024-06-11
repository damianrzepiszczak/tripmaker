package rzepiszczak.damian.tripmaker.trip.management.infrastructure.settings;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rzepiszczak.damian.tripmaker.trip.management.application.domain.TripsSettings;

@Configuration
class TripsSettingsConfiguration {

    @Bean
    TripsSettings tripsSettings() {
        return new FromPropertiesTripsSettings();
    }
}