(ns dashboard.resource
    (:require
      [re-frame.core :as rf]))

(defn resource-list []
  [:div {:class "col-lg-12 grid-margin stretch-card"}
    [:div {:class "card"}
      [:div {:class "card-body"}
        [:h4 {:class "card-title"} "Resources"]
        [:div {:class "table-responsive"}
          [:table {:class "table table-striped"}
            [:thead
              [:tr
                [:th "ID"]
                [:th "Status"]
                [:th "Type"]
                [:th "App"]]]
            [:tbody
          (let [resources @(rf/subscribe [:resources])]
            (for [{type :type, id :id, status :status, app-id :app} (vals resources)]
              [:tr {:key id :style {:width "100%"}}
                [:td [:a {:href (str "/#/resources/" id)} id]]
                [:td status]
                [:td type]
                [:td [:a {:href (str "/#/apps/" app-id)} app-id]]]))]]]]]])

(defn resources-page []
    [:div {:class "row"}
        [resource-list]])


(defn resource-page [id]
    [:div {:class "row"}
        [:div {:class "col-lg-12 grid-margin stretch-card"}
            [:div {:class "card"}
            (let [resources @(rf/subscribe [:resources])
                  resource (get resources (keyword id))
                  resource-id (:id resources)]
                [:div {:class "card-body"}
                  [:h4 {:class "card-title"} (:id resource)]
                  [:div.resource-details
                    [:div.row
                    [:div.col-md-12
                      [:div.table-responsive
                        [:table {:class "table table-striped table-bordered"}
                          [:tbody
                          [:tr
                            [:th "Type"]
                            [:td (:type resource)]]
                          [:tr
                            [:th "App"]
                            [:td (:app resource)]]
                          [:tr
                            [:th "Status"]
                            [:td (:status resource)]]]]]]]]])]]])
