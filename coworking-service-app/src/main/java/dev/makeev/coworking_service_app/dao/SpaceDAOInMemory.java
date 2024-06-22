package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.Space;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaceDAOInMemory implements SpaceDAO {

    private final Map<String, Space> mapOfSpaces = new HashMap<>();

    @Override
    public void add(Space newSpace) {
        mapOfSpaces.put(newSpace.name(), newSpace);
    }

    @Override
    public List<Space> getSpaces() {
        return mapOfSpaces.values().stream().toList();
    }

    @Override
    public Space getSpaceByName(String nameOfSpace) {
        return mapOfSpaces.get(nameOfSpace);
    }


    @Override
    public void delete(String nameOfSpace) {
        mapOfSpaces.remove(nameOfSpace);
    }
}
