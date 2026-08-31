package menu

type MenuService struct {
	repo MenuRepository
}

func NewMenuService(repo MenuRepository) *MenuService {
	return &MenuService{repo: repo}
}

func (s *MenuService) ListItems() []MenuItem {
	return s.repo.FindAll()
}
