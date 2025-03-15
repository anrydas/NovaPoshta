package das.tools.np.entity.plugin;

import das.tools.np.entity.db.CargoNumber;

import java.util.Arrays;
import java.util.List;

public class DemoPlugin implements PluginInterface{
    @Override
    public String getName() {
        return "Demo Plugin";
    }

    @Override
    public String getDescription() {
        return "The demo NP plugin";
    }

    @Override
    public String getNameUK() {
        return "Демонстрація";
    }

    @Override
    public String getDescriptionUK() {
        return "Демонстраційний плагін NP";
    }

    @Override
    public void doProcess(List<CargoNumber> list) {
        System.out.println("<--- Demo plugin started --->");
        System.out.println("    All numbers list as is:");
        String[] array = new String[list.size()];
        int i = 0;
        for (CargoNumber number : list) {
            System.out.printf("Current number is: %s%n", number.getNumber());
            array[i] = list.get(i).getNumber();
            i++;
        }
        System.out.println("    All numbers sorted by number:");
        Arrays.sort(array);
        for (String n : array) {
            System.out.printf("Current number is: %s%n", n);
        }
        System.out.println("---> Demo plugin finished <---");
    }
}
