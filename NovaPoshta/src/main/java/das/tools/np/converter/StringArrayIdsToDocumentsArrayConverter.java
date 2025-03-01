package das.tools.np.converter;

import das.tools.np.entity.request.Document;
import org.springframework.core.convert.converter.Converter;

public class StringArrayIdsToDocumentsArrayConverter implements Converter<String[], Document[]> {
    @Override
    public Document[] convert(String[] source) {
        Document[] docs = new Document[source.length];
        for (int i = 0; i < source.length; i++) {
            docs[i] = Document.builder()
                    .documentNumber(source[i])
                    .build();
        }
        return docs;
    }
}
