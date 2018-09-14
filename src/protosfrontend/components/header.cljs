(ns components.header
    (:require
      [re-frame.core :as rf]))

(defn bar []
  [:div.header.py-4
   [:div.container
    [:div.d-flex
     [:a.header-brand {:href "/"}
      [:img.header-brand-img {:alt "protos logo" :src "images/protos-logo.svg"}]]
     [:div.d-flex.order-lg-2.ml-auto
      [:div.dropdown
       [:a.nav-link.pr-0.leading-none {:data-toggle "dropdown", :href "#"}
        [:span.avatar {:style {:background-image "url(images/generic.png)"}}]
        [:span.ml-2.d-none.d-lg-block
         [:span.text-default "Ionel Stonel"]
         [:small.text-muted.d-block.mt-1 "Administrator"]]]
       [:div.dropdown-menu.dropdown-menu-right.dropdown-menu-arrow
        [:a.dropdown-item {:href "#"}
         [:i.dropdown-icon.fe.fe-user]
         "Profile"]
        [:a.dropdown-item {:href "#"}
         [:i.dropdown-icon.fe.fe-settings]
         "Settings"]
        [:a.dropdown-item {:href "#"}
         [:span.float-right [:span.badge.badge-primary "6"]]
         [:i.dropdown-icon.fe.fe-mail]
         "Inbox"]
        [:a.dropdown-item {:href "#"}
         [:i.dropdown-icon.fe.fe-send]
         "Message"]
        [:div.dropdown-divider]
        [:a.dropdown-item {:href "#"}
         [:i.dropdown-icon.fe.fe-log-out]
         "Sign out"]]]]
     [:a.header-toggler.d-lg-none.ml-3.ml-lg-0
      {:data-target "#headerMenuCollapse",
       :data-toggle "collapse",
       :href "#"}
      [:span.header-toggler-icon]]]]])

(defn menu []
  [:div {:class "header collapse d-lg-flex p-0", :id "headerMenuCollapse"}
    [:div {:class "container"}
      [:div {:class "row align-items-center"}
        [:div {:class "col-lg order-lg-first"}
          [:ul {:class "nav nav-tabs border-0 flex-column flex-lg-row"}
            [:li {:class "nav-item"}
              [:a {:href "#", :class "nav-link active"} [:i {:class "fe fe-home" }] " Home"]]
            [:li {:class "nav-item"}
              [:a {:href "#", :class "nav-link"} [:i {:class "fe fe-layers" }] " Apps"]]
            [:li {:class "nav-item"}
              [:a {:href "#", :class "nav-link"} [:i {:class "fe fe-shopping-bag" }] " Store"]]
            [:li {:class "nav-item"}
              [:a {:href "#", :class "nav-link"} [:i {:class "fe fe-grid" }] " Resources"]]
            [:li {:class "nav-item"}
              [:a {:href "#", :class "nav-link"} [:i {:class "fe fe-box" }] " Providers"]]]]]]])

(defn top []
  [:div
    [bar]
    [menu]])


