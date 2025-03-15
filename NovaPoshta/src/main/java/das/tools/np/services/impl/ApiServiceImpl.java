package das.tools.np.services.impl;

import das.tools.np.entity.db.NumberToPhone;
import das.tools.np.entity.request.AppRequest;
import das.tools.np.entity.request.Document;
import das.tools.np.entity.request.MethodProperties;
import das.tools.np.entity.response.AppResponse;
import das.tools.np.gui.ApplicationLogService;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.repository.NumberToPhoneRepository;
import das.tools.np.services.ApiService;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ApiServiceImpl implements ApiService {
    private final RestTemplate restTemplate;
    private final ConfigService configService;
    private final ConversionService conversionService;
    private final CommonService commonService;
    private final NumberToPhoneRepository numberToPhoneRepository;
    private final ApplicationLogService logService;
    private final AlertService alertService;
    private final LocalizeResourcesService localizeService;

    public ApiServiceImpl(RestTemplate restTemplate, ConfigService configService, ConversionService conversionService, CommonService commonService, NumberToPhoneRepository numberToPhoneRepository, ApplicationLogService logService, AlertService alertService, LocalizeResourcesService localizeService) {
        this.restTemplate = restTemplate;
        this.configService = configService;
        this.conversionService = conversionService;
        this.commonService = commonService;
        this.numberToPhoneRepository = numberToPhoneRepository;
        this.logService = logService;
        this.alertService = alertService;
        this.localizeService = localizeService;
    }
    
    @Override
    public AppResponse getNewNumberResponse(String[] docs, String phone) {
        Document[] documents = conversionService.convert(docs, Document[].class);
        phone = (commonService.isNotEmpty(phone)) ? phone : configService.getConfigValue(ConfigService.CONFIG_PHONE_NUMBER_KEY);
        for (Document d : Objects.requireNonNull(documents)) {
            if (commonService.isNotEmpty(phone)) {
                d.setPhoneNumber(phone);
            } else {
                log.warn("The phone number is empty. Data for number '{}' will be got not fully completed", d.getDocumentNumber());
            }
        }
        return getResponse(documents);
    }

    @Override
    public AppResponse getExistedNumberResponse(String[] docs) {
        Document[] documents = conversionService.convert(docs, Document[].class);
        for (Document d : Objects.requireNonNull(documents)) {
            NumberToPhone numberToPhone = numberToPhoneRepository.findForNumber(d.getDocumentNumber());
            String phone = numberToPhone.getPhone();
            d.setPhoneNumber(phone);
        }
        return getResponse(documents);
    }

    private AppResponse getResponse(Document[] documents) {
        AppRequest request = AppRequest.builder()
                .methodProperties(
                        MethodProperties.builder()
                                .documents(documents)
                                .build())
                .build();
        AppResponse response = AppResponse.builder()
                .isSuccess(false)
                .build();
        try {
            CompletableFuture<ResponseEntity<AppResponse>> future = prepareFuture(request);
            ResponseEntity<AppResponse> resp = future.get();
            if (resp.getStatusCode() == HttpStatus.OK) {
                response = resp.getBody();
                if (log.isDebugEnabled()) log.debug("got response={}", response);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error getting response: ", e);
            logService.populateErrorMessage(localizeService.getLocalizedResource("alert.log.gettingResponse"));
            alertService.showError(localizeService.getLocalizedResource("alert.message.gettingResponse"), e.getLocalizedMessage());
            return response;
        }
        return response;
    }

    private CompletableFuture<ResponseEntity<AppResponse>> prepareFuture(AppRequest request) {
        String apiUrl = configService.getConfigValue(
                ConfigService.CONFIG_END_POINT_KEY);
        String url = ServletUriComponentsBuilder.fromHttpUrl(apiUrl)
                .toUriString();
        if (log.isDebugEnabled()) log.debug("sending request={} to url={}", request, url);
        return CompletableFuture.supplyAsync(() -> restTemplate.exchange(url,
                HttpMethod.POST,
                new HttpEntity<>(request, getHttpHeader()),
                AppResponse.class));
    }

    public HttpHeaders getHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}