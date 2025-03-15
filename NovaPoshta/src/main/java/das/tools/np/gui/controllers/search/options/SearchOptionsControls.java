package das.tools.np.gui.controllers.search.options;

import das.tools.np.entity.db.CargoStatus;
import das.tools.np.entity.db.Group;
import das.tools.np.entity.db.NumberType;
import das.tools.np.entity.db.Status;
import das.tools.np.entity.search.*;
import das.tools.np.entity.search.values.*;
import das.tools.np.repository.GroupRepository;
import das.tools.np.services.CargoStatusService;
import das.tools.np.services.CargoTypeService;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.tools.Borders;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("prototype")
@Slf4j
public class SearchOptionsControls {
    private SearchParams searchParams;
    private List<Node> controls;
    private boolean isRestoreParams;
    private final CargoStatusService statusService;
    private final CargoTypeService typeService;
    private final GroupRepository groupRepository;
    private final LocalizeResourcesService localizeService;

    public SearchOptionsControls(CargoStatusService statusService, CargoTypeService typeService, GroupRepository groupRepository, LocalizeResourcesService localizeService) {
        this.statusService = statusService;
        this.typeService = typeService;
        this.groupRepository = groupRepository;
        this.localizeService = localizeService;
    }

    public Node getOptionsBox(SearchParams params) {
        controls = new LinkedList<>();
        this.isRestoreParams = (params != null);
        if (isRestoreParams) {
            searchParams = params;
        } else {
            searchParams = getEmptyParams();
        }
        initControls();
        VBox vb = new VBox(5, controls.toArray(new Node[0]));
        vb.setAlignment(Pos.CENTER_LEFT);
        return Borders.wrap(vb)
                .lineBorder()
                .innerPadding(5)
                .radius(5)
                .thickness(1)
                .color(Color.BLACK)
                .buildAll();
    }

    public SearchParams getEmptyParams() {
        return SearchParams.builder()
                .create(SearchDateValue.builder().build())
                .schedule(SearchDateValue.builder().build())
                .weight(SearchIntValue.builder().build())
                .cost(SearchIntValue.builder().build())
                .group(SearchStringValue.builder().value("Default").build())
                .groupText(SearchStringValue.builder().build())
                .appStatus(new SearchTypeValue<>(false, CargoStatus.NEW, SearchCondition.EQUAL))
                .numberType(new SearchTypeValue<>(false, NumberType.UNDEF, SearchCondition.EQUAL))
                .status(new SearchTypeValue<>(false, Status.SELF_CREATED, SearchCondition.EQUAL))
                .price(SearchIntValue.builder().build())
                .seats(SearchIntValue.builder().build())
                .build();
    }

    private void initControls() {
        List<Group> allGroups = groupRepository.getAll();
        List<String> items = new ArrayList<>(allGroups.size());
        allGroups.forEach(s -> items.add(s.getName()));
        controls.add(
                Borders.wrap(
                    new VBox(5,
                            new StringValueBox(localizeService.getLocalizedResource("options.label.group"),
                                    items, isRestoreParams, searchParams.getGroup(), allGroups.get(0).getName()).getControlsBox(),
                            new StringTextBox(localizeService.getLocalizedResource("options.label.groupName"),
                                    isRestoreParams, searchParams.getGroupText(), "").getControlsBox()
                    )
                ).etchedBorder()
                    .innerPadding(5)
                    .radius(5)
                .buildAll()
        );

        controls.add(new IntegerValueBox(localizeService.getLocalizedResource("options.label.weight"),
                1000, 1, 1, isRestoreParams, searchParams.getWeight()).getControlsBox());
        controls.add(new IntegerValueBox(localizeService.getLocalizedResource("options.label.cost"),
                10000, 100, 10, isRestoreParams, searchParams.getCost()).getControlsBox());
        controls.add(new IntegerValueBox(localizeService.getLocalizedResource("options.label.price"),
                10000, 100, 10, isRestoreParams, searchParams.getPrice()).getControlsBox());
        controls.add(new IntegerValueBox(localizeService.getLocalizedResource("options.label.seats"),
                100, 1, 1, isRestoreParams, searchParams.getSeats()).getControlsBox());

        controls.add(new CargoStatusValueBox(localizeService.getLocalizedResource("options.label.numberStatus"),
                statusService.getStatuses(), searchParams.getAppStatus(), isRestoreParams, CargoStatus.NEW,
                new StringConverter<CargoStatus>() {
                    @Override
                    public String toString(CargoStatus cargoStatus) {
                        return statusService.getStatusName(cargoStatus);
                    }
                    @Override
                    public CargoStatus fromString(String s) {
                        return statusService.fromString(s);
                    }
                }).getControlsBox()
        );
        controls.add(new NumberTypeValueBox(localizeService.getLocalizedResource("options.label.numberType"),
                typeService.getTypes(), searchParams.getNumberType(), isRestoreParams, NumberType.UNDEF,
                new StringConverter<NumberType>() {
                    @Override
                    public String toString(NumberType numberType) {
                        return typeService.getTypeName(numberType);
                    }
                    @Override
                    public NumberType fromString(String s) {
                        return typeService.getTypeByName(s);
                    }
                }
        ).getControlsBox());
        controls.add(new TypedValueBox<Status>(localizeService.getLocalizedResource("options.label.status"), statusService.getAllStatusKeys(),
                        searchParams.getStatus(), isRestoreParams, Status.COMPLETED,
                        new StringConverter<Status>() {
                            @Override
                            public String toString(Status status) {
                                return status != null ? statusService.getStatusName(status) : "";
                            }
                            @Override
                            public Status fromString(String s) {
                                return statusService.getStatusFromString(s);
                            }
                        }
                ).getControlsBox()
        );

        controls.add(new DateValueBox(localizeService.getLocalizedResource("options.label.createDate"),
                isRestoreParams, searchParams.getCreate()).getControlsBox());
        controls.add(new DateValueBox(localizeService.getLocalizedResource("options.label.scheduledDate"),
                isRestoreParams, searchParams.getSchedule()).getControlsBox());
    }

    public SearchParams getSearchParams() {
        return searchParams;
    }
}
