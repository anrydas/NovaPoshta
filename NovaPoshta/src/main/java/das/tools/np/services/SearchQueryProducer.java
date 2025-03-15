package das.tools.np.services;

import das.tools.np.entity.search.SearchParams;

public interface SearchQueryProducer {
    String getQuery(SearchParams params);

    String getArchiveQuery(SearchParams params);
}
