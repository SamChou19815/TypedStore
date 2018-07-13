package typedstore;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
     * Delete a collection of keys efficiently.
     *
     * @param datastore datastore service used to delete.
     * @param keys keys to delete.
     */
    static void delete(@NotNull Datastore datastore, @NotNull Collection<Key> keys) {
        datastore.delete(keys.toArray(DUMMY_KEY_ARRAY));
    }
    
}
