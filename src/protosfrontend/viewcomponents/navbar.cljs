(ns viewcomponents.navbar
  (:require
    [re-frame.core :as rf]))

(defn user-info []
  (let [username @(rf/subscribe [:username])]
    (if username
      [:li {:class "nav-item dropdown d-none d-xl-inline-block"}
        [:a {:class "nav-link dropdown-toggle", :id "UserDropdown", :href "#", :data-toggle "dropdown", :aria-expanded "false"}
          [:span {:class "profile-text"} (str "Hello, " username " !")]
          [:img {:class "img-xs rounded-circle", :src "images/faces/face1.jpg", :alt "Profile image"}]]
        [:div {:class "dropdown-menu dropdown-menu-right navbar-dropdown", :aria-labelledby "UserDropdown"}
          [:a {:class "dropdown-item mt-2"} "Manage Account"]
          [:a {:class "dropdown-item"} "Change Password"]
          [:a {:class "dropdown-item" :on-click #(rf/dispatch [:logout])} "Sign Out"]]]
      [:li [:button {:on-click #(rf/dispatch [:open-modal :login-modal])} "Log in"]])))

(defn menu []
    [:nav {:class "navbar default-layout col-lg-12 col-12 p-0 fixed-top d-flex flex-row"}
      [:div {:class "text-center navbar-brand-wrapper d-flex align-items-top justify-content-center"}
        [:a {:class "navbar-brand brand-logo" :href "index.html"}
          [:img {:src "images/logo.svg" :alt "logo"} ]]
        [:a {:class "navbar-brand brand-logo-mini" :href "index.html"}
          [:img {:src "images/logo-mini.svg" :alt "logo"} ]]]
      [:div {:class "navbar-menu-wrapper d-flex align-items-center"}
        [:ul {:class "navbar-nav navbar-nav-right"}
          [:li {:class "nav-item dropdown"}
            [:a {:class "nav-link count-indicator dropdown-toggle", :id "notificationDropdown", :href "#", :data-toggle "dropdown"}
              [:i {:class "mdi mdi-bell"}]
              [:span {:class "count"} "1"]]
            [:div {:class "dropdown-menu dropdown-menu-right navbar-dropdown preview-list", :aria-labelledby "notificationDropdown"}
              [:a {:class "dropdown-item"}
              [:p {:class "mb-0 font-weight-normal float-left"} "You have 1 new notifications"]
              [:span {:class "badge badge-pill badge-warning float-right"} "View all"]]
              [:div {:class "dropdown-divider"}]
              [:a {:class "dropdown-item preview-item"}
              [:div {:class "preview-thumbnail"}
                [:div {:class "preview-icon bg-success"}
                [:i {:class "mdi mdi-alert-circle-outline mx-0"}]]]
              [:div {:class "preview-item-content"}
                [:h6 {:class "preview-subject font-weight-medium text-dark"} "Application Error"]
                [:p {:class "font-weight-light small-text"} "Just now"]]]]]
              [user-info]]
        [:button {:class "navbar-toggler navbar-toggler-right d-lg-none align-self-center", :type "button", :data-toggle "offcanvas"}
          [:span {:class "icon-menu"}]]]])
