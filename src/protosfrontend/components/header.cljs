(ns components.header
    (:require
      [re-frame.core :as rf]))

(defn bar [name role]
  [:div.header.py-4
   [:div.container
    [:div.d-flex
     [:a.header-brand {:href "/"}
      [:img.header-brand-img {:alt "protos logo" :src "/static/images/protos-logo.svg"}]]
    (let [loading? @(rf/subscribe [:loading?])]
      (when loading?
        [:div {:class "sk-wave"}
          [:div {:class "sk-rect sk-rect1"}]
          [:div {:class "sk-rect sk-rect2"}]
          [:div {:class "sk-rect sk-rect3"}]
          [:div {:class "sk-rect sk-rect4"}]
          [:div {:class "sk-rect sk-rect5"}]]))

     [:div.d-flex.order-lg-2.ml-auto
      [:div.dropdown
       [:a.nav-link.pr-0.leading-none {:data-toggle "dropdown", :href "#"}
        [:span.avatar {:style {:background-image "url(/static/images/user-generic.png)"}}]
        [:span.ml-2.d-none.d-lg-block
         [:span.text-default name]
         [:small.text-muted.d-block.mt-1 role]]]
       [:div.dropdown-menu.dropdown-menu-right.dropdown-menu-arrow
        [:a.dropdown-item {:href "#/"}
         [:i.dropdown-icon.fe.fe-user]
         "Profile"]
        [:a.dropdown-item {:href "#/"}
         [:i.dropdown-icon.fe.fe-settings]
         "Settings"]
        [:div.dropdown-divider]
        [:a.dropdown-item {:href "#/" :on-click #(rf/dispatch [:logout])}
         [:i.dropdown-icon.fe.fe-log-out]
         "Sign out"]]]]
     [:a.header-toggler.d-lg-none.ml-3.ml-lg-0
      {:data-target "#headerMenuCollapse",
       :data-toggle "collapse",
       :href "#"}
      [:span.header-toggler-icon]]]]])

(defn menu [active-page]
  [:div {:class "header collapse d-lg-flex p-0", :id "headerMenuCollapse"}
    [:div {:class "container"}
      [:div {:class "row align-items-center"}
        [:div {:class "col-lg order-lg-first"}
          [:ul {:class "nav nav-tabs border-0 flex-column flex-lg-row"}
            [:li {:class "nav-item"}
              [:a {:href "#", :class (str "nav-link " (if (= active-page :dashboard-page) "active"))} [:i {:class "fe fe-home" }] " Home"]]
            [:li {:class "nav-item"}
              [:a {:href "#/apps", :class (str "nav-link " (if (= active-page :apps-page) "active"))} [:i {:class "fe fe-layers" }] " Apps"]]
            [:li {:class "nav-item"}
              [:a {:href "#/store", :class (str "nav-link " (if (= active-page :store-page) "active"))} [:i {:class "fe fe-shopping-bag" }] " Store"]]
            [:li {:class "nav-item"}
              [:a {:href "#/tasks", :class (str "nav-link " (if (= active-page :tasks-page) "active"))} [:i {:class "fe fe-check-circle" }] " Tasks"]]
            [:li {:class "nav-item"}
              [:a {:href "#/resources", :class (str "nav-link " (if (= active-page :resources-page) "active"))} [:i {:class "fe fe-git-merge" }] " Resources"]]]]]]])

(defn top [name role active-page]
  [:div
    [bar name role]
    [menu active-page]])


