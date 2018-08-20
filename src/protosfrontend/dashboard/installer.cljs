(ns dashboard.installer
    (:require
      [re-frame.core :as rf]))

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
                      versions (keys (:versions installer))
                      selected-version (last (sort versions))
                      metadata (get-in installer [:versions selected-version])]
                [:div {:class "card-body"}
                  [:h2 {:class "card-title"} (:name installer)]
                    [:div {:class "form-group"}
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
                                   [:th "Versions"]
                                   [:td (for [version versions]
                                             [:button {:type "button" :key version :class (str "btn btn-sm btn-rounded submit-btn mr-1 "  (if (= selected-version version) "btn-inverse-dark" "btn-outline-dark"))} version])]]
                                  [:tr
                                   [:th "Description"]
                                   [:td (-> metadata :description)]]
                                  [:tr
                                   [:th "Provides"]
                                   [:td (clojure.string/join  " " (-> metadata :provides))]]]]]]]
                        [:div {:class "col-12"}
                          [:button {:type "button" :class "btn btn-rounded btn-rounded btn-outline-danger mr-1" :on-click #(rf/dispatch [:remove-installer id])} "Delete"]
                          [:button {:type "button" :class "btn btn-rounded btn-rounded btn-outline-primary mr-1" :on-click #(rf/dispatch [:remove-installer id])} "Create app"]]])]]])
