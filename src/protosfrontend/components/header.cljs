(ns components.header
    (:require
      [re-frame.core :as rf]
      [protosfrontend.routes :as routes]
      [clairvoyant.core :refer-macros [trace-forms]]
      [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

(defn bar []
    (let [loading? @(rf/subscribe [:loading?])
          userinfo @(rf/subscribe [:userinfo])
          username (get userinfo :username)
          role     (get userinfo :role)]
  [:div {:class "header py-4"}
   [:div {:class "container"}
    [:div {:class "d-flex"}
     [:a {:class "header-brand" :href "/"}
      [:img {:class "header-brand-img" :alt "protos logo" :src "/static/images/protos-logo.svg"}]]
      (when loading?
        [:div {:class "sk-wave"}
          [:div {:class "sk-rect sk-rect1"}]
          [:div {:class "sk-rect sk-rect2"}]
          [:div {:class "sk-rect sk-rect3"}]
          [:div {:class "sk-rect sk-rect4"}]
          [:div {:class "sk-rect sk-rect5"}]])
     [:div {:class "d-flex order-lg-2 ml-auto"}
      [:div {:class "dropdown"}
       [:a {:class "nav-link pr-0 leading-none" :data-toggle "dropdown" :href "#"}
        [:span {:class "avatar" :style {:background-image "url(/static/images/user-generic.png)"}}]
        [:span {:class "ml-2 d-none d-lg-block"}
         [:span {:class "text-default"} username]
         [:small {:class "text-muted d-block mt-1"} role]]]
       [:div {:class "dropdown-menu dropdown-menu-right dropdown-menu-arrow"}
        [:a {:class "dropdown-item" :href "/ui/"}
         [:i {:class "dropdown-icon fe fe-user"}]
         "Profile"]
        [:a {:class "dropdown-item" :href "/ui/"}
         [:i {:class "dropdown-icon fe fe-settings"}]
         "Settings"]
        [:div {:class "dropdown-divider"}]
        [:a {:class "dropdown-item" :href "/ui/" :on-click #(rf/dispatch [:logout])}
         [:i {:class "dropdown-icon fe fe-log-out"}]
         "Sign out"]]]]
     [:a {:class "header-toggler d-lg-none ml-3 ml-lg-0"
          :data-target "#headerMenuCollapse"
          :data-toggle "collapse"
          :href "#"}
      [:span {:class "header-toggler-icon"}]]]]]))

(defn menu [active-page]
  [:div {:class "header collapse d-lg-flex p-0" :id "headerMenuCollapse"}
    [:div {:class "container"}
      [:div {:class "row align-items-center"}
        [:div {:class "col-lg order-lg-first"}
          [:ul {:class "nav nav-tabs border-0 flex-column flex-lg-row"}
            [:li {:class "nav-item"}
              [:a {:href (routes/url-for :dashboard-page) :class (str "nav-link " (if (= active-page :dashboard-page) "active"))} [:i {:class "fe fe-home" }] " Home"]]
            [:li {:class "nav-item"}
              [:a {:href (routes/url-for :apps-page) :class (str "nav-link " (if (some #(= active-page %) [:apps-page :app-page]) "active"))} [:i {:class "fe fe-layers" }] " Apps"]]
            [:li {:class "nav-item"}
              [:a {:href (routes/url-for :store-page) :class (str "nav-link " (if (some #(= active-page %) [:store-page :store-installer-page]) "active"))} [:i {:class "fe fe-shopping-bag" }] " Store"]]
            [:li {:class "nav-item"}
              [:a {:href (routes/url-for :tasks-page) :class (str "nav-link " (if (some #(= active-page %) [:tasks-page :task-page]) "active"))} [:i {:class "fe fe-check-circle" }] " Tasks"]]
            [:li {:class "nav-item"}
              [:a {:href (routes/url-for :resources-page) :class (str "nav-link " (if (some #(= active-page %) [:resources-page :resource-page]) "active"))} [:i {:class "fe fe-git-merge" }] " Resources"]]]]]]])

(defn top [active-page]
  [:div
    [bar]
    [menu active-page]])

)

