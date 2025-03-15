package das.tools.np.services.impl;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.gui.ApplicationLogService;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.repository.ArchiveRepository;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.services.ArchiveNumberService;
import das.tools.np.services.LocalizeResourcesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ArchiveNumberServiceImpl implements ArchiveNumberService {
    private final ArchiveRepository archiveRepository;
    private final CargoNumberRepository numberRepository;
    private final AlertService alertService;
    private final LocalizeResourcesService localizeService;
    private final ApplicationLogService logService;

    public ArchiveNumberServiceImpl(ArchiveRepository archiveRepository, CargoNumberRepository numberRepository, AlertService alertService, LocalizeResourcesService localizeService, ApplicationLogService logService) {
        this.archiveRepository = archiveRepository;
        this.numberRepository = numberRepository;
        this.alertService = alertService;
        this.localizeService = localizeService;
        this.logService = logService;
    }

    @Transactional(timeoutString = "${app.transaction.timeout.seconds}")
    public void moveNumberToArchive(CargoNumber number) {
        if (archiveRepository.isNumberExists(number.getNumber())) {
            alertService.showError(localizeService.getLocalizedResource("alert.archive.moveTo.header.numberExists"),
                    String.format(localizeService.getLocalizedResource("alert.archive.moveTo.content.numberExists"), number.getNumber()));
            return;
        }
        archiveRepository.add(number);
        numberRepository.delete(number);
        logService.populateInfoMessage(String.format(localizeService.getLocalizedResource("alert.log.number.movedToArchive"), number.getNumber()));
    }

    @Override
    public void moveNumberToArchive(String number) {
        CargoNumber cargoNumber = numberRepository.findByNumber(number);
        moveNumberToArchive(cargoNumber);
    }

    @Transactional(timeoutString = "${app.transaction.timeout.seconds}")
    public void restoreNumber(CargoNumber number) {
        if (numberRepository.isNumberExists(number.getNumber())) {
            alertService.showError(localizeService.getLocalizedResource("alert.archive.restore.header.numberExists"),
                    String.format(localizeService.getLocalizedResource("alert.archive.restore.content.numberExists"), number.getNumber()));
            return;
        }
        if (!archiveRepository.isNumberExists(number.getNumber())) {
            alertService.showError(localizeService.getLocalizedResource("alert.archive.restore.header.numberExists"),
                    String.format(localizeService.getLocalizedResource("alert.archive.restore.content.numberNotFoundInArchive"), number.getNumber()));
            return;
        }
        numberRepository.add(number);
        archiveRepository.delete(number);
        logService.populateInfoMessage(String.format(localizeService.getLocalizedResource("alert.log.number.restoredFromArchive"), number.getNumber()));
    }

    @Override
    public void restoreNumber(String number) {
        CargoNumber cargoNumber = archiveRepository.findByNumber(number);
        restoreNumber(cargoNumber);

    }

    public void deleteArchivedNumber(CargoNumber number) {
        boolean dialogResult = alertService.showConfirmDialog(localizeService.getLocalizedResource("alert.archive.title.confirmDelete"),
                String.format(localizeService.getLocalizedResource("alert.archive.header.confirmDelete"), number.getNumber()));
        if (dialogResult) {
            archiveRepository.delete(number);
        }
    }

    public void deleteArchivedNumber(String number) {
        CargoNumber cargoNumber = archiveRepository.findByNumber(number);
        deleteArchivedNumber(cargoNumber);
    }
}
