package com.team1.airline.dao;

import com.team1.airline.entity.Aircraft;
import java.util.List;

public interface AircraftDAO {
    void saveAircraft(Aircraft aircraft);
    Aircraft findByAircraftId(String aircraftId);
    List<Aircraft> findAll();
    void updateAircraft(Aircraft aircraft);
    void deleteAircraft(String aircraftId);
}
