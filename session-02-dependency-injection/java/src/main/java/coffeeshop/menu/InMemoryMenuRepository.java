package coffeeshop.menu;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryMenuRepository implements MenuRepository {

    private final List<MenuItem> items = List.of(
            new MenuItem(1, "Latte", 4.50),
            new MenuItem(2, "Espresso", 3.00),
            new MenuItem(3, "Cappuccino", 4.00),
            new MenuItem(4, "Americano", 3.50)
    );

    @Override
    public List<MenuItem> findAll() {
        return items;
    }
}
