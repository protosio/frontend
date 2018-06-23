(ns viewcomponents.sidebar)

(defn sidebar []
    [:nav {:class "sidebar sidebar-offcanvas", :id "sidebar"}
        [:ul {:class "nav"}
            [:li {:class "nav-item"}
                [:a {:class "nav-link", :href "#/"}
                    [:i {:class "menu-icon mdi mdi-television"}]
                    [:span {:class "menu-title"} "Dashboard"]]]
            [:li {:class "nav-item"}
            [:a {:class "nav-link", :href "#/apps"}
                [:i {:class "menu-icon mdi mdi-backup-restore"}]
                [:span {:class "menu-title"} "Apps"]]]
            [:li {:class "nav-item"}
            [:a {:class "nav-link", :href "#/installers"}
                [:i {:class "menu-icon mdi mdi-chart-line"}]
                [:span {:class "menu-title"} "Installers"]]]
            [:li {:class "nav-item"}
            [:a {:class "nav-link", :href "#/resources"}
                [:i {:class "menu-icon mdi mdi-table"}]
                [:span {:class "menu-title"} "Resources"]]]
            [:li {:class "nav-item"}
            [:a {:class "nav-link", :href "#/providers"}
                [:i {:class "menu-icon mdi mdi-sticker"}]
                [:span {:class "menu-title"} "Providers"]]]]])
