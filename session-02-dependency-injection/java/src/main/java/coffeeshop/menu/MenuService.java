package coffeeshop.menu;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuRepository repository;

    public MenuService(MenuRepository repository) {
        this.repository = repository;
    }

    public List<MenuItem> listItems() {
        return repository.findAll();
    }
}
