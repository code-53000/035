package com.sailing.service;

import com.sailing.entity.SailingBooking;

public interface BookingValidationService {

    interface ValidationStep {
        void validate(SailingBooking booking);
    }

    void validateBooking(SailingBooking booking);
}
