(ns dashboard.installer
    (:require
      [reagent.core :as r]
      [protosfrontend.util :as util]
      [components.buttons :as buttons]
      [reagent-forms.core :refer [bind-fields]]
      [re-frame.core :as rf]))

(defonce page-choice (r/atom "details"))

(defn alert [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class (str "card-alert alert alert-" (:type alert-data) " alert-dismissible mb-0")}
        [:button {:type "button" :class "close" :data-dismiss "alert"}]
        (:message alert-data)])))

(defn installers-page [title]
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
                  [:th {:class "text-center"} "Runtime"]
                  [:th {:class "text-center"}
                    [:i {:class "icon-settings"}]]]]
              [:tbody
              (let [installers @(rf/subscribe [:installers])]
                (for [{name :name id :id} (vals installers)]
                [:tr {:key id}
                  [:td {:class "text-center"}
                    [:div {:class "avatar d-block bg-white" :style {:background-image "url(images/installer-generic.svg)" :background-size "80%"}}]]
                  [:td
                    [:a {:href (str "/#/installers/" id)} name]
                    [:div {:class "small text-muted"} (str "ID: " id)]]
                  [:td {:class "text-center"}
                    [:i {:class "tech tech-docker"}]]
                  [:td
                    [:div {:class "item-action dropdown"}
                      [:a {:href "javascript:void(0)" :data-toggle "dropdown" :class "icon"}
                        [:i {:class "fe fe-more-vertical"}]]
                      [:div {:class "dropdown-menu dropdown-menu-right"}
                        [:a {:href "javascript:void(0)" :class "dropdown-item"}
                          [:i {:class "dropdown-icon fe fe-stop-circle"}] " Create app"]
                        [:a {:href "javascript:void(0)" :class "dropdown-item"}
                          [:i {:class "dropdown-icon fe fe-trash"}] " Remove"]]]]]))]]]]]]])

(defn create-app [installer selected-version]
  [:div {:class "card-body"}
    [:div {:class "row justify-content-center"}
      [:div {:class "auto-form-wrapper col-md-6"}
        [bind-fields
          [:div
          [:div {:class "form-group"}
            [:label {:class "form-label"} "Name"]
            [:input {:field :text :id :create-app.form.name :class "form-control" :placeholder "Name"}]]]
          (util/form-events [:create-app-form])]]]
(let [metadata (get-in installer [:versions selected-version])]
  (if-not (empty? (:params metadata))
    [:div {:class "row justify-content-center"}
      [:div {:class "auto-form-wrapper col-md-6"}
        [:div {:class "border-top my-3"}]
        [bind-fields
          (into [:div ] (for [field (:params metadata)]
                             [:div {:class "form-group"}
                               [:label {:class "form-label"} field]
                               [:input {:field :text :id (keyword (str "create-app.form.installer-params." field)) :class "form-control" :placeholder field}]]))
          (util/form-events [:create-app-form])]]]))])

(defn installer-details [installer]
  (let [versions (keys (:versions installer))
        selected-version (last (sort versions))
        metadata (get-in installer [:versions selected-version])]
  [:div {:class "card-body"}
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "Description:"]]
      [:div {:class "col-5"} (-> metadata :description)]]
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "ID:"]]
      [:div {:class "col-5"} (:id installer)]]
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "Versions:"]]
      [:div {:class "col-5"} (for [version versions]
                                  [:button {:type "button" :key version :class (str "btn btn-sm mr-1 "  (if (= selected-version version) "btn-primary" "btn-secondary"))} version])]]
    [:div {:class "row mb-1"}
      [:div {:class "col-2"} [:strong "Provides:"]]
      [:div {:class "col-5"} (for [type (-> metadata :provides)]
                                  [:span {:key type :class "tag"} type])]]]))

(defn installer-page [id]
  [:div {:class "container"}
    [:div {:class "row row-cards row-deck"}
      [:div {:class "col-12"}
        (let [installer @(rf/subscribe [:installer id])
              versions (keys (:versions installer))
              selected-version (last (sort versions))
              loading? @(rf/subscribe [:loading?])]
        [:div {:class "card"}
          [:div {:class "card-header"}
            [:div {:class "avatar d-block bg-white mr-3" :style {:background-image "url(images/installer-generic.svg)" :background-size "80%"}}]
            [:h3 {:class "card-title"} (:name installer) (when loading? [:i {:class "fa fa-spin fa-circle-o-notch"}])]
            [:div {:class "card-options"}
              (if (= @page-choice "details")
              [:div {:class "btn-list"}
                [:button {:type "button" :class "btn btn-outline-primary btn-sm" :on-click #(reset! page-choice "create-app")} "Create app"]
                [:button {:type "button" :class "btn btn-danger btn-sm" :on-click #(rf/dispatch [:remove-installer (:id installer)])} "Remove"]]
              [:div {:class "btn-list"}
                [buttons/submit-button "Create" [:create-app (:id installer) selected-version] "primary btn-sm" loading?]
                [:button {:type "button" :class "btn btn-outline-danger btn-sm" :on-click #(reset! page-choice "details")} "Cancel"]]
              )]]
          [alert [:alert-dashboard]]
          (if (= @page-choice "details")
            [installer-details installer]
            [create-app installer selected-version])])]]])
