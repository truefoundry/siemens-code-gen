package ControlImplementation.Interface;

import Enums.ETile;

import java.util.List;

public interface ITileControl
{
    /**
     * Retrieves the names of all tiles currently available.
     *
     * @return A list of strings representing the names of all tiles.
     */
    List<String> getAllTilesName();

    /**
     * Checks if a specific tile exists.
     *
     * @param name The tile to check, represented by an enumeration of type ETile.
     * @return true if the specified tile exists, false otherwise.
     */
    boolean exists(ETile name);

    /**
     * Opens the specified tile.
     *
     * @param tile The tile to open, represented by an enumeration of type ETile.
     */
    void open(ETile tile);
}
