package das.tools.np.services.impl;

import das.tools.np.entity.db.*;
import das.tools.np.entity.response.AppResponse;
import das.tools.np.entity.response.ResponseData;
import das.tools.np.gui.ApplicationLogService;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.enums.FilteringMode;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.repository.ExtraPhonesRepository;
import das.tools.np.repository.GroupRepository;
import das.tools.np.repository.NumberToPhoneRepository;
import das.tools.np.services.ApiService;
import das.tools.np.services.CargoNumberService;
import das.tools.np.services.CargoStatusService;
import das.tools.np.services.CommonService;
import javafx.scene.control.TreeItem;
import javafx.scene.text.TextFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CargoNumberServiceImpl implements CargoNumberService {
    private final CargoNumberRepository numberRepository;
    private final GroupRepository groupRepository;
    private final ConversionService conversionService;
    private final ApiService apiService;
    private final ApplicationLogService logService;
    private final CargoStatusService statusService;
    private final AlertService alertService;
    private final LocalizeResourcesService localizeService;
    private final CommonService commonService;
    private final ExtraPhonesRepository extraPhonesRepository;
    private final NumberToPhoneRepository numberToPhoneRepository;

    public CargoNumberServiceImpl(CargoNumberRepository numberRepository, GroupRepository groupRepository, ConversionService conversionService, ApiService apiService, ApplicationLogService logService, CargoStatusService statusService, AlertService alertService, LocalizeResourcesService localizeService, CommonService commonService, ExtraPhonesRepository extraPhonesRepository, NumberToPhoneRepository numberToPhoneRepository) {
        this.numberRepository = numberRepository;
        this.groupRepository = groupRepository;
        this.conversionService = conversionService;
        this.apiService = apiService;
        this.logService = logService;
        this.statusService = statusService;
        this.alertService = alertService;
        this.localizeService = localizeService;
        this.commonService = commonService;
        this.extraPhonesRepository = extraPhonesRepository;
        this.numberToPhoneRepository = numberToPhoneRepository;
    }

    @Override
    public CargoNumber addOrUpdate(CargoNumber number, ExtraPhone extraPhone) {
        CargoNumber res;
        if (!numberRepository.isNumberExists(number.getNumber())) {
            res = numberRepository.add(number);
            numberToPhoneRepository.add(res.getNumber(), extraPhone.getPhone());
        } else {
            res = update(number);
        }
        return res;
    }

    @Override
    public CargoNumber update(CargoNumber number) {
        CargoNumber found = numberRepository.findByNumber(number.getNumber());
        if (found != null) {
            number.setId(found.getId());
            number.setGroup(found.getGroup());
            number.setNumberType(found.getNumberType());
            return numberRepository.update(number);
        } else {
            log.warn("The number {} doesn't updated", number.getNumber());
            return null;
        }
    }

    @Override
    public void reloadNumber(SimpleNumber number, List<SimpleNumber> list) {
        int index = findNumberIndex(number, list);
        if (index >= 0) {
            CargoNumber cargoNumber = numberRepository.findByNumber(number.getNumber());
            list.set(index, conversionService.convert(cargoNumber, SimpleNumber.class));
            if (log.isDebugEnabled()) log.debug("reloaded number={}", number.getNumber());
        }
    }

    private int findNumberIndex(SimpleNumber number, List<SimpleNumber> list) {
        int res = -1;
        String value = number.getNumber();
        int pos = -1;
        for (SimpleNumber n : list) {
            pos++;
            if (n.getNumber().equals(value)) {
                return pos;
            }
        }
        return res;
    }

    @Override
    public void updateNumberData(SimpleNumber number, List<SimpleNumber> list, TextFlow txLog) {
        AppResponse response = apiService.getExistedNumberResponse(new String[]{number.getNumber()});
        updateNumbersData(response, NumberType.UNDEF);
        logService.populateLogMessage(response, txLog);
        reloadNumber(number, list);
    }

    @Override
    public void updateNumberData(SimpleNumber number, TreeItem<String> item, TextFlow txLog) {
        AppResponse response = apiService.getExistedNumberResponse(new String[]{number.getNumber()});
        updateNumbersData(response, NumberType.UNDEF);
        logService.populateLogMessage(response, txLog);
    }

    @Override
    public void storeNumbersData(AppResponse response, NumberType numberType, String comment, String phone, boolean autoUpdate, Group group) {
        ResponseData[] responseData = response.getData();
        for (ResponseData data : responseData) {
            CargoNumber number = conversionService.convert(data, CargoNumber.class);
            if (number != null) {
                if (extraPhonesRepository.isPhoneAlreadyExists(phone)) {
                    ExtraPhone extraPhone = extraPhonesRepository.findByPhone(phone);
                    number.setAppStatus(statusService.getCargoStatus(number.getStatusCode()));
                    number.setFullData(data);
                    number.setNumberType(numberType);
                    number.setAutoUpdated(autoUpdate);
                    number.setGroup(group);
                    if (commonService.isNotEmpty(comment)) number.setComment(comment);
                    addOrUpdate(number, extraPhone);
                } else {
                    log.error("Couldn't find phone number '{}'", phone);
                }
            } else {
                log.error("got empty number for data={}", data);
            }
        }
    }

    @Override
    public void updateNumbersData(AppResponse response, NumberType numberType) {
        ResponseData[] responseData = response.getData();
        for (ResponseData data : responseData) {
            CargoNumber number = conversionService.convert(data, CargoNumber.class);
            if (number != null) {
                number.setAppStatus(statusService.getCargoStatus(number.getStatusCode()));
                number.setFullData(data);
                number.setNumberType(numberType);
                update(number);
            } else {
                log.error("got empty number for data={}", data);
            }
        }
    }

    @Override
    public void updateUncompleted(List<SimpleNumber> list, TextFlow txLog, boolean showDialog) {
        List<CargoNumber> numbers = numberRepository.findUncompleted();
        if (numbers.size() > 0) {
            String[] docs = new String[numbers.size()];
            for (int i = 0; i < numbers.size(); i++) {
                docs[i] = numbers.get(i).getNumber();
            }
            AppResponse response = apiService.getExistedNumberResponse(docs);
            updateNumbersData(response, NumberType.UNDEF);
            for (String n : docs) {
                CargoNumber cargoNumber = numberRepository.findByNumber(n);
                SimpleNumber simpleNumber = conversionService.convert(cargoNumber, SimpleNumber.class);
                reloadNumber(simpleNumber, list);
            }
            logService.populateLogMessage(response, txLog);
        } else {
            if (showDialog) {
                alertService.showInfo(localizeService.getLocalizedResource("alert.NoNumber.title"),
                        localizeService.getLocalizedResource("alert.NoNumber.message"));
            } else {
                logService.populateWarnMessage(localizeService.getLocalizedResource("alert.log.NoNumber.message"));
            }
        }
    }

    @Override
    public void moveNumberToGroup(SimpleNumber number, List<SimpleNumber> list, long newGroupId) {
        CargoNumber cargoNumber = numberRepository.findByNumber(number.getNumber());
        if (cargoNumber.getGroup().getId() != newGroupId) {
            Group group = groupRepository.findById(newGroupId);
            CargoNumber newNumber = numberRepository.moveToGroup(cargoNumber, group);
            SimpleNumber simpleNumber = conversionService.convert(newNumber, SimpleNumber.class);
            reloadNumber(simpleNumber, list);
            logService.populateInfoMessage(String.format(localizeService.getLocalizedResource("group.message.movedToGroup"), number.getNumber(), group.getName()));
        } else {
            alertService.showError(localizeService.getLocalizedResource("group.alert.cantMove.header"),
                    String.format(localizeService.getLocalizedResource("group.alert.cantMove.message"), number.getNumber()));
        }
    }

    @Override
    public List<CargoNumber> getAllFiltered(FilteringMode filter, Group group) {
        List<CargoNumber> numbers = null;
        final boolean isGroupUsed = group != null;
        switch (filter) {
            case ALL -> numbers = (isGroupUsed ? numberRepository.findAllByGroup(group) : numberRepository.findAll());
            case UNCOMPLETED -> numbers = (isGroupUsed ? numberRepository.findUncompleted(group) : numberRepository.findUncompleted());
            case INBOUND -> numbers = (isGroupUsed ? numberRepository.findAllByType(NumberType.IN, group) : numberRepository.findAllByType(NumberType.IN));
            case OUTBOUND -> numbers = (isGroupUsed ? numberRepository.findAllByType(NumberType.OUT, group) : numberRepository.findAllByType(NumberType.OUT));
        }
        return numbers;
    }

    @Override
    public List<CargoNumber> getAllFilteredSortedByUpdateDate(FilteringMode filter) {
        List<CargoNumber> numbers = null;
        switch (filter) {
            case ALL -> numbers = numberRepository.findAllSortedByUpdateDate();
            case UNCOMPLETED -> numbers = numberRepository.findUncompletedSortedByUpdateDate();
            case INBOUND -> numbers = numberRepository.findAllByTypeSortedByUpdateDate(NumberType.IN);
            case OUTBOUND -> numbers = numberRepository.findAllByTypeSortedByUpdateDate(NumberType.OUT);
        }
        return numbers;
    }

    @Override
    public List<CargoNumber> getAllFilteredSortedByCreateDate(FilteringMode filter) {
        List<CargoNumber> numbers = null;
        switch (filter) {
            case ALL -> numbers = numberRepository.findAllSortedByCreateDate();
            case UNCOMPLETED -> numbers = numberRepository.findUncompletedSortedByCreateDate();
            case INBOUND -> numbers = numberRepository.findAllByTypeSortedByCreateDate(NumberType.IN);
            case OUTBOUND -> numbers = numberRepository.findAllByTypeSortedByCreateDate(NumberType.OUT);
        }
        return numbers;
    }

    @Override
    public List<String> getNumbersUpdateDates(List<CargoNumber> numbers) {
        List<String> dates = new ArrayList<>(numbers.size());
        for (CargoNumber n : numbers) {
            String formatted = TREE_DATE_FORMAT.format(n.getUpdated());
            if (!dates.contains(formatted)) {
                dates.add(formatted);
            }
        }
        return dates;
    }

    @Override
    public List<String> getNumbersCreateDates(List<CargoNumber> numbers) {
        List<String> dates = new ArrayList<>(numbers.size());
        for (CargoNumber n : numbers) {
            String formatted = TREE_DATE_FORMAT.format(n.getCreated());
            if (!dates.contains(formatted)) {
                dates.add(formatted);
            }
        }
        return dates;
    }
}
