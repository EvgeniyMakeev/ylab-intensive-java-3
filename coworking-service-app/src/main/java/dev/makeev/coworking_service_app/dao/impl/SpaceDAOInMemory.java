package dev.makeev.coworking_service_app.dao.impl;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.model.Space;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of the {@link SpaceDAO} interface.
 */
public class SpaceDAOInMemory implements SpaceDAO {

    private final Map<String, Space> mapOfSpaces = new HashMap<>();

    /**
     * {@inheritdoc}
     */
    @Override
    public void add(Space newSpace) {
        mapOfSpaces.put(newSpace.name(), newSpace);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<Space> getSpaces() {
        return mapOfSpaces.values().stream().toList();
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public Space getSpaceByName(String nameOfSpace) {
        return mapOfSpaces.get(nameOfSpace);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void delete(String nameOfSpace) {
        mapOfSpaces.remove(nameOfSpace);
    }
}
