package com.drivesmart.repository;

import com.drivesmart.entity.Booking;
import com.drivesmart.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByCarId(Long carId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByStartDateBetween(LocalDate start, LocalDate end);
    boolean existsByCarIdAndStatusIn(Long carId, List<BookingStatus> statuses);
    boolean existsByCarIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long carId,
            List<BookingStatus> statuses,
            LocalDate end,
            LocalDate start);
}
