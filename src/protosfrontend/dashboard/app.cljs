(ns dashboard.app
    (:require
      [re-frame.core :as rf]
      [baking-soda.core :as b]))


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
                      app-id (:id app)]
                [:div {:class "card-body"}
                    [:h4 {:class "card-title"} (:name app)]
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
                               [:td (:status app)]]]]]]
                        [b/Button {:color "danger"
                                    :on-click #(rf/dispatch [:remove-app app-id])} "Remove"]
                        [b/Button {:color "primary"
                                    :on-click #(rf/dispatch [:app-state app-id "stop"])} "Stop"]
                        [b/Button {:color "success"
                                    :on-click #(rf/dispatch [:app-state app-id "start"])} "Start"]
                        [b/Button {:color "primary"
                                    :on-click #(rf/dispatch [:get-app app-id])} "Refresh"]])]]])
