package typedstore;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * {@code DatastoreVarargAdapter} is designed to be a vararg adapter for datastore operations.
 * This utility class is used to overcome Kotlin's inefficiency with vararg array copy.
 */
final class DatastoreVarargAdapter {
    
    /**
     * The dummy key array used for typed conversion.
     */
    @NotNull
    private static final Key[] DUMMY_KEY_ARRAY = {};
    /**
     * The dummy key array used for typed conversion.
     */
    @NotNull
    private static final Entity[] DUMMY_ENTITY_ARRAY = {};
    
    /**
     * Add a collection of entities to the database.
     *
     * @param datastore datastore service used to delete.
     * @param entities entities to add.
     * @return a list of added entities.
     */
    @NotNull
    static List<Entity> add(@NotNull Datastore datastore, @NotNull Collection<Entity> entities) {
        return datastore.add(entities.toArray(DUMMY_ENTITY_ARRAY));
    }
    
    /**
     * Put a collection of entities to the database.
     *
     * @param datastore datastore service used to delete.
     * @param entities entities to put.
     */
    static void put(@NotNull Datastore datastore, @NotNull Collection<Entity> entities) {
        datastore.put(entities.toArray(DUMMY_ENTITY_ARRAY));
    }
    
    /**
     * Delete a collection of keys efficiently.
     *
     * @param datastore datastore service used to delete.
     * @param keys keys to delete.
     */
    static void delete(@NotNull Datastore datastore, @NotNull Collection<Key> keys) {
        datastore.delete(keys.toArray(DUMMY_KEY_ARRAY));
    }
    
}
