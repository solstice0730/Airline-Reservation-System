package com.team1.airline.dao.impl;

import com.team1.airline.dao.AircraftDAO;
import com.team1.airline.entity.Aircraft;
import com.team1.airline.dao.impl.DataManager;

import java.util.List;
import java.util.stream.Collectors;

public class AircraftDAOImpl implements AircraftDAO {

    private String aircraftToLine(Aircraft aircraft) {
        return String.join(" ",
                aircraft.getAircraftId(),
                aircraft.getModelName(),
                String.valueOf(aircraft.getTotalSeats()),
                String.valueOf(aircraft.getEconomy()),
                String.valueOf(aircraft.getBusiness()));
    }

    @Override
    public void saveAircraft(Aircraft aircraft) {
        DataManager.getInstance().getAircrafts().add(aircraft);
    }

    @Override
    public Aircraft findByAircraftId(String aircraftId) {
        return DataManager.getInstance().getAircrafts().stream()
                .filter(a -> a.getAircraftId().equals(aircraftId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Aircraft> findAll() {
        return DataManager.getInstance().getAircrafts();
    }

    @Override
    public void updateAircraft(Aircraft aircraft) {
        List<Aircraft> aircrafts = DataManager.getInstance().getAircrafts();
        for (int i = 0; i < aircrafts.size(); i++) {
            if (aircrafts.get(i).getAircraftId().equals(aircraft.getAircraftId())) {
                aircrafts.set(i, aircraft);
                return;
            }
        }
    }

    @Override
    public void deleteAircraft(String aircraftId) {
        List<Aircraft> aircrafts = DataManager.getInstance().getAircrafts();
        aircrafts.removeIf(a -> a.getAircraftId().equals(aircraftId));
    }
}
