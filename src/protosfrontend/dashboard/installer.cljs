(ns dashboard.installer
    (:require
      [reagent.core :as r]
      [protosfrontend.util :as util]
      [reagent-forms.core :refer [bind-fields]]
      [re-frame.core :as rf]))

(defonce page-choice (r/atom "details"))

(defn alert [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class "form-group"}
        [:div {:class "form-row text-center"}
          [:div {:class "col-12"}
            [:div {:class (str "alert alert-" (:type alert-data)) :role "alert"} (:message alert-data)]]]])))

(defn input-field [properties]
  ^{:key (:id properties)} [:div {:class "form-group"}
    [:div {:class "input-group"}
      [:input properties]
      [:div {:class "input-group-append"}
        [:span {:class "input-group-text"}
          [:i {:class "mdi mdi-check-circle-outline"}]]]]])

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

(defn installer-details
  [installer]
  (let [versions (keys (:versions installer))
        selected-version (last (sort versions))
        metadata (get-in installer [:versions selected-version])]
  [:div.installer-details
    [:div {:class "form-group"}
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
             [:td (clojure.string/join  " " (-> metadata :provides))]]]]]]
    [:div {:class "col-12"}
      [:button {:type "button" :class "btn btn-rounded btn-rounded btn-outline-danger mr-1" :on-click #(rf/dispatch [:remove-installer (:id installer)])} "Delete"]
      [:button {:type "button" :class "btn btn-rounded btn-rounded btn-outline-primary mr-1" :on-click #(reset! page-choice "create-app")} "Create app"]]]))

(defn create-app
  [installer selected-version]
  [:div.create-app
    [:div {:class "auto-form-wrapper"}
      [bind-fields
        [:div {:class "col-9"}
          [input-field {:field :text :id :create-app.form.name :class "form-control" :placeholder "Name"}]]
        (util/form-events [:create-app-form])]
        (let [metadata (get-in installer [:versions selected-version])]
          (if-not (empty? (:params metadata))
            [:div.installer-params {:class "col-9"}
              [:div {:class "border-top my-3"}]
              [bind-fields
                (into [:div ] (for [field (:params metadata)]
                                   [input-field {:field :text :id (keyword (str "create-app.form.installer-params." field)) :class "form-control" :placeholder field}]))
                (util/form-events [:create-app-form])]]))
      (let [loading? @(rf/subscribe [:loading?])]
        [:div {:class "form-group"}
        [:div {:class "col-12"}
          [:button {:type "button" :class "btn btn-rounded btn-rounded btn-outline-danger mr-1" :on-click #(reset! page-choice "details")} "Cancel"]
          [util/submit-button-spinner "Create" [:create-app (:id installer) selected-version] "primary" loading? loading?]]])
      [alert [:alert-dashboard]]]])

(defn installer-page
    [id]
    [:div {:class "row"}
        [:div {:class "col-lg-12 grid-margin stretch-card"}
            [:div {:class "card"}
                (let [installer @(rf/subscribe [:installer id])
                      versions (keys (:versions installer))
                      selected-version (last (sort versions))]
                  [:div {:class "card-body"}
                    [:h2 {:class "card-title"} (:name installer)]
                    (if (= @page-choice "details")
                      [installer-details installer]
                      [create-app installer selected-version])])]]])
