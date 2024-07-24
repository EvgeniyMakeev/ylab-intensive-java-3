package dev.makeev.coworking_service_app.service.implementation;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.dto.SpaceDTO;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.SpaceNotFoundException;
import dev.makeev.coworking_service_app.model.SlotsAvailableForBooking;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.WorkingHours;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.logging_time_starter.advice.annotations.LoggingTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code SpaceServiceImpl} class implements the {@link SpaceService} interface.
 * It provides methods to manage Spaces.
 */
@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceDAO spaceDAO;

    /**
     * {@inheritdoc}
     */
    @Override
    public void addSpace(SpaceAddDTO spaceAddDTO) throws SpaceAlreadyExistsException, SpaceNotFoundException {
        WorkingHours workingHours = new WorkingHours(spaceAddDTO.hourOfBeginningWorkingDay(), spaceAddDTO.hourOfEndingWorkingDay());

        if (spaceDAO.getSpaceByName(spaceAddDTO.name()).isPresent()) {
            throw new SpaceAlreadyExistsException();
        }

        LocalDate nowDate = LocalDate.now();
        Map<LocalDate, Map<Integer, Long>> bookingSlots = new HashMap<>();

        Map<Integer, Long> slots = new HashMap<>();
        long freeSlot = 0L;
        for (int i = spaceAddDTO.hourOfBeginningWorkingDay(); i < spaceAddDTO.hourOfEndingWorkingDay(); i++) {
            slots.put(i, freeSlot);
        }

        for (int i = 0; i < spaceAddDTO.numberOfDaysAvailableForBooking(); i++) {
            bookingSlots.put(nowDate.plusDays(i), slots);
        }

        spaceDAO.add(new Space(spaceAddDTO.name(), workingHours, bookingSlots));
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public List<SpaceDTO> getSpaces() {
        List<SpaceDTO> spaceDTOsList = new ArrayList<>();
        long freeSlot = 0L;

        spaceDAO.getNamesOfSpaces().forEach(s -> {
            List<SlotsAvailableForBooking> availableSlots = new ArrayList<>();
            spaceDAO.getSpaceByName(s).orElseThrow(SpaceNotFoundException::new).bookingSlots()
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(dateEntry -> {
                        List<String> slots = new ArrayList<>();
                        dateEntry.getValue().keySet().stream()
                                .filter(hour -> dateEntry.getValue().get(hour) == freeSlot)
                                .sorted(Comparator.naturalOrder())
                                .forEachOrdered(hour ->
                                        slots.add(String.format("%02d:00 - %02d:00", hour, hour + 1)));
                        availableSlots.add(new SlotsAvailableForBooking(dateEntry.getKey().toString(), slots));
                    });
            spaceDTOsList.add(new SpaceDTO(s, availableSlots));
        });

        return spaceDTOsList;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void deleteSpace(String nameOfSpace) throws SpaceNotFoundException {
        spaceDAO.getSpaceByName(nameOfSpace).orElseThrow(SpaceNotFoundException::new);
        spaceDAO.delete(nameOfSpace);
    }
}
