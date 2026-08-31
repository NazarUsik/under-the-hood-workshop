package menu

// MenuService interface: proxy and real service both implement this.
type MenuService interface {
	ListItems() []MenuItem
}

type menuServiceImpl struct {
	repo MenuRepository
}

func NewMenuService(repo MenuRepository) MenuService {
	return &menuServiceImpl{repo: repo}
}

func (s *menuServiceImpl) ListItems() []MenuItem {
	return s.repo.FindAll()
}
