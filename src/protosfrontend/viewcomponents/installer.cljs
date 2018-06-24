(ns viewcomponents.installer
    (:require
      [re-frame.core :as rf]
      [baking-soda.core :as b]))

(defn installer-list []
  [:div {:class "col-lg-12 grid-margin stretch-card"}
    [:div {:class "card"}
      [:div {:class "card-body"}
        [:h4 {:class "card-title"} "Installers"]
        [:div {:class "table-responsive"}
          [:table {:class "table table-striped"}
            [:thead
              [:tr
                [:th "Name"]
                [:th "ID"]]]
            [:tbody
            (let [installers @(rf/subscribe [:installers])]
              (for [{Name :name, ID :id} (vals installers)]
                [:tr {:key ID :style {:width "100%"}}
                  [:td [:a {:href (str "/#/installers/" ID)} Name]]
                  [:td ID]]))]]]]]])

(defn installers-page []
    [:div {:class "row"}
        [installer-list]])


(defn installer-page [id]
    [:div {:class "row"}
        [:div {:class "col-lg-12 grid-margin stretch-card"}
            [:div {:class "card"}
                (let [installer @(rf/subscribe [:installer id])
                      metadata (get-in installer [:metadata])]
                [:div {:class "card-body"}
                    [:h4 {:class "card-title"} (:name installer)]
                    [:div.installer-details
                        [:div.table-responsive
                            [:table {:class "table table-striped table-bordered"}
                             [:tbody
                              [:tr
                                [:th "ID"]
                                [:td (:id installer)]]
                              [:tr
                               [:th "Name"]
                               [:td (:name installer)]]
                              [:tr
                               [:th "Description"]
                               [:td (-> metadata :description)]]
                              [:tr
                               [:th "Provides"]
                               [:td (clojure.string/join  " " (-> metadata :provides))]]]]]]
                    [b/Button {:color "danger" :on-click #(rf/dispatch [:remove-installer id])} "Remove"]
                    [b/Button {:color "primary" :on-click #(rf/dispatch [:open-modal :create-app-modal (:id installer)])} "Create app"]])]]])
