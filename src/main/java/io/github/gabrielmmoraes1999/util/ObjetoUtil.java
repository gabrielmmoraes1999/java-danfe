package io.github.gabrielmmoraes1999.util;

import java.util.Collection;
import java.util.Optional;

public final class ObjetoUtil {

    private ObjetoUtil() {}

    public static <T> Optional<T> verifica(T obj) {
        if (obj == null)
            return Optional.empty();
        if (obj instanceof Collection)
            return ((Collection<?>) obj).isEmpty() ? Optional.empty() : Optional.of(obj);

        final String s = String.valueOf(obj).trim();

        return s.isEmpty() || s.equalsIgnoreCase("null") ? Optional.empty() : Optional.of(obj);
    }

}
