(ns dashboard.app
    (:require
      [re-frame.core :as rf]
      [protosfrontend.util :as util]
      [baking-soda.core :as b]))

(defn alert [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class "form-group"}
        [:div {:class "form-row text-center"}
          [:div {:class "col-12"}
            [:div {:class (str "alert alert-" (:type alert-data)) :role "alert"} (:message alert-data)]]]])))

(defn app-list []
  [:div {:class "col-lg-12 grid-margin stretch-card"}
    [:div {:class "card"}
      [:div {:class "card-body"}
        [:h4 {:class "card-title"} "Apps"]
        [:div {:class "table-responsive"}
          [:table {:class "table table-striped"}
            [:thead
              [:tr
                [:th "Name"]
                [:th "ID"]
                [:th "Status"]]]
            [:tbody
            (let [apps @(rf/subscribe [:apps])]
              (for [{name :name, id :id, status :status} (vals apps)]
                [:tr {:key id :style {:width "100%"}}
                  [:td [:a {:href (str "/#/apps/" id)} name]]
                  [:td id]
                  [:td status]]))]]]]]])

(defn apps-page []
    [:div {:class "row"}
        [app-list]])

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
