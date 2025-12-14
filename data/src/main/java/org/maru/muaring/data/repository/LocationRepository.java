package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.LocationRequestDTO;

public interface LocationRepository {
    void updateLocation(LocationRequestDTO request, Callback<Void> callback);
}
