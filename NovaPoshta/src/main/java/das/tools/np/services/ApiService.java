package das.tools.np.services;

import das.tools.np.entity.response.AppResponse;

public interface ApiService {
    AppResponse getNewNumberResponse(String[] docs, String phone);

    AppResponse getExistedNumberResponse(String[] docs);
}
