(ns dashboard.app
    (:require
      [re-frame.core :as rf]
      [protosfrontend.util :as util]))

(defn alert [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class "form-group"}
        [:div {:class "form-row text-center"}
          [:div {:class "col-12"}
            [:div {:class (str "alert alert-" (:type alert-data)) :role "alert"} (:message alert-data)]]]])))

(defn apps-page [title]
  [:div {:class "container"}
    (if title
      [:div.page-header [:h1.page-title title]])
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
       [:div {:class "card"}
        [:div {:class "table-responsive"}
          [:table {:class "table table-hover table-outline table-vcenter text-nowrap card-table"}
            [:thead
              [:tr
                [:th {:class "text-center w-1"}
                  [:i {:class "icon-people"}]]
                [:th "Name"]
                [:th "Last activity"]
                [:th {:class "text-center"} "Runtime"]
                [:th "Status"]
                [:th {:class "text-center"}
                  [:i {:class "icon-settings"}]]]]
            [:tbody
            (let [apps @(rf/subscribe [:apps])]
              (for [{name :name, id :id, status :status} (vals apps)]
              [:tr {:key id}
                [:td {:class "text-center"}
                  [:div {:class "avatar d-block" :style {:background-image "url(images/app-generic.png)"}}
                    [:span {:class "avatar-status bg-green"}]]]
                [:td
                  [:a {:href (str "/#/apps/" id)} name]
                  [:div {:class "small text-muted"} (str "ID: " id)]]
                [:td
                  [:div {:class "small text-muted"} "Application started"]
                  [:div "5 minutes ago"]]
                [:td {:class "text-center"}
                  [:i {:class "payment payment-visa"}]]
                [:td
                  [:div status]]
                [:td
                  [:div {:class "item-action dropdown"}
                    [:a {:href "javascript:void(0)", :data-toggle "dropdown", :class "icon"}
                      [:i {:class "fe fe-more-vertical"}]]
                    [:div {:class "dropdown-menu dropdown-menu-right"}
                      [:a {:href "javascript:void(0)", :class "dropdown-item"}
                        [:i {:class "dropdown-icon fe fe-tag"}]" Action "]
                      [:a {:href "javascript:void(0)", :class "dropdown-item"}
                        [:i {:class "dropdown-icon fe fe-edit-2"}]" Another action "]
                      [:a {:href "javascript:void(0)", :class "dropdown-item"}
                        [:i {:class "dropdown-icon fe fe-message-square"}]" Something else here"]
                      [:div {:class "dropdown-divider"}]
                      [:a {:href "javascript:void(0)", :class "dropdown-item"}
                        [:i {:class "dropdown-icon fe fe-link"}]" Separated link"]]]]]))]]]]]]])

(defn app-page [id]
  [:div {:class "row"}
    [:div {:class "col-lg-12 grid-margin stretch-card"}
      [:div {:class "card"}
        (let [apps @(rf/subscribe [:apps])
              app (get apps (keyword id))
              app-id (:id app)
              loading? @(rf/subscribe [:loading?])]
        [:div {:class "card-body"}
          [:h4 {:class "card-title"} (:name app) (when loading? [:i {:class "fa fa-spin fa-spinner"}])]
          [:div {:class "form-group"}
          [:div.app-details
              [:div.table-responsive
                  [:table {:class "table table-striped table-bordered"}
                   [:tbody
                    [:tr
                      [:th "ID"]
                      [:td (:id app)]]
                    [:tr
                      [:th "Name"]
                      [:td (:name app)]]
                    [:tr
                      [:th "Installer ID"]
                      [:td (:installer-id app)]]
                    [:tr
                     [:th "Status"]
                     [:td (:status app)]]]]]]]
          [:div {:class "form-group"}
          [:div {:class "col-12"}
              [util/submit-button "Start" [:app-state app-id "start"] "success" loading?]
              [util/submit-button "Stop" [:app-state app-id "stop"] "primary" loading?]
              [util/submit-button "Refresh" [:get-app app-id] "primary" loading?]
              [util/submit-button "Remove" [:remove-app app-id] "danger" loading?]]]
          [alert [:alert-dashboard]]])]]])


