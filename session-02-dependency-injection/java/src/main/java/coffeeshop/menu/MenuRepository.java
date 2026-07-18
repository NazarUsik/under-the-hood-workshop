package coffeeshop.menu;

import java.util.List;

public interface MenuRepository {

    List<MenuItem> findAll();
}
