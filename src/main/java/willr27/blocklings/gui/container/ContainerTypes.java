package willr27.blocklings.gui.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import willr27.blocklings.Blocklings;

import java.util.ArrayList;
import java.util.List;

public class ContainerTypes
{
    public static final List<ContainerType<?>> CONTAINER_TYPES = new ArrayList<>();

    //public static final ContainerType<EquipmentContainer> EQUIPMENT_ID = addType("equipment", EquipmentContainer::new);

    private static <T extends Container> ContainerType<T> addType(String name, ContainerType.IFactory<T> factory)
    {
        ContainerType<T> type = new ContainerType<T>(factory);
        type.setRegistryName(Blocklings.MODID, name);
        CONTAINER_TYPES.add(type);
        return type;
    }
}
